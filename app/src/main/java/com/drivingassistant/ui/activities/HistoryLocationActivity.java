package com.drivingassistant.ui.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.drivingassistant.R;
import com.drivingassistant.ui.adapter.LocationListAdapter;
import com.drivingassistant.model.entity.UserLocation;
import com.drivingassistant.ui.retrofit.IMyService;
import com.drivingassistant.ui.retrofit.RetrofitClient;

import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

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
        Log.wtf("khang", time_start);
        Log.wtf("khang", "Get history.");

        // Fetching the stored data
        // from the SharedPreference
        SharedPreferences sh = getSharedPreferences("USER", MODE_PRIVATE);

        String user_id = sh.getString("id", "");

        // Create JSON parser class
        JSONParserHistory jsonParserHistory = new JSONParserHistory();
        compositeDisposable.add(iMyService.getHistory(user_id, time_start, time_end)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Consumer<String>() {
                @Override
                public void accept(String response) throws Exception {
                    Log.wtf("khangne", response);
                    JSONObject jsonResponse = new JSONObject(response);
                    List<HashMap<String, String>> mapList = jsonParserHistory.parseResult(jsonResponse);
                    setContent(mapList);
                }
            }));
    }

    private void setContent(List<HashMap<String, String>> mapList) throws ParseException {
        listLocation = new ArrayList<>();
        // Use for loop
        for (int i = 0; i < mapList.size(); i++) {
            // Initialize hash map
            HashMap<String, String> hashMapList = mapList.get(i);
            // Get location
            Log.wtf("khangne", hashMapList.toString());
            String latitude = hashMapList.get("latitude");
            String longitude = hashMapList.get("longitude");
            String created_at = hashMapList.get("created_at");
            String location = "";
            Log.wtf("khangne", created_at);

            Geocoder geocoder = new Geocoder(HistoryLocationActivity.this, Locale.getDefault());
            try {
                List<Address> listAddresses = geocoder.getFromLocation(Double.parseDouble(latitude),
                        Double.parseDouble(longitude), 1);
                if(listAddresses != null && listAddresses.size() > 0){
                    location = listAddresses.get(0).getAddressLine(0);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            SimpleDateFormat output = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
            Date d = sdf.parse(created_at);
            String formattedTime = output.format(d);
            Log.wtf("khang", formattedTime);
            listLocation.add(new UserLocation(i + 1, location, formattedTime));
        }
        if (mapList.size() != 0) {
            locationListAdapter = new LocationListAdapter(listLocation);
            listViewLocation = (ListView) findViewById(R.id.listproduct);
            listViewLocation.setAdapter(locationListAdapter);
        }
        else{
            Toast.makeText(HistoryLocationActivity.this, "You don't go anywhere today!", Toast.LENGTH_SHORT).show();
        }
    }
}

