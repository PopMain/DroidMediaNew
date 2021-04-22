package com.popmain.droidmedia.activity;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.popmain.droidmedia.R;

public class DrawImageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw_image);
        ImageView ivGlide = findViewById(R.id.ivGlide);
        Glide.with(this)
                .load(R.drawable.image)
                .centerCrop()
                .into(ivGlide);
    }
}
