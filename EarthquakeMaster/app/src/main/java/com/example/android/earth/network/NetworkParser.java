package com.example.android.earth.network;

import android.app.AlertDialog;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.android.earth.activities.MapsActivity;
import com.example.android.earth.activities.NearbyEarthquakesActivity;
import com.example.android.earth.database.BookmarkDatabase;
import com.example.android.earth.database.BookmarkEntry;
import com.example.android.earth.model.Earthquake;
import com.example.android.earth.R;
import com.example.android.earth.utils.Util;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Random;

import static android.graphics.Color.rgb;
import static com.example.android.earth.activities.NearbyEarthquakesActivity.convertTheEarthquakeListToString;

public class NetworkParser {

    private RequestQueue queue;
    private Context context;

    private ErrorNotifierInterface errorListener;
    private NearbyEarthquakeErrorInterface nearbyEarthquakeErrorInterface;

    public interface ErrorNotifierInterface{
        void onErrorJSONRequest();
    }

    public interface NearbyEarthquakeErrorInterface{
        void onErrorJSONRequest();
    }

    public NetworkParser(Context context){
        queue = Volley.newRequestQueue(context);
        this.context = context;
    }

    public void setErrorListener(ErrorNotifierInterface errorListener){
        this.errorListener = errorListener;
    }

    public void setNearbyEarthquakeErrorInterface(NearbyEarthquakeErrorInterface nearbyEarthquakeErrorInterface){
        this.nearbyEarthquakeErrorInterface = nearbyEarthquakeErrorInterface;
    }

    public void setJsonObject(final String url) {
        try {
            if (MapsActivity.map != null) MapsActivity.map.clear();
            MapsActivity.progressDialog.show();
            if (!url.isEmpty()) {
                final Earthquake earthquake = new Earthquake();
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray features = response.getJSONArray("features");
                            shuffleJsonArray(features);
                            int length;
                            if (url.equals(Util.PAST_HOUR_EARTHQUAKE_URL))
                                length = features.length();
                            else length = Util.LIMIT;
                            if (features.length() < length) length = features.length();

                            for (int i = 0; i < length; i++) {
                                // Inside properties
                                JSONObject properties = features.getJSONObject(i).getJSONObject("properties");
                                String place = properties.getString("place");
                                double mag = Double.parseDouble(properties.getString("mag"));
                                long time = Long.parseLong(properties.getString("time"));
                                String detail = properties.getString("detail");

                                // Inside geometry/coordinates
                                double lon = features.getJSONObject(i).getJSONObject("geometry").getJSONArray("coordinates").getDouble(0);
                                double lat = features.getJSONObject(i).getJSONObject("geometry").getJSONArray("coordinates").getDouble(1);
                                earthquake.setPlace(place);
                                earthquake.setMagnitude(mag);
                                earthquake.setTime(Util.convertToFormattedDate(time));
                                earthquake.setLinkDetail(detail);
                                earthquake.setLon(lon);
                                earthquake.setLat(lat);
                                Marker marker = MapsActivity.map.addMarker(
                                        new MarkerOptions()
                                                .title(earthquake.getPlace())
                                                .position(new LatLng(lat, lon))
                                                // This appears under the title
                                                .snippet(context.getString(R.string.magnitude) + " " + earthquake.getMagnitude() + "\n" + context.getString(R.string.date) + ": " + earthquake.getTime()));
                                marker.setTag(earthquake.getLinkDetail());

                                // Change the marker color based on the magnitude of the earthquake
                                int color = 0;
                                if (earthquake.getMagnitude() >= 0 && earthquake.getMagnitude() <= 1) {
                                    marker.setIcon(Util.arrayOfColors[0]);
                                    color = Color.GREEN;
                                } else if (earthquake.getMagnitude() >= 1 && earthquake.getMagnitude() <= 2) {
                                    marker.setIcon(Util.arrayOfColors[1]);
                                    color = Color.YELLOW;
                                } else if (earthquake.getMagnitude() >= 2 && earthquake.getMagnitude() <= 3) {
                                    marker.setIcon(Util.arrayOfColors[2]);
                                    color = rgb(255, 165, 0);
                                } else if (earthquake.getMagnitude() >= 3) {
                                    marker.setIcon(Util.arrayOfColors[3]);
                                }

                                MapsActivity.map.addCircle(new CircleOptions()
                                        .center(new LatLng(earthquake.getLat(), earthquake.getLon()))
                                        .fillColor(color)
                                        .strokeColor(color))
                                        .setRadius(5000);

                                if(i==length-1) MapsActivity.progressDialog.dismiss();
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        errorListener.onErrorJSONRequest();
                    }
                });
                queue.add(jsonObjectRequest);
            }
        }catch (Exception ex) {setJsonObject(url);}
    }

    public void setJsonObjectDetailsUrl(final Marker marker){
        MapsActivity.progressDialog.show();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, marker.getTag().toString(), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                String url;
                try {
                    url = response.getJSONObject("properties").getString("url");
                    setJsonObjectPopUpDetails(url,marker);
                }catch (Exception ex){ex.printStackTrace();}
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                errorListener.onErrorJSONRequest();
            }
        });
        queue.add(jsonObjectRequest);
    }

    public void setJsonObjectPopUpDetails(final String url, final Marker marker){
        MapsActivity.alertDialog = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.pop_up_details,null);
        Button close = view.findViewById(R.id.closeBID);
        WebView webView = view.findViewById(R.id.webViewID);
        final ImageView bookmark = view.findViewById(R.id.bookmarkId);
        webView.setVerticalScrollBarEnabled(true);
        webView.setHorizontalScrollBarEnabled(true);

        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl(url);
        MapsActivity.alertDialog.setView(view);
        close.setOnClickListener(new View.OnClickListener() {public void onClick(View v) {MapsActivity.dialog.dismiss();}});
        MapsActivity.dialog = MapsActivity.alertDialog.create();
        MapsActivity.dialog.show();

        String bookmarkTitle = marker.getTitle();
        final String bookmarkSnippet = marker.getSnippet();
        final BookmarkEntry bookmarkEntry = new BookmarkEntry();
        bookmarkEntry.setTitle(bookmarkTitle);
        bookmarkEntry.setSnippet(bookmarkSnippet);

        new AsyncTask<Void,Void,Void>(){
            @Override
            protected Void doInBackground(Void... voids) {
                LiveData<BookmarkEntry> bookmarkEntryLiveData = BookmarkDatabase.getInstance(context).bookmarkDAO().getBookmarkBySnippet(bookmarkSnippet);
                bookmarkEntryLiveData.observe((LifecycleOwner) context, new Observer<BookmarkEntry>() {
                    @Override
                    public void onChanged(@Nullable BookmarkEntry bookmarkEntry) {
                        if(bookmarkEntry!=null)
                            bookmark.setBackgroundResource(R.drawable.bookmark_filled);
                        else
                            bookmark.setBackgroundResource(R.drawable.bookmark);
                    }
                });
                return null;
            }
        }.execute();

        bookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (bookmark.getBackground().getConstantState().equals(context.getResources().getDrawable(R.drawable.bookmark).getConstantState())) {
                    bookmark.setBackgroundResource(R.drawable.bookmark_filled);
                    new AsyncTask<Void, Void, Void>() {
                        @Override
                        protected Void doInBackground(Void... voids) {
                            BookmarkDatabase.getInstance(context).bookmarkDAO().addBookmark(bookmarkEntry);
                            return null;
                        }
                    }.execute();
                }
                else if(bookmark.getBackground().getConstantState().equals(context.getResources().getDrawable(R.drawable.bookmark_filled).getConstantState())){
                    bookmark.setBackgroundResource(R.drawable.bookmark);
                    new AsyncTask<Void, Void, Void>() {
                        @Override
                        protected Void doInBackground(Void... voids) {
                            BookmarkDatabase.getInstance(context).bookmarkDAO().removeBookmark(bookmarkEntry);
                            return null;
                        }
                    }.execute();
                }
            }
        });

        MapsActivity.progressDialog.dismiss();
    }

    public void setNearbyEarthquakes(double lat, double lon){
        NearbyEarthquakesActivity.progressDialog.show();

        String url = buildNearbyEarthquakesStringURL(lat,lon);

        final ArrayList<Earthquake> earthquakes = new ArrayList<>();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray features = response.getJSONArray("features");
                    shuffleJsonArray(features);

                    for (int i = 0; i < features.length(); i++) {
                        Earthquake earthquake = new Earthquake();
                        // Inside properties
                        JSONObject properties = features.getJSONObject(i).getJSONObject("properties");
                        String place = properties.getString("place");
                        double mag = Double.parseDouble(properties.getString("mag"));
                        long time = Long.parseLong(properties.getString("time"));

                        earthquake.setPlace(place);
                        earthquake.setMagnitude(mag);
                        earthquake.setTime(Util.convertToFormattedDate(time));

                        earthquakes.add(earthquake);
                    }
                    NearbyEarthquakesActivity.adapter.setData(earthquakes);
                    NearbyEarthquakesActivity.progressDialog.dismiss();
                    Util.updateWidgetWithText(context,convertTheEarthquakeListToString(earthquakes));
                }catch (Exception ex){ex.printStackTrace();}
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                nearbyEarthquakeErrorInterface.onErrorJSONRequest();
            }
        });
        queue.add(jsonObjectRequest);
    }

    private String buildNearbyEarthquakesStringURL(double lat, double lon){
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .authority("earthquake.usgs.gov")
                .appendPath("fdsnws")
                .appendPath("event")
                .appendPath("1")
                .appendPath("query")
                .appendQueryParameter("format", "geojson")
                .appendQueryParameter("latitude", String.valueOf(lat))
                .appendQueryParameter("longitude", String.valueOf(lon))
                .appendQueryParameter("maxradiuskm",String.valueOf(Util.MAX_RADIUS_KM))
                .appendQueryParameter("limit","100");
        return builder.build().toString();
    }

    private void shuffleJsonArray(JSONArray array) throws JSONException {
        // Implementing Fisher–Yates shuffle
        Random random = new Random();
        for (int i = array.length() - 1; i >= 0; i--) {
            int j = random.nextInt(i + 1);
            // Simple swap
            Object object = array.get(j);
            array.put(j, array.get(i));
            array.put(i, object);
        }
    }
}
