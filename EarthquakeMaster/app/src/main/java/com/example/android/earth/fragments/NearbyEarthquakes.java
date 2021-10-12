package com.example.android.earth.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.earth.activities.NearbyEarthquakesActivity;
import com.example.android.earth.R;

public class NearbyEarthquakes extends Fragment {

    public NearbyEarthquakes() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_nearby_earthquakes, container, false);
        view.findViewById(R.id.nearby_earthquakes_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), NearbyEarthquakesActivity.class));
            }
        });
        return view;
    }

}
