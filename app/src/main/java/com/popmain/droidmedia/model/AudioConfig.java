package com.popmain.droidmedia.model;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;

/**
 * Created by paomian on 2017/11/26.
 * 音频配置
 */

public class AudioConfig {
    // 输入配置
    public static final class IN {
        public static final int AUDIO_RESOURCE = MediaRecorder.AudioSource.MIC;
        public static final int AUDIO_RESOURCE_CANCELER = MediaRecorder.AudioSource.VOICE_COMMUNICATION;
        public static final int AUDIO_SAMPLE_RATE_IN_HZ = 44100; // 输入输出的频率
        public static final int AUDIO_CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_STEREO; // CHANNEL_IN_STEREO双声道输入 CHANNEL_IN_MONO单声道
        public static final int AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT; // 输入与输出的位数
        public static int getMinBufferSize() {
            return AudioRecord.getMinBufferSize(AUDIO_SAMPLE_RATE_IN_HZ, AUDIO_CHANNEL_CONFIG, AUDIO_ENCODING);
        }
    }

    // 输出配置
    public static final class OUT {
        public static final int AUDIO_TRACK_STREAM_TYPE = AudioManager.STREAM_MUSIC;
//        public static final int AUDIO_TRACK_STREAM_CANCELER_TYPE = AudioManager.STREAM_VOICE_CALL;
        public static final int AUDIO_TRACK_CHANNEL_CONFIG = AudioFormat.CHANNEL_OUT_STEREO;
        public static final int AUDIO_TRACK_SAMPLE_RATE_IN_HZ = IN.AUDIO_SAMPLE_RATE_IN_HZ;
        public static final int AUDIO_TRACK_ENCODING = IN.AUDIO_ENCODING;
        public static int getMinBufferSize() {
            return AudioTrack.getMinBufferSize(AUDIO_TRACK_SAMPLE_RATE_IN_HZ, AUDIO_TRACK_CHANNEL_CONFIG, AUDIO_TRACK_ENCODING);
        }
    }
}
