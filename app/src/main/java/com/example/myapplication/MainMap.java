package com.example.myapplication;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.util.Log;
import android.view.View;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Spinner;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class MainMap extends FragmentActivity implements OnMapReadyCallback {
    GoogleMap map;
    SupportMapFragment mapFragment;
    SearchView searchView;
    Marker marker;
    FusedLocationProviderClient fusedLocationProviderClient;
    ImageButton btn_restaurant, btn_traffic_mode, btn_gps;
    double currentLat = 0, currentLong = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_map);

        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //Initialize fused location
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
    }

    public final boolean isTrafficEnabled(GoogleMap googleMap) {
        return googleMap.isTrafficEnabled();

    }
    public final void setTrafficEnabled(GoogleMap googleMap, boolean enabled) {
        googleMap.setTrafficEnabled(enabled);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        getCurrentLocation();

        // set traffic mode
        btn_traffic_mode = (ImageButton) findViewById(R.id.btn_traffic_mode);

        btn_traffic_mode.setOnClickListener(v -> {
            if (isTrafficEnabled(map))
                setTrafficEnabled(map, false);
            else
                setTrafficEnabled(map, true);
        });

        // get nearby restaurant
        btn_restaurant = (ImageButton) findViewById(R.id.btn_restaurant);

        btn_restaurant.setOnClickListener(v -> {
            // Initialize url
            String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json" + // Url
                    "?location=" + currentLat + "," + currentLong + // Location latitude and longtitude
                    "&radius=5000" + // Nearby radius
                    "&types=restaurant" + // Place type
                    "&sensor=true" + // Sensor
                    "&key=" + getResources().getString(R.string.gg_map_key); // Google map key

            Log.wtf("khang", url);
            // Execute place task method to download json data
            new PlaceTask().execute(url);
        });

        // get current location
        btn_gps = (ImageButton) findViewById(R.id.btn_gps);

        btn_gps.setOnClickListener(v -> getCurrentLocation());

        // search places
        searchView = (SearchView) findViewById(R.id.search_place);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String location = searchView.getQuery().toString();
                if (location != null || !location.equals("")) {
                    Geocoder geocoder = new Geocoder(MainMap.this);
                    List<Address> addressList = null;
                    try {
                        addressList = geocoder.getFromLocationName(location, 1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (addressList != null) {
                        Address address = addressList.get(0);
                        LatLng position = new LatLng(address.getLatitude(), address.getLongitude());
                        if (marker != null) marker.remove();
                        marker = map.addMarker(new MarkerOptions().position(position).title(location));
                        map.animateCamera(CameraUpdateFactory.newLatLngZoom(position, 10));
                    }
                }
                return true;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

    }

    private void getCurrentLocation() {
        // Initialize task location
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // When permission denied, request permission
            ActivityCompat.requestPermissions(MainMap.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
            // here to request the missing permissions, and then overriding
            // public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        fusedLocationProviderClient.getLastLocation()
                .addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // When success
                        if (location != null){
                            currentLat = location.getLatitude();
                            currentLong = location.getLongitude();
                            mapFragment.getMapAsync(new OnMapReadyCallback() {
                                @Override
                                public void onMapReady(GoogleMap googleMap) {
                                    map = googleMap;
                                    // Initialize lat lng
                                    LatLng latLng = new LatLng(currentLat, currentLong);
                                    // Create marker options
                                    MarkerOptions options = new MarkerOptions().position(latLng)
                                            .title("I'm there")
                                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_location));
                                    // Zoom map
                                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
                                    // Add marker on map
                                    map.addMarker(options);
                                }
                            });
                        }
                    }
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 44){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                // When permission grated, call method
                getCurrentLocation();
            }
        }
    }

    private class PlaceTask extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... strings) {
            // Initialize data
            String data = null;
            try {
                // Initialize data
                data = downloadUrl(strings[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return data;
        }

        @Override
        protected void onPostExecute(String s) {
            new ParserTask().execute(s);
        }
    }

    private String downloadUrl(String string) throws IOException{
        // Initialize url
        URL url = new URL(string);

        // Initialize connection
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        // Connect connection
        connection.connect();

        // Initialize input stream
        InputStream stream = connection.getInputStream();

        // Initialize buffer reader
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

        // Initialize stream builder
        StringBuilder builder = new StringBuilder();

        // Initialize string variable
        String line = "";

        // Use while loop
        while((line = reader.readLine()) != null){
            builder.append(line);
        }

        // Get append data
        String data = builder.toString();

        // Close reader
        reader.close();

        // Return data
        return data;
    }

    private class ParserTask extends AsyncTask<String, Integer, List<HashMap<String, String>>> {


        @Override
        protected List<HashMap<String, String>> doInBackground(String... strings) {
            // Create JSON parser class
            JSONParser jsonParser = new JSONParser();

            // Initialize hash map list
            List<HashMap<String, String>> mapList = null;
            JSONObject object = null;
            try {
                // Initialize json object
                object = new JSONObject(strings[0]);

                // Parse json object
                mapList = jsonParser.parseResult(object);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            // Return map list
            return mapList;
        }

        @Override
        protected void onPostExecute(List<HashMap<String, String>> hashMaps) {
            // Clear map
            map.clear();

            // Use for loop
            for (int i = 0; i < hashMaps.size(); i++){
                // Initialize hash map
                HashMap<String, String> hashMapList = hashMaps.get(i);

                // Get latitude
                double lat = Double.parseDouble(hashMapList.get("lat"));

                // Get longitude
                double lng = Double.parseDouble(hashMapList.get("lng"));

                // Get name
                String name = hashMapList.get("name");

                // Concat latitude and longitude
                LatLng latLng = new LatLng(lat, lng);

                // Initialize marker options
                MarkerOptions options = new MarkerOptions();

                // Set position
                options.position(latLng);

                // Set title
                options.title(name);

                // Add marker on map
                map.addMarker(options);
            }
        }
    }
}