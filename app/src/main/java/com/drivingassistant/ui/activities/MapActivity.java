package com.drivingassistant.ui.activities;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;

import com.drivingassistant.R;
import com.drivingassistant.parser.FetchURL;
import com.drivingassistant.parser.TaskLoadedCallback;
import com.drivingassistant.ui.retrofit.IMyService;
import com.drivingassistant.ui.retrofit.MyLocationService;
import com.drivingassistant.ui.retrofit.RetrofitClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import androidx.annotation.NonNull;
//import androidx.appcompat.widget.SearchView;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.util.Log;
import android.view.View;

import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
//

public class MapActivity extends FragmentActivity implements OnMapReadyCallback, TaskLoadedCallback {
    GoogleMap map;
    SupportMapFragment mapFragment;
    SearchView searchView;
    Marker marker;
    FusedLocationProviderClient fusedLocationProviderClient;
    ImageButton btn_traffic_mode, btn_gps, btn_direction;
    Button btn_restaurant;
    LatLng place1, place2;
    Polyline currentPolyline;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    IMyService iMyService;
    LocationRequest locationRequest;
    static MapActivity instance;

    double currentLat = 0, currentLong = 0;

    public static MapActivity getInstance() {
        return instance;
    }
    private static final DateTimeFormatter dtfDateTime = DateTimeFormatter.ofPattern("MM/dd/uuuu HH:mm:ss");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        loadLocale();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        instance = this;

        // Init Service
        Retrofit retrofitClient = RetrofitClient.getInstance();
        iMyService = retrofitClient.create(IMyService.class);

        // Initialize fused location
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);

        btn_restaurant = (Button) findViewById(R.id.btn_restaurant);
        btn_restaurant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Initialize url
                String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json" + // Url
                        "?location=" + currentLat + "," + currentLong + // Location latitude and longtitude
                        "&radius=5000" + // Nearby radius
                        "&types=restaurant" + // Place type
                        "&sensor=true" + // Sensor
                        "&key=" + getResources().getString(R.string.map_key); // Google map key

                Log.wtf("khang", url);
                // Execute place task method to download json data
                new PlaceTask().execute(url);
            }
        });

        btn_traffic_mode = (ImageButton) findViewById(R.id.btn_traffic_mode);
        btn_traffic_mode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isTrafficEnabled(map))
                    setTrafficEnabled(map, false);
                else
                    setTrafficEnabled(map, true);
            }
        });

        btn_gps = (ImageButton) findViewById(R.id.btn_gps);
        btn_gps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCurrentLocation();
            }
        });

        // Initialize places
        Places.initialize(getApplicationContext(), "AIzaSyCrO7kgh1q6qVCRkAH2GPpjYWnnLXxsK2U");

        searchView = (SearchView) findViewById(R.id.sv_location);
        // Set searchView non focusable
        searchView.setFocusable(false);
        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Initialize place field list
                List<Place.Field> fieldList = Arrays.asList(Place.Field.ADDRESS,
                        Place.Field.LAT_LNG, Place.Field.NAME);
                // Create intent
                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY,
                        fieldList).build(MapActivity.this);
                // Start activity result
                startActivityForResult(intent, 100);
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String location = searchView.getQuery().toString();
                List<Address> addressList = null;
                if (location != null || !location.equals("")) {
                    Geocoder geocoder = new Geocoder(MapActivity.this);
                    try {
                        addressList = geocoder.getFromLocationName(location, 1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (addressList.size() > 0) {
                        Address address = addressList.get(0);
                        place2 = new LatLng(address.getLatitude(), address.getLongitude());
                        if (marker != null) marker.remove();
                        marker = map.addMarker(new MarkerOptions().position(place2).title(location));
                        map.animateCamera(CameraUpdateFactory.newLatLngZoom(place2, 70));
                    } else {
                        Toast.makeText(MapActivity.this, "No results found on Kime Maps.", Toast.LENGTH_SHORT).show();
                    }
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        btn_direction = (ImageButton) findViewById(R.id.btn_direction);
        btn_direction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Initialize url
                String url = getUrl(place1, place2, "driving");
                Log.wtf("khang", url);
                // Execute place task method to download json data
                new FetchURL(MapActivity.this).execute(url, "driving");
            }
        });

    }

    private void updateLocation() {
        buildLocationRequest();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, getPendingIntent());
        Log.wtf("khang", "Update location.");
    }

    private PendingIntent getPendingIntent() {
        Intent intent = new Intent(this, MyLocationService.class);
        intent.setAction(MyLocationService.ACCESS_PROCESS_UPDATE);
        Log.wtf("khang", "Pending intent.");
        return PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private void buildLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setSmallestDisplacement(10f);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK){
            // When success
            // Initialize place
            Place place = Autocomplete.getPlaceFromIntent(data);
            // Set address on SearchView
            Log.wtf("search_view", place.getAddress());
            searchView.setQuery(place.getAddress(), false);
        }
        else if (resultCode == AutocompleteActivity.RESULT_ERROR){
            // When error
            // Initialize status
            Status status = Autocomplete.getStatusFromIntent(data);
            // Display result
            Log.wtf("search_view", status.getStatusMessage());
            Toast.makeText(MapActivity.this, status.getStatusMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    // Traffic mode
    public final boolean isTrafficEnabled(GoogleMap googleMap) {
        return googleMap.isTrafficEnabled();

    }
    public final void setTrafficEnabled(GoogleMap googleMap, boolean enabled) {
        googleMap.setTrafficEnabled(enabled);
    }

    // Map mode
    @Override
    public void onMapReady(GoogleMap googleMap) {
        getCurrentLocation();
        Dexter.withActivity(this)
            .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
            .withListener(new PermissionListener() {
                @Override
                public void onPermissionGranted(PermissionGrantedResponse response) {
                    Log.wtf("khang", "Dexter.");
                    updateLocation();
                }

                @Override
                public void onPermissionDenied(PermissionDeniedResponse response) {
                    Toast.makeText(MapActivity.this, "You must accept this location.", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {

                }
            }).check();
    }

    private void getCurrentLocation() {
        // Initialize task location
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // When permission denied, request permission
            ActivityCompat.requestPermissions(MapActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                // When success
                if (location != null){

                    Log.wtf("khang", "I'm here.");
                    mapFragment.getMapAsync(new OnMapReadyCallback() {
                        @Override
                        public void onMapReady(GoogleMap googleMap) {
                            map = googleMap;
                            map.clear();

                            // Initialize lat lng
                            currentLat = location.getLatitude();
                            currentLong = location.getLongitude();
                            place1 = new LatLng(currentLat, currentLong);

                            // Create marker options
                            MarkerOptions options = new MarkerOptions().position(place1)
                                    .title("I'm there")
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_location));

                            // Zoom map
                            map.animateCamera(CameraUpdateFactory.newLatLngZoom(place1, 15));

                            // Add marker on map
                            map.addMarker(options);
                            map.getUiSettings().setZoomControlsEnabled(false);
                            map.getUiSettings().setMyLocationButtonEnabled(true);
                        }
                    });
                }
            }
        });
    }

    public void sendHistory(String speed, String latitude, String longitude, String traffic_sign) {
        MapActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.wtf("khang", "Send history.");
                compositeDisposable.add(iMyService.sendHistory(speed, latitude, longitude, traffic_sign)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<String>() {
                            @Override
                            public void accept(String response) throws Exception {
                                getCurrentLocation();
                                JSONObject jsonResponse = new JSONObject(response);
                                String result = jsonResponse.getString("result");
                                Log.wtf("khang", result);
                            }
                        }));
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 44) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // When permission grated, call method
                getCurrentLocation();
            }
        }
    }

    // Google Direction API
    private String getUrl(LatLng origin, LatLng dest, String directionMode){
        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        // Mode
        String mode = "mode=" + directionMode;
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + mode;
        // Output format
        String output = "json";
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + //
                "?" + parameters + "&key=" + getResources().getString(R.string.map_key);
        return url;
    }

    @Override
    public void onTaskDone(Object... values) {
        if (currentPolyline != null)
            currentPolyline.remove();
        currentPolyline = map.addPolyline((PolylineOptions) values[0]);
        LatLng center = new LatLng((place1.latitude + place2.latitude) / 2,
                (place1.longitude + place2.longitude) / 2);
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(center, 13));
    }

    // Google Places API
    private class PlaceTask extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... strings) {
            // Initialize data
            String data = null;
            // Initialize ''data''
            try {
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

        Log.wtf("search_near_by", data);
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

            return mapList;
        }

        @Override
        protected void onPostExecute(List<HashMap<String, String>> hashMaps) {

            // Use for loop
            for (int i = 0; i < hashMaps.size(); i++){
                // Initialize hash map
                HashMap<String, String> hashMapList = hashMaps.get(i);
                Log.wtf("khang", hashMapList.toString());
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
    private void setLocale(String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());

        SharedPreferences.Editor editor = getSharedPreferences("Settings", MODE_PRIVATE).edit();
        editor.putString("MyLang", lang);
        editor.apply();
    }

    public void loadLocale(){
        SharedPreferences prefs = getSharedPreferences("Settings", Activity.MODE_PRIVATE);
        String lang = prefs.getString("MyLang", "");
        setLocale(lang);
    }
}