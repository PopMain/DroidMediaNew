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
    ImageView ivImage2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw_image);
        ivImage = findViewById(R.id.ivGlide);
        ivImage2 = findViewById(R.id.ivGlide2);
//        Glide.with(this)
//                .load(R.drawable.image)
//                .centerCrop()
//                .into(ivGlide);
        findViewById(R.id.btnBitmapReused).setOnClickListener(this);
        findViewById(R.id.btnBitmapSize).setOnClickListener(this);
        findViewById(R.id.btnBitmapReduce).setOnClickListener(this);
        findViewById(R.id.btnBitmapReused2).setOnClickListener(this);
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
            case R.id.btnBitmapReused2:
                clearLRUAndReused();
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
        Bitmap bitmap = BitmapLruCacheMemoryReuse.getInstance().getBitmapFromLruCache(R.drawable.photo + "");

        /*
            如果从内存中获取 Bitmap 对象失败 , 这里就需要创建该图片 , 并放入 LruCache 内存中
         */
        if(bitmap == null){
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeResource(getResources(), R.drawable.photo, options);
            // 要复用内存的 Bitmap 对象 , 将新的 Bitmap 写入到该 Bitmap 内存中
            Bitmap inBitmap;
            // 尝试获取复用对象
            inBitmap = BitmapLruCacheMemoryReuse.getInstance().
                    getReuseBitmap(options.outWidth, options.outHeight, 1);
            // 加载指定大小格式的图像
            bitmap = BitmapSizeReduce.getResizedBitmap(this, R.drawable.photo,
                    options.outWidth, options.outHeight, false, inBitmap);

            // 将新的 bitap 放入 LruCache 内存缓存中
            BitmapLruCacheMemoryReuse.getInstance().
                    putBitmapToLruCache(R.drawable.photo + "", bitmap);

            Log.i("Bitmap 没有获取到创建新的", "image : " + bitmap.getWidth() + " , " +
                    bitmap.getHeight() + " , " +
                    bitmap.getByteCount());

        } else {
            Log.i("Bitmap 内存中获取数据", "image : " + bitmap.getWidth() + " , " +
                    bitmap.getHeight() + " , " +
                    bitmap.getByteCount());
        }



        // 第二次从 LruCache 内存中获取 Bitmap 数据
        Bitmap bitmap2 = BitmapLruCacheMemoryReuse.getInstance().
                getBitmapFromLruCache(R.drawable.photo + "");

        Log.i("Bitmap 第二次内存中获取数据", "image : " + bitmap2.getWidth() + " , " +
                bitmap2.getHeight() + " , " +
                bitmap2.getByteCount());
    }

    private void clearLRUAndReused() {
        BitmapLruCacheMemoryReuse.getInstance().clearLruCache();
        // 尝试获取复用对象
        Bitmap inBitmap = BitmapLruCacheMemoryReuse.getInstance().
                getReuseBitmap(200, 200, 1);
        Log.i("Bitmap", "get inBitmap success " + (inBitmap != null));
        // 加载指定大小格式的图像
        Bitmap bitmap = BitmapSizeReduce.getResizedBitmap(this, R.drawable.image,
                200, 200, false, inBitmap);
        ivImage2.setImageBitmap(bitmap);
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
        ivImage2.setImageBitmap(reduceSizeBitmap);
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
