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
                    String latitude = Double.toString(location.getLatitude());
                    String longitude = Double.toString(location.getLongitude());

                    try{
                        MapActivity.getInstance().sendHistory("", latitude, longitude, "");
                    }
                    catch (Exception ex){
                        ex.printStackTrace();
                    }
                }
            }
        }
    }
}