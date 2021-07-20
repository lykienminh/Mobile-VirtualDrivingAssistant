package com.drivingassistant.ui.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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

    public void getHistory(String duration) {
        String time_start = "", time_end = "";
        Log.wtf("khang", "Get history.");
        compositeDisposable.add(iMyService.getHistory(time_start, time_end)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Consumer<String>() {
                @Override
                public void accept(String response) throws Exception {
                    JSONObject jsonResponse = new JSONObject(response);
                    String result = jsonResponse.getString("result");
                    location.setText(result);
                }
            }));
    }
}

