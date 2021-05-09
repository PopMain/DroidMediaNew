package com.popmain.droidmedia.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.popmain.droidmedia.R;
import com.popmain.droidmedia.util.BitmapLruCacheMemoryReuse;
import com.popmain.droidmedia.util.BitmapSizeReduce;

public class DrawImageActivity extends AppCompatActivity implements View.OnClickListener {
    ImageView ivImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw_image);
        ivImage = findViewById(R.id.ivGlide);
//        Glide.with(this)
//                .load(R.drawable.image)
//                .centerCrop()
//                .into(ivGlide);
        findViewById(R.id.btnBitmapReused).setOnClickListener(this);
        findViewById(R.id.btnBitmapSize).setOnClickListener(this);
        findViewById(R.id.btnBitmapReduce).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnBitmapSize:
                showBitmapMemory();
                break;
            case R.id.btnBitmapReduce:
                bitmapSizeReduce();
                break;
            case R.id.btnBitmapReused:
                memoryCache();
                break;
            default:
                break;
        }
    }


    /**
     * 图像缓存
     */
    private void memoryCache() {

        // 第一次从 LruCache 内存中获取 Bitmap 数据
        Bitmap bitmap = BitmapLruCacheMemoryReuse.getInstance().getBitmapFromLruCache(R.drawable.image + "");

        /*
            如果从内存中获取 Bitmap 对象失败 , 这里就需要创建该图片 , 并放入 LruCache 内存中
         */
        if(bitmap == null){
            // 要复用内存的 Bitmap 对象 , 将新的 Bitmap 写入到该 Bitmap 内存中
            Bitmap inBitmap;
            // 尝试获取复用对象
            inBitmap = BitmapLruCacheMemoryReuse.getInstance().
                    getReuseBitmap(200, 200, 1);
            // 加载指定大小格式的图像
            bitmap = BitmapSizeReduce.getResizedBitmap(this, R.drawable.image,
                    200, 200, false, inBitmap);

            // 将新的 bitap 放入 LruCache 内存缓存中
            BitmapLruCacheMemoryReuse.getInstance().
                    putBitmapToLruCache(R.drawable.image + "", bitmap);

            Log.i("Bitmap 没有获取到创建新的", "image : " + bitmap.getWidth() + " , " +
                    bitmap.getHeight() + " , " +
                    bitmap.getByteCount());

        } else {
            Log.i("Bitmap 内存中获取数据", "image : " + bitmap.getWidth() + " , " +
                    bitmap.getHeight() + " , " +
                    bitmap.getByteCount());
        }



        // 第一次从 LruCache 内存中获取 Bitmap 数据
        Bitmap bitmap2 = BitmapLruCacheMemoryReuse.getInstance().
                getBitmapFromLruCache(R.drawable.image + "");

        Log.i("Bitmap 第二次内存中获取数据", "image : " + bitmap2.getWidth() + " , " +
                bitmap2.getHeight() + " , " +
                bitmap2.getByteCount());
    }

    /**
     * 图像尺寸缩小
     */
    private void bitmapSizeReduce(){
        // 从资源文件中加载内存
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.image);
        // 打印 Bitmap 对象的宽高, 字节大小
        Log.i("Bitmap", "image : " + bitmap.getWidth() + " , " +
                bitmap.getHeight() + " , " +
                bitmap.getByteCount());

        // 从资源文件中加载内存
        Bitmap reduceSizeBitmap = BitmapSizeReduce.getResizedBitmap(this, R.drawable.photo,
                100, 100 , false , null);
        // 打印 Bitmap 对象的宽高, 字节大小
        Log.i("Bitmap", "reduceSizeBitmap : " + reduceSizeBitmap.getWidth() + " , " +
                reduceSizeBitmap.getHeight() + " , " +
                reduceSizeBitmap.getByteCount());
        ivImage.setImageBitmap(reduceSizeBitmap);
    }


    /**
     * 分析 Bitmap 内存占用情况
     */
    private void showBitmapMemory(){
        Log.i("Bitmap", "getResources().getDisplayMetrics().densityDpi : " +
                getResources().getDisplayMetrics().densityDpi +
                " , getResources().getDisplayMetrics().density : " +
                getResources().getDisplayMetrics().density);

        // 从资源文件中加载内存
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.image);
        // 打印 Bitmap 对象的宽高, 字节大小
        Log.i("Bitmap", "image : " + bitmap.getWidth() + " , " +
                bitmap.getHeight() + " , " +
                bitmap.getByteCount());
        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.photo);
        // 打印 Bitmap 对象的宽高, 字节大小
        Log.i("Bitmap", "photo : " + bitmap.getWidth() + " , " +
                bitmap.getHeight() + " , " +
                bitmap.getByteCount());
    }
}
