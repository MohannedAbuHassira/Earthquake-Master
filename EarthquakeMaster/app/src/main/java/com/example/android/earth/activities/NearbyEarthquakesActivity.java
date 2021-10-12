package com.example.android.earth.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.android.earth.adapters.NearbyEarthquakesAdapter;
import com.example.android.earth.model.Earthquake;
import com.example.android.earth.network.NetworkParser;
import com.example.android.earth.R;
import com.example.android.earth.utils.Util;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class NearbyEarthquakesActivity extends AppCompatActivity {

    public static NearbyEarthquakesAdapter adapter;

    public static ProgressDialog progressDialog;

    private NetworkParser networkParser;

    private LocationManager locationManager;
    private static final int REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby_earthquakes);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getString(R.string.loading));

        networkParser = new NetworkParser(this);
        networkParser.setNearbyEarthquakeErrorInterface(new NetworkParser.NearbyEarthquakeErrorInterface() {
            @Override
            public void onErrorJSONRequest() {
                findViewById(R.id.nearby_earthquake_recycleview_id).setVisibility(View.GONE);
                findViewById(R.id.map_error_layout).setVisibility(View.VISIBLE);
            }
        });

        setUpRecycleView();

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            getCurrentLocation();
        else
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);

        getSupportActionBar().setTitle(R.string.nearby_earthquakes);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    private void getCurrentLocation() {
        try {
            Location location = getLastKnownLocation();
            networkParser.setNearbyEarthquakes(location.getLatitude(), location.getLongitude());
        }catch (NullPointerException ex) {
            Toast.makeText(this, "please enable location service to proceed.", Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("MissingPermission")
    private Location getLastKnownLocation() {
        List<String> providers = locationManager.getProviders(true);
        for (String provider : providers) {
            locationManager.requestLocationUpdates(provider, 1000, 0,
                    new LocationListener() {

                        public void onLocationChanged(Location location) {
                        }

                        public void onProviderDisabled(String provider) {
                        }

                        public void onProviderEnabled(String provider) {
                        }

                        public void onStatusChanged(String provider, int status,
                                                    Bundle extras) {
                        }
                    });
            Location location = locationManager.getLastKnownLocation(provider);
            if (location == null)
                continue;
            else
                return location;
        }
        return null;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==REQUEST_CODE && grantResults.length>0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            getCurrentLocation();
    }

    private void setUpRecycleView(){
        RecyclerView recyclerView = findViewById(R.id.nearby_earthquake_recycleview_id);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new NearbyEarthquakesAdapter(this, new ArrayList<Earthquake>());
        recyclerView.setAdapter(adapter);
    }

    public void refreshTheLayout(View view){
        // Reload the activity
        finish();
        overridePendingTransition(0, 0);
        startActivity(getIntent());
        overridePendingTransition(0, 0);
    }

    public static String convertTheEarthquakeListToString(ArrayList<Earthquake> earthquakes){
        StringBuilder builder = new StringBuilder();
        builder.append("Nearby earthquakes list..."+ "\n");
        builder.append(R.string.dashes+"\n");
        for (int i = 0; i <earthquakes.size() ; i++) {
            builder.append("Name" + ": " + earthquakes.get(i).getPlace()+"\n");
            builder.append("Date" + ": " + earthquakes.get(i).getTime()+"\n");
            if(i!=earthquakes.size()-1) builder.append(R.string.dashes+"\n");
        }
        return builder.toString();
    }
}
