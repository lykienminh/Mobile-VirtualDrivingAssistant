package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    public void openSettings(View view) {
        // Do something in response to button
        Intent intent = new Intent(this, settings.class);
        startActivity(intent);
    }
    public void openMainMap(View view) {
        // Do something in response to button
        Intent intent = new Intent(this, MainMap.class);
        startActivity(intent);
    }
}