package com.popmain.droidmedia.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.audiofx.AcousticEchoCanceler;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.popmain.droidmedia.R;
import com.popmain.droidmedia.model.AudioConfig;
import com.popmain.droidmedia.model.Constant;
import com.popmain.droidmedia.util.AudioUtils;
import com.popmain.droidmedia.util.PathUtil;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
/**
 * * AudioRecord类，我们可以使用三种不同的read方法来完成录制工作，
 * 每种方法都有其实用的场合
 * 一、实例化一个AudioRecord类我们需要传入几种参数
 * 1、AudioSource(音频源：麦克风，系统内置音等等)：这里可以是MediaRecorder.AudioSource.MIC（麦克风源）
 * 2、SampleRateInHz:录制频率，可以为8000hz或者11025hz等，不同的硬件设备这个值不同
 * 3、ChannelConfig:录制通道，可以为AudioFormat.CHANNEL_CONFIGURATION_MONO（单声道)和AudioFormat.CHANNEL_CONFIGURATION_STEREO（多声道）
 * 4、AudioFormat:录制编码格式，可以为AudioFormat.ENCODING_16BIT和8BIT,其中16BIT的仿真性比8BIT好，但是需要消耗更多的电量和存储空间
 * 5、BufferSize:录制缓冲大小：可以通过getMinBufferSize来获取
 * 这样我们就可以实例化一个AudioRecord对象了
 * 二、创建一个文件，用于保存录制的内容
 * 同上篇
 * 三、打开一个输出流，指向创建的文件
 * DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(file)))
 * 四、现在就可以开始录制了，我们需要创建一个字节数组来存储从AudioRecorder中返回的音频数据，但是
 * 注意，我们定义的数组要小于定义AudioRecord时指定的那个BufferSize
 * short[]buffer = new short[BufferSize/4];
 * startRecording();
 * 然后一个循环，调用AudioRecord的read方法实现读取
 * 另外使用MediaPlayer是无法播放使用AudioRecord录制的音频的，为了实现播放，我们需要
 * 使用AudioTrack类来实现
 * AudioTrack类允许我们播放原始的音频数据
 *
 * * 一、实例化一个AudioTrack同样要传入几个参数
 * 1、StreamType:在AudioManager中有几个常量，其中一个是STREAM_MUSIC;

 这个在构造AudioTrack的第一个参数中使用。这个参数和Android中的AudioManager有关系，涉及到手机上的音频管理策略。

 Android将系统的声音分为以下几类常见的（未写全）：

 l         STREAM_ALARM：警告声

 l         STREAM_MUSCI：音乐声，例如music等

 l         STREAM_RING：铃声

 l         STREAM_SYSTEM：系统声音

 l         STREAM_VOCIE_CALL：电话声音
 * 2、SampleRateInHz：最好和AudioRecord使用的是同一个值
 * 3、ChannelConfig：同上
 * 4、AudioFormat：同上
 * 5、BufferSize：通过AudioTrack的静态方法getMinBufferSize来获取
 * 6、Mode：可以是AudioTrack.MODE_STREAM和MODE_STATIC，关于这两种不同之处，可以查阅文档
 * 二、打开一个输入流，指向刚刚录制内容保存的文件，然后开始播放，边读取边播放
 */

// todo :
// 1、【feature】后台录音、后台播放
// 2、【feature】降噪，去回声，增益，均衡 (原生：AcousticEchoCanceler，第三方： webrtc/speex)
// 3. 【优化】进制录音双开、进制播放双开、播放和录音互斥

public class AudioActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "AudioActivity";

    private static final String RECORD_DEBUG_TAG = "MyAudioRecord";

    private static final int PERMISSION_REQUEST_CODE_AUDIO = 0x01;


    private static final int MESSAGE_STOP_RECORD = 0x01;
    private static final int MESSAGE_CHANGE_DURATION = 0x02;
    private static final int MESSAGE_CHANGE_PROGRESS = 0x03;

    private static final String sAudioPath = "audio";
    private static final String sAudioFileName = "myaudio.pcm";
    private static final String sAudioWavFileName = "myaudio.wav";


    private Button mBtnStartRecord;
    private Button mBtnStopRecord;
    private ProgressBar mProgressBar;
    private TextView mTvDuration;
    private Button mBtnStartPauseTrack;
    private Button mBtnStackAudioFile;

    private long mDuration = 0;


    private Timer mTimer;
    private UpDurationTask mUpDurationTask;

    private File mAudioFile;
    private volatile boolean mIsRecording = false;
    private volatile boolean mIsTracking = true;

    private File mWavFile;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_STOP_RECORD:
                    stopAudioRecord();
                    break;
                case MESSAGE_CHANGE_DURATION:
                    updateDurationText();
                    break;
                case MESSAGE_CHANGE_PROGRESS:
                    int progress = msg.arg1;
                    updateProgress(progress);
                    break;
            }
        }
    };


    private void updateProgress(int progress) {
        mProgressBar.setProgress(progress);
    }


    // 更新时长view
    private void updateDurationText() {
        long minute = mDuration / 60;
        long second = mDuration % 60;
        String minuteStr = String.format("%02d", minute);
        String secondStr = String.format("%02d", second);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(minuteStr).append(":").append(secondStr);
        mTvDuration.setText(stringBuilder.toString());
    }

    // 更新时长任务
    private class UpDurationTask extends TimerTask {
        @Override
        public void run() {
            mDuration++;
            mHandler.sendEmptyMessage(MESSAGE_CHANGE_DURATION);
        }
    }

    // AudioRecord
    private AudioRecord mAudioRecord;
    // AudioTrack
    private AudioTrack mAudioTrack;
    // AcousticEchoCanceler
    private AcousticEchoCanceler mAcousticEchoCanceler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio);
        findView();
        init();
    }

    private void findView() {
        mBtnStartRecord = (Button) findViewById(R.id.btn_start_record);
        mBtnStopRecord = (Button) findViewById(R.id.btn_stop_record);
        mProgressBar = (ProgressBar) findViewById(R.id.pb_record);
        mTvDuration = (TextView) findViewById(R.id.tv_record_duration);
        mBtnStartPauseTrack = (Button) findViewById(R.id.btn_pause_start_track);
        mBtnStackAudioFile = (Button) findViewById(R.id.btn_track_file);
        mBtnStartRecord.setOnClickListener(this);
        mBtnStopRecord.setOnClickListener(this);
        mBtnStartPauseTrack.setOnClickListener(this);
        mBtnStackAudioFile.setOnClickListener(this);
    }

    private void init() {
        setUIStateStopAudioRecord();
        String appDir = PathUtil.getApplicationDir(getApplicationContext());
        if (!TextUtils.isEmpty(appDir)) {
            StringBuilder sb = new StringBuilder();
            sb.append(appDir).append(File.separator).append(sAudioPath).append(File.separator);
            File audioPath = new File(sb.toString());
            if (!audioPath.exists()) {
                audioPath.mkdirs();
            }
            mAudioFile = new File(sb.append(sAudioFileName).toString());
            mWavFile = new File(sb.append(sAudioWavFileName).toString());
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
        @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE_AUDIO) {
            int i = 0;
            for (String permission : permissions) {
                if (permission.equals(Manifest.permission.RECORD_AUDIO) && i < grantResults.length
                    && grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    startAudioRecord();
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mIsRecording = false;
        mHandler.removeCallbacksAndMessages(null);
        releaseRecordAndTrack();
        stopUpdateDurationTask();
    }

    private void releaseRecordAndTrack() {
        if (mAudioRecord != null) {
            mAudioRecord.release();
        }
        if (mAudioTrack != null) {
            mAudioTrack.release();
        }
    }

    private void stopUpdateDurationTask() {
        if (mUpDurationTask != null) {
            mUpDurationTask.cancel();
            mUpDurationTask = null;
        }
        if (mTimer != null) {
            mTimer.purge();
            mTimer.cancel();
            mTimer = null;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_start_record:
                if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                    startAudioRecord();
                } else {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO},
                            PERMISSION_REQUEST_CODE_AUDIO);
                }
                break;
            case R.id.btn_stop_record:
                stopAudioRecord();
                break;
            case R.id.btn_pause_start_track:
                if (mIsTracking) {
                    pauseTracking();
                } else {
                    startTracking();
                }
                break;
            case R.id.btn_track_file:
                startTrackFile();
                break;
        }
    }

    /**
     * 初始化消除回声
     */
    private void initAEC() {
        if (AudioUtils.isDeviceAcousticEchoCancelerSupport() && mAudioRecord != null) {
            Log.d(TAG, "isDeviceAcousticEchoCancelerSupport " + AudioUtils.isDeviceAcousticEchoCancelerSupport());
            // isDeviceAcousticEchoCancelerSupport已经判断版本
            mAcousticEchoCanceler = AcousticEchoCanceler.create(mAudioRecord.getAudioSessionId());
            mAcousticEchoCanceler.setEnabled(true);
        }
    }

    /**
     * 释放消除回声AcousticEchoCanceler
     */
    private void releaseACE() {
        if (mAcousticEchoCanceler != null) {
            mAcousticEchoCanceler.setEnabled(false);
            mAcousticEchoCanceler.release();
        }
    }

    /**
     * track file runnable, AudioTrack.MODE_STATIC
     */
    private Runnable mTrackingFileRunnable = new Runnable() {
        @Override
        public void run() {
            ByteArrayOutputStream bos = new ByteArrayOutputStream((int) mAudioFile.length());
            BufferedInputStream in = null;
            try {
                in = new BufferedInputStream(new FileInputStream(mAudioFile));
                int bufferSize = 1024;
                byte[] buffer = new byte[bufferSize];
                int len = 0;
                int rateOutHz = AudioConfig.IN.AUDIO_SAMPLE_RATE_IN_HZ;
                int trackChannelConfig = AudioConfig.OUT.AUDIO_TRACK_CHANNEL_CONFIG;
                int audioEncoding = AudioConfig.IN.AUDIO_ENCODING;
                while (-1 != (len = in.read(buffer, 0, bufferSize))) { // in --> buffer
                    bos.write(buffer, 0, len); // buffer --> ByteArrayOutputStream
                }
                byte[] fileData = bos.toByteArray();
                Log.d(RECORD_DEBUG_TAG, "FILE LEN " + fileData.length);
//                int trackBufferSize = AudioTrack.getMinBufferSize(sRateInHz, trackChannelConfig, audioEncoding);
                mAudioTrack = new AudioTrack(AudioConfig.OUT.AUDIO_TRACK_STREAM_TYPE, rateOutHz, trackChannelConfig, audioEncoding, fileData.length, AudioTrack.MODE_STATIC);
                //duration = (File.length / (频率 * 声道数 * 位数)) 由于频率是秒为单位，所以这里计算的结果也是秒为单位，乘以1000转毫秒
                final long duration = ((long)(fileData.length)) * 1000 / (AudioConfig.IN.AUDIO_SAMPLE_RATE_IN_HZ * 2 * AudioConfig.IN.AUDIO_ENCODING);
                Log.d(RECORD_DEBUG_TAG, "duration  " + duration);
                // 设置周期回调，以为单位帧，这里100ms更新一次
                mAudioTrack.setPositionNotificationPeriod(AudioConfig.IN.AUDIO_SAMPLE_RATE_IN_HZ / 10);
                mAudioTrack.setPlaybackPositionUpdateListener(new AudioTrack.OnPlaybackPositionUpdateListener() {
                    private long mProgress;
                    @Override
                    public void onMarkerReached(AudioTrack track) {

                    }

                    @Override
                    public void onPeriodicNotification(AudioTrack track) {
                        mProgress = mProgress + 100;
                        mDuration = mProgress / 1000;
                        float progressFloat = (mProgress / (float)duration + 0.05f); // 每次+100，精度为0.01
                        Log.d(RECORD_DEBUG_TAG, "track progress " + progressFloat + " track duration " + mDuration);
                        mHandler.sendEmptyMessage(MESSAGE_CHANGE_DURATION);
                        mHandler.sendMessage(Message.obtain(mHandler, MESSAGE_CHANGE_PROGRESS, (int) (progressFloat * 100), 0));
                    }
                });
                mAudioTrack.write(fileData, 0, fileData.length);
                mAudioTrack.play();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException ioe) {
                try {
                    if (in != null)
                        in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    if (bos != null)
                        bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            FileOutputStream wavFos = null;
            FileInputStream wavFis = null;
            // 播放完生成wav文件
            Log.d(RECORD_DEBUG_TAG, "begin save wav");
            if (mWavFile.exists()) {
                mWavFile.delete();
                Log.d(RECORD_DEBUG_TAG, "delete old wav file");
            }
            try {
                wavFos = new FileOutputStream(mWavFile);
                wavFis = new FileInputStream(mAudioFile);
                // audio len
                long audioLen = wavFis.getChannel().size();
                // 包括rtff和wave,44 - 4 -4 = 36
                long totalLen =  + 36;
                int channel = 2;
                long byteRate = 16 * AudioConfig.IN.AUDIO_SAMPLE_RATE_IN_HZ * channel / 8;
                byte[] data = new byte[1024];
                // 写wav头
                writeWavHeader(wavFos, audioLen, totalLen, AudioConfig.IN.AUDIO_SAMPLE_RATE_IN_HZ, channel, byteRate);
                while (wavFis.read(data) != -1) {
                    wavFos.write(data);
                }
                Log.d(RECORD_DEBUG_TAG, "end save wav file");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (wavFis != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (wavFos != null) {
                    try {
                        wavFos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

        }
    };
    /**
     * 播放保存的录音文件 PCM格式
     */
    private void startTrackFile() {
        if (mAudioFile == null || !mAudioFile.exists()) {
            Log.d(RECORD_DEBUG_TAG, "file not found");
            return;
        }
        Thread thread = new Thread(mTrackingFileRunnable);
        thread.start();
    }

    private void pauseTracking() {
        mIsTracking = false;
        mBtnStartPauseTrack.setText("开始播放【AudioTrack】");
        if (mAudioTrack != null) {
            mAudioTrack.pause();
        }
    }

    private void startTracking() {
        mIsTracking = true;
        mBtnStartPauseTrack.setText("暂停播放【AudioTrack】");
        if (mAudioTrack != null) {
            mAudioTrack.play();
        }
    }

    /**
     * 录音开始
     */
    private void startAudioRecord() {
        if (mAudioFile.exists()) {
            mAudioFile.delete();
        }
        setUIStateDuringAudioRecord();
        // 线程池 ?
        Thread thread = new Thread(mRecordRunnable);
        thread.start();
    }

    private void stopAudioRecord() {
        if (mIsRecording) {
            mIsRecording = false;
        }
        setUIStateStopAudioRecord();
    }

    private void setUIStateDuringAudioRecord() {
        mBtnStartRecord.setEnabled(false);
        mBtnStopRecord.setEnabled(true);
    }

    private void setUIStateStopAudioRecord() {
        mBtnStartRecord.setEnabled(true);
        mBtnStopRecord.setEnabled(false);
    }

    // @CORE 录音核心: 实现边录边播
    private Runnable mRecordRunnable = new Runnable() {
        @Override
        public void run() {
            DataOutputStream dos = null;
//          BufferedOutputStream bos = null;
            try {
                dos = new DataOutputStream(new FileOutputStream(mAudioFile));
//            bos = new BufferedOutputStream(new FileOutputStream(mAudioFile));
                int recordResult;
                int rateInHz = AudioConfig.IN.AUDIO_SAMPLE_RATE_IN_HZ; // 输入输出的频率
                int recordChannelConfig = AudioConfig.IN.AUDIO_CHANNEL_CONFIG; // CHANNEL_IN_STEREO双声道输入 CHANNEL_IN_MONO单声道
                int audioEncoding = AudioConfig.IN.AUDIO_ENCODING; // 输入与输出的位数
                int recordBufferSize = AudioConfig.IN.getMinBufferSize(); // 输入的buffer大小
                int trackChannelConfig = AudioConfig.OUT.AUDIO_TRACK_CHANNEL_CONFIG; // CHANNEL_OUT_STEREO双声道输出  CHANNEL_OUT_MONO单声道
                int trackBufferSize = AudioConfig.OUT.getMinBufferSize(); // 输出的buffer大小,这里用AudioTrack单独计算，如果是边录边播，可以用录制（AudioRecord）buffer的大小，减小延迟
                Log.d(RECORD_DEBUG_TAG, "Record Buffer Size  " + recordBufferSize + "    Track Buffer Size  " + trackBufferSize);
                if (AudioUtils.isDeviceAcousticEchoCancelerSupport()) {
                    mAudioRecord = new AudioRecord(AudioConfig.IN.AUDIO_RESOURCE_CANCELER, rateInHz, recordChannelConfig, audioEncoding, recordBufferSize);
                } else {
                    mAudioRecord = new AudioRecord(AudioConfig.IN.AUDIO_RESOURCE, rateInHz, recordChannelConfig, audioEncoding, recordBufferSize);
                }
                initAEC();
                if (AudioUtils.isDeviceAcousticEchoCancelerSupport()) {
                    // isDeviceAcousticEchoCancelerSupport已经判断版本
                    mAudioTrack = new AudioTrack(AudioConfig.OUT.AUDIO_TRACK_STREAM_TYPE, rateInHz, trackChannelConfig, audioEncoding, recordBufferSize, AudioTrack.MODE_STREAM, mAudioRecord.getAudioSessionId());
                } else {
                    mAudioTrack = new AudioTrack(AudioConfig.OUT.AUDIO_TRACK_STREAM_TYPE, rateInHz, trackChannelConfig, audioEncoding, recordBufferSize, AudioTrack.MODE_STREAM);
                }
                if (mIsTracking) {
                    mAudioTrack.play();
                }
                if (mAudioRecord.getState() == AudioRecord.STATE_INITIALIZED) {
                    mAudioRecord.startRecording();
                    byte[] tempBuffer = new byte[recordBufferSize];
                    mIsRecording = true;
                    mDuration = 0;
                    mHandler.sendEmptyMessage(MESSAGE_CHANGE_DURATION);
                    mTimer = new Timer();
                    mUpDurationTask = new UpDurationTask();
                    mTimer.schedule(mUpDurationTask, Constant.Time.SECOND, Constant.Time.SECOND);
                    while (mIsRecording) {
                        // 如果是录音状态
                        Log.d(RECORD_DEBUG_TAG, "start recording");
                        // AudioRecord中读取录音数据放置tempBuffer中
                        recordResult = mAudioRecord.read(tempBuffer, 0, recordBufferSize);
                        if (recordResult == AudioRecord.ERROR_INVALID_OPERATION || recordResult == AudioRecord.ERROR_BAD_VALUE) {
                            Log.e(RECORD_DEBUG_TAG, "recordResult error");
                            continue;
                        }
                        Log.d(RECORD_DEBUG_TAG, "recordResult length  " + recordResult);
                        if (recordResult > 0) {
//                        for (int i = 0; i < tempBuffer.length; i++) {
//                            // bug: pcm文件异常 (dos = DataOutputStream)
//                            dos.writeShort(tempBuffer[i]);
//                        }
                            dos.write(tempBuffer, 0, tempBuffer.length);
                            // 写入录音数据到输出流
//                        bos.write(tempBuffer, 0, tempBuffer.length);
                            // 把每次录的音频数据写到AudioTrack里, 录多少写多少
                            if (mIsTracking) {
                                mAudioTrack.write(tempBuffer, 0, recordBufferSize);
                            }
                        }
                    }
                    Log.d(RECORD_DEBUG_TAG, "stop recording");
                }
                releaseACE();
            } catch (FileNotFoundException e) {
                Log.d(RECORD_DEBUG_TAG, "FileNotFoundException " + mAudioFile.getAbsolutePath());
                e.printStackTrace();
            } catch (IOException e) {
                Log.d(RECORD_DEBUG_TAG, "IOException");
                e.printStackTrace();
            } finally {
                releaseRecordAndTrack();
                if (mUpDurationTask != null) {
                    mUpDurationTask.cancel();
                }
                if (mTimer != null) {
                    mTimer.purge();
                    mTimer.cancel();
                }
                mHandler.sendEmptyMessage(MESSAGE_STOP_RECORD);
                if (dos != null) {
                    try {
                        dos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    };


    // @CORE
    /**
     * 写wav头
     * @param out 输出流
     * @param totalAudioLen 音频大小
     * @param totalDataLen 数据大小（加上head，不包过riff&wave）
     * @param longSampleRate 频率
     * @param channels 通道
     * @param byteRate 位数
     * @throws IOException
     */
    private void writeWavHeader(FileOutputStream out, long totalAudioLen, long totalDataLen, long longSampleRate,
                                int channels, long byteRate) throws IOException{
        byte[] header = new byte[44];
        header[0] = 'R'; // RIFF
        header[1] = 'I';
        header[2] = 'F';
        header[3] = 'F';
        header[4] = (byte) (totalDataLen & 0xff);//数据大小
        header[5] = (byte) ((totalDataLen >> 8) & 0xff);
        header[6] = (byte) ((totalDataLen >> 16) & 0xff);
        header[7] = (byte) ((totalDataLen >> 24) & 0xff);
        header[8] = 'W';//WAVE
        header[9] = 'A';
        header[10] = 'V';
        header[11] = 'E';
        //FMT Chunk
        header[12] = 'f'; // 'fmt '
        header[13] = 'm';
        header[14] = 't';
        header[15] = ' ';//过渡字节
        //数据大小
        header[16] = 16; // 4 bytes: size of 'fmt ' chunk
        header[17] = 0;
        header[18] = 0;
        header[19] = 0;
        //编码方式 10H为PCM编码格式
        header[20] = 1; // format = 1
        header[21] = 0;
        //通道数
        header[22] = (byte) channels;
        header[23] = 0;
        //采样率，每个通道的播放速度
        header[24] = (byte) (longSampleRate & 0xff);
        header[25] = (byte) ((longSampleRate >> 8) & 0xff);
        header[26] = (byte) ((longSampleRate >> 16) & 0xff);
        header[27] = (byte) ((longSampleRate >> 24) & 0xff);
        //音频数据传送速率,采样率*通道数*采样深度/8
        header[28] = (byte) (byteRate & 0xff);
        header[29] = (byte) ((byteRate >> 8) & 0xff);
        header[30] = (byte) ((byteRate >> 16) & 0xff);
        header[31] = (byte) ((byteRate >> 24) & 0xff);
        // 确定系统一次要处理多少个这样字节的数据，确定缓冲区，通道数*采样位数
        header[32] = (byte) (1 * 16 / 8);
        header[33] = 0;
        //每个样本的数据位数
        header[34] = 16;
        header[35] = 0;
        //Data chunk
        header[36] = 'd';//data
        header[37] = 'a';
        header[38] = 't';
        header[39] = 'a';
        header[40] = (byte) (totalAudioLen & 0xff);
        header[41] = (byte) ((totalAudioLen >> 8) & 0xff);
        header[42] = (byte) ((totalAudioLen >> 16) & 0xff);
        header[43] = (byte) ((totalAudioLen >> 24) & 0xff);
        out.write(header, 0, 44);
    }
}
