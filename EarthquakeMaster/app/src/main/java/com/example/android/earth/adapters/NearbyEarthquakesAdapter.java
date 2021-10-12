package com.example.android.earth.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.earth.model.Earthquake;
import com.example.android.earth.R;

import java.util.ArrayList;
import java.util.List;

public class NearbyEarthquakesAdapter extends RecyclerView.Adapter<NearbyEarthquakesAdapter.ViewHolder> {

    private LayoutInflater mInflater;
    private List<Earthquake> mData;

    public NearbyEarthquakesAdapter(Context context, List<Earthquake> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }

    public void setData(ArrayList<Earthquake> mData){
        this.mData = mData;
        notifyDataSetChanged();
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(mInflater.inflate(R.layout.nearby_earthquakes_list_item, parent, false));
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Earthquake earthquake = mData.get(position);
        holder.mag.setText(String.valueOf(earthquake.getMagnitude()));
        holder.place.setText(earthquake.getPlace());
        holder.date.setText(earthquake.getTime());

        // Change the circle color based on the magnitude of the earthquake
        if (earthquake.getMagnitude() >= 0 && earthquake.getMagnitude() <= 1) {
            holder.mag.getBackground().setColorFilter(Color.parseColor("#76FF03"), PorterDuff.Mode.SRC_ATOP);
        } else if (earthquake.getMagnitude() >= 1 && earthquake.getMagnitude() <= 2) {
            holder.mag.getBackground().setColorFilter(Color.parseColor("#FFEA00"), PorterDuff.Mode.SRC_ATOP);
        } else if (earthquake.getMagnitude() >= 2 && earthquake.getMagnitude() <= 3) {
            holder.mag.getBackground().setColorFilter(Color.parseColor("#FF6D00"), PorterDuff.Mode.SRC_ATOP);
        } else if (earthquake.getMagnitude() >= 3) {
            holder.mag.getBackground().setColorFilter(Color.parseColor("#DD2C00"), PorterDuff.Mode.SRC_ATOP);
        }
    }

    @Override
    public int getItemCount() { return mData == null ? 0 : mData.size(); }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView mag;
        TextView date;
        TextView place;

        ViewHolder(View itemView) {
            super(itemView);
            mag = itemView.findViewById(R.id.nearby_earthquake_mag_id);
            place = itemView.findViewById(R.id.nearby_earthquake_place_id);
            date = itemView.findViewById(R.id.nearby_earthquake_date_id);
        }
    }
}