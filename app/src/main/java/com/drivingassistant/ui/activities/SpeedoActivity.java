package com.drivingassistant.ui.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.drivingassistant.R;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import in.unicodelabs.kdgaugeview.KdGaugeView;

public class SpeedoActivity extends AppCompatActivity{
    KdGaugeView speedometerView;
    TextView txtLat, txtLong, txtSpeed;
    FusedLocationProviderClient mFusedLocationClient;
    int PERMISSION_ID = 44;
    double currentLat, currentLong, lastLat, lastLong;
    private static final long MAX_SPEED = 180;
    private static final long INTERVAL = 1000 * 2;
    private static final long FASTEST_INTERVAL = 1000 * 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speedo);
        speedometerView = (KdGaugeView) findViewById(R.id.speedMeter);
        txtLat = (TextView) findViewById(R.id.speed_lat);
        txtLong = (TextView) findViewById(R.id.speed_long);
        txtSpeed = (TextView) findViewById(R.id.speed_speed);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        getLastLocation();
    }

    @SuppressLint("MissingPermission")
    private void getLastLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                mFusedLocationClient.getLastLocation().addOnCompleteListener(task -> {
                    Location location = task.getResult();
                    if (location != null) {
                        currentLong = location.getLongitude();
                        currentLat = location.getLatitude();
                        double dSpeed = location.getSpeed();
                        double a = 3.6 * (dSpeed);
                        int kmhSpeed = (int) (Math.round(a));
                        txtLong.setText("Longitude:  " + currentLong);
                        txtLat.setText("Latitude: " + currentLat);
                        txtSpeed.setText("Speed = " + kmhSpeed);
                        speedometerView.setSpeed(kmhSpeed);
                        if (kmhSpeed >= MAX_SPEED){
                            Toast.makeText(this, "Over Speed ! Please Slow Down", Toast.LENGTH_SHORT).show();
                        }
                    }
                    requestNewLocationData();
                });
            } else {
                Toast.makeText(this, "Please turn on" + "your location...", Toast.LENGTH_LONG).show();
            }
        } else {
            requestPermissions();
        }
    }

    @SuppressLint("MissingPermission")
    private void requestNewLocationData() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
    }

    private final LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            lastLong = mLastLocation.getLongitude();
            lastLat = mLastLocation.getLatitude();
            double dSpeed = mLastLocation.getSpeed();
            double a = 3.6 * (dSpeed);
            int kmhSpeed = (int) (Math.round(a));
            txtLong.setText("Longitude:  " + currentLong);
            txtLat.setText("Latitude: " + currentLat);
            txtSpeed.setText("Speed = " + kmhSpeed);
            speedometerView.setSpeed(kmhSpeed);
            getLastLocation();
        }
    };

    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_ID);
    }

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (checkPermissions()) {
            getLastLocation();
        }
    }

    public void returnHome(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
