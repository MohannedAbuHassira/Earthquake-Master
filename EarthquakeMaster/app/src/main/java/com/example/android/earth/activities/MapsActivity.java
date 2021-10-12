package com.example.android.earth.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.RadioGroup;

import com.example.android.earth.network.NetworkParser;
import com.example.android.earth.R;
import com.example.android.earth.ui.CustomInfoWindow;
import com.example.android.earth.utils.Util;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Marker;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener
        , GoogleMap.OnMarkerClickListener{

    public static GoogleMap map;

    public static AlertDialog dialog;
    public static AlertDialog.Builder alertDialog;

    private RadioGroup radioGroup;

    public static ProgressDialog progressDialog;

    private NetworkParser networkParser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getString(R.string.loading));

        networkParser = new NetworkParser(this);
        networkParser.setErrorListener(new NetworkParser.ErrorNotifierInterface() {
            @Override
            public void onErrorJSONRequest() {
                findViewById(R.id.map_fragment_id).setVisibility(View.GONE);
                findViewById(R.id.map_error_layout).setVisibility(View.VISIBLE);
                progressDialog.dismiss();
            }
        });

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        registerClickListeners();

        map.setInfoWindowAdapter(new CustomInfoWindow(MapsActivity.this));

        radioGroup = findViewById(R.id.radioGroupID);
        radioGroupOnChecked();
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        networkParser.setJsonObjectDetailsUrl(marker);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        Util.updateWidgetWithText(this,marker.getTitle()+"\n" + marker.getSnippet());
        return false;
    }

    private void registerClickListeners(){
        map.setOnMarkerClickListener(this) ;
        map.setOnInfoWindowClickListener(this);
    }

    private void radioGroupOnChecked(){
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.pastHourRBID :
                        networkParser.setJsonObject(Util.PAST_HOUR_EARTHQUAKE_URL);
                        break;
                    case R.id.pastDayRBID :
                        networkParser.setJsonObject(Util.PAST_DAY_EARTHQUAKE_URL);
                        break;
                    case R.id.pastWeekRBID :
                        networkParser.setJsonObject(Util.PAST_WEEK_EARTHQUAKE_URL);
                        break;
                    case R.id.pastMonthRBID :
                        networkParser.setJsonObject(Util.PAST_MONTH_EARTHQUAKE_URL);
                        break;
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        radioGroup = findViewById(R.id.radioGroupID);
        radioGroupOnChecked();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(progressDialog.isShowing()) progressDialog.dismiss();
    }

    public void refreshTheLayout(View view){
        // Reload the activity
        finish();
        overridePendingTransition(0, 0);
        startActivity(getIntent());
        overridePendingTransition(0, 0);
    }
}
