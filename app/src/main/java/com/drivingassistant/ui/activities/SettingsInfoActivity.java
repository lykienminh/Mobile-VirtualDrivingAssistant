package com.drivingassistant.ui.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.drivingassistant.R;

import android.view.View;
import android.widget.ImageView;
import com.bumptech.glide.Glide;

public class SettingsInfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_info);
        ImageView imageView = findViewById(R.id.stg_info_gif);
        Glide.with(this).load(R.drawable.stg_info).into(imageView);
    }

    public void returnHome(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}