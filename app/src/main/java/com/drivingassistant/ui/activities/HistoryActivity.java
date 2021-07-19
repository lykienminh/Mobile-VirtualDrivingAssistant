package com.drivingassistant.ui.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.drivingassistant.R;

public class HistoryActivity extends AppCompatActivity {
    private TextView recordTime, speed, location, sign;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        recordTime = findViewById(R.id.history_time_value);
        speed = findViewById(R.id.history_speed_value);
        location = findViewById(R.id.history_location_value);
        sign = findViewById(R.id.history_sign_value);
    }

    public void returnHome(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

}
