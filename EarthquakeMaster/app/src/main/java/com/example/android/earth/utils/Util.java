package com.example.android.earth.utils;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.widget.RemoteViews;

import com.example.android.earth.R;
import com.example.android.earth.ui.EarthquakesListWidget;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Util {
    public static final String PAST_HOUR_EARTHQUAKE_URL = "https://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/all_hour.geojson";
    public static final String PAST_WEEK_EARTHQUAKE_URL = "https://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/all_week.geojson";
    public static final String PAST_DAY_EARTHQUAKE_URL = "https://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/all_day.geojson";
    public static final String PAST_MONTH_EARTHQUAKE_URL = "https://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/all_month.geojson";

    public static final int LIMIT = 100;
    public static final double MAX_RADIUS_KM = 1000;

    public final static BitmapDescriptor[] arrayOfColors = new BitmapDescriptor[]{
            BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)
            , BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)
            , BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)
            , BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)
    };

    public static String convertToFormattedDate(long timeStamp){
        return new SimpleDateFormat("dd/MM/yy, hh:mm aa", Locale.getDefault()).format(new Date(timeStamp));
    }

    public static void updateWidgetWithText(Context context, String text){
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.earthquakes_list_widget);
        ComponentName thisWidget = new ComponentName(context, EarthquakesListWidget.class);
        remoteViews.setTextViewText(R.id.app_widget_text_id, text);
        appWidgetManager.updateAppWidget(thisWidget, remoteViews);
    }


}
