package com.example.android.earth.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.example.android.earth.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

public class CustomInfoWindow implements GoogleMap.InfoWindowAdapter {
    private View view;
    private Context context;

    public CustomInfoWindow(Context context){
        this.context = context;
        view = LayoutInflater.from(context).inflate(R.layout.custom_info_window,null);
    }

    @Override
    public View getInfoWindow(Marker marker) {
        TextView title = view.findViewById(R.id.theTitleID);
        title.setText(marker.getTitle());
        TextView snippet = view.findViewById(R.id.snippetID);
        snippet.setText(marker.getSnippet());
        return view;
    }

    @Override
    public View getInfoContents(Marker marker) {
        TextView title = view.findViewById(R.id.theTitleID);
        title.setText(marker.getTitle());
        TextView magnitude = view.findViewById(R.id.snippetID);
        magnitude.setText(marker.getSnippet());
        return view;
    }
}
