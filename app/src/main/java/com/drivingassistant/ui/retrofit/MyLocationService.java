package com.drivingassistant.ui.retrofit;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.drivingassistant.ui.activities.MapActivity;
import com.google.android.gms.location.LocationResult;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class MyLocationService extends BroadcastReceiver {

    public static final String ACCESS_PROCESS_UPDATE = "package com.drivingassistant.ui.activities.UPDATE_LOCATION";
    private static final DateTimeFormatter dtfDateTime = DateTimeFormatter.ofPattern("MM/dd/uuuu HH:mm:ss");
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null)
        {
            final String action = intent.getAction();
            if (ACCESS_PROCESS_UPDATE.equals(action)){
                LocationResult result = LocationResult.extractResult(intent);
                if (result != null){
                    Location location = result.getLastLocation();
                    String location_string = new StringBuilder("" + location.getLatitude())
                            .append("/")
                            .append(location.getLongitude())
                            .toString();

                    // seconds passed since the Unix epoch time (midnight of January 1, 1970 UTC)
                    Instant now = Instant.now();
                    // convert Instant to LocalDateTime
                    LocalDateTime localDateTime = LocalDateTime.ofInstant(now, ZoneId.systemDefault());
                    String time = dtfDateTime.format(localDateTime);
                    Log.wtf("khang", time);
                    try{
                        MapActivity.getInstance().sendHistory("", location_string, "", time);
                    }
                    catch (Exception ex){
                        ex.printStackTrace();
                    }
                }
            }
        }
    }
}