package com.drivingassistant.ui.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.drivingassistant.R;
import com.drivingassistant.adapter.LocationListAdapter;
import com.drivingassistant.model.entity.UserLocation;
import com.drivingassistant.ui.retrofit.IMyService;
import com.drivingassistant.ui.retrofit.RetrofitClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class HistoryLocationActivity extends AppCompatActivity {
    private TextView recordTime, speed, location, sign;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    IMyService iMyService;
    ArrayList<UserLocation> listLocation;
    LocationListAdapter locationListAdapter;
    ListView listViewLocation;
    private static final DateTimeFormatter dtfDateTime = DateTimeFormatter.ofPattern("MM/dd/uuuu");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_location);

        // Init Service
        Retrofit retrofitClient = RetrofitClient.getInstance();
        iMyService = retrofitClient.create(IMyService.class);

        getHistory();
    }

    public void returnHome(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void getHistory() {
        Instant now = Instant.now();
        // convert Instant to LocalDateTime
        LocalDateTime localDateTime = LocalDateTime.ofInstant(now, ZoneId.systemDefault());
        String time = dtfDateTime.format(localDateTime);
        String time_start = time + " 00:00:00";
        String time_end = time + " 23:59:59";
        Log.wtf("khang", "Get history.");

        // Create JSON parser class
        JSONParserHistory jsonParserHistory = new JSONParserHistory();
        compositeDisposable.add(iMyService.getHistory(time_start, time_end)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Consumer<String>() {
                @Override
                public void accept(String response) throws Exception {
                    Log.wtf("khang", response);
                    JSONObject jsonResponse = new JSONObject(response);
                    List<HashMap<String, String>> mapList = jsonParserHistory.parseResult(jsonResponse);
                    setContent(mapList);
                }
            }));
    }

    private void setContent(List<HashMap<String, String>> mapList) {
        listLocation = new ArrayList<>();
        // Use for loop
        for (int i = 0; i < mapList.size(); i++) {
            // Initialize hash map
            HashMap<String, String> hashMapList = mapList.get(i);
            Log.wtf("khang", hashMapList.toString());
            // Get location
            String location = hashMapList.get("location");
            String created_at = hashMapList.get("created_at");

            Log.wtf("khang", created_at);
            listLocation.add(new UserLocation(i + 1, location, created_at));
        }
        locationListAdapter = new LocationListAdapter(listLocation);
        listViewLocation = (ListView) findViewById(R.id.listproduct);
        listViewLocation.setAdapter(locationListAdapter);
    }
}

