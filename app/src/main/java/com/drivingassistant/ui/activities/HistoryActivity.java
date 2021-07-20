package com.drivingassistant.ui.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.drivingassistant.R;
import com.drivingassistant.ui.retrofit.IMyService;

import org.json.JSONObject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class HistoryActivity extends AppCompatActivity {
    private TextView recordTime, speed, location, sign;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    IMyService iMyService;
    Button btn_location, btn_speed, btn_traffic_sign;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        btn_location = (Button) findViewById(R.id.btn_location);
        btn_speed = (Button) findViewById(R.id.btn_speed);
        btn_traffic_sign = (Button) findViewById(R.id.btn_traffic_sign);

        btn_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HistoryActivity.this, HistoryLocationActivity.class);
                startActivity(intent);
            }
        });
    }

    public void returnHome(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

}

