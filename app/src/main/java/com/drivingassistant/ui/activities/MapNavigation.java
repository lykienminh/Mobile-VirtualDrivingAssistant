package com.drivingassistant.ui.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.widget.Toast;

import com.drivingassistant.R;
import com.drivingassistant.ui.fragments.MapFragmentView;

import java.util.ArrayList;
import java.util.Arrays;

public class MapNavigation extends AppCompatActivity {

    private final static int REQUEST_CODE_ASK_PERMISSIONS = 1;
    private static final String[] RUNTIME_PERMISSIONS = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.ACCESS_NETWORK_STATE
    };
    private final ArrayList<Double> positions = new ArrayList<Double>();

    private MapFragmentView m_mapFragmentView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_navigation);

        if (hasPermissions(this, RUNTIME_PERMISSIONS)) {
            setupMapFragmentView();
        } else {
            ActivityCompat
                    .requestPermissions(this, RUNTIME_PERMISSIONS, REQUEST_CODE_ASK_PERMISSIONS);
        }
    }

    /**
     * Only when the app's target SDK is 23 or higher, it requests each dangerous permissions it
     * needs when the app is running.
     */
    private static boolean hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission)
                        != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS: {
                for (int index = 0; index < permissions.length; index++) {
                    if (grantResults[index] != PackageManager.PERMISSION_GRANTED) {

                        /*
                         * If the user turned down the permission request in the past and chose the
                         * Don't ask again option in the permission request system dialog.
                         */
                        if (!ActivityCompat
                                .shouldShowRequestPermissionRationale(this, permissions[index])) {
                            Toast.makeText(this, "Required permission " + permissions[index]
                                            + " not granted. "
                                            + "Please go to settings and turn on for sample app",
                                    Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(this, "Required permission " + permissions[index]
                                    + " not granted", Toast.LENGTH_LONG).show();
                        }
                    }
                }

                setupMapFragmentView();
                break;
            }
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void setupMapFragmentView() {
        Intent intent = this.getIntent();
        String[] place1 = intent.getStringExtra("place1").split(",");
        double lat1 = Double.parseDouble(place1[0]);
        double lng1 = Double.parseDouble(place1[1]);
        positions.add(lat1);
        positions.add(lng1);
        String[] place2 = intent.getStringExtra("place2").split(",");
        double lat2 = Double.parseDouble(place2[0]);
        double lng2 = Double.parseDouble(place2[1]);
        positions.add(lat2);
        positions.add(lng2);
        Log.wtf("navigation", positions.toString());
        // All permission requests are being handled. Create map fragment view. Please note
        // the HERE Mobile SDK requires all permissions defined above to operate properly.
        m_mapFragmentView = new MapFragmentView(this, positions);
    }

    @Override
    public void onDestroy() {
        m_mapFragmentView.onDestroy();
        super.onDestroy();
    }
}