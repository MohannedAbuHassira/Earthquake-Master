package com.example.android.earth.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.example.android.earth.adapters.SliderPagerAdapter;
import com.example.android.earth.fragments.BookmarkedEarthquakes;
import com.example.android.earth.fragments.NearbyEarthquakes;
import com.example.android.earth.fragments.RecentEarthquakesFragment;
import com.example.android.earth.R;
import com.google.android.gms.maps.MapsInitializer;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private SliderPagerAdapter sliderPagerAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        MapsInitializer.initialize(getApplicationContext());

        viewPager = findViewById(R.id.view_pager_id);

        List<Fragment> fragments = new ArrayList<>();
        fragments.add(new RecentEarthquakesFragment());
        fragments.add(new NearbyEarthquakes());
        fragments.add(new BookmarkedEarthquakes());

        sliderPagerAdapter = new SliderPagerAdapter(getSupportFragmentManager(), fragments);

        viewPager.setAdapter(sliderPagerAdapter);

        makeViewPagerFancier();

        getSupportActionBar().setTitle(getString(R.string.app_name));
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    private void makeViewPagerFancier(){
        viewPager.setPageTransformer(false, new ViewPager.PageTransformer() {
            private static final float MIN_SCALE = 0.85f;
            private static final float MIN_ALPHA = 0.5f;

            public void transformPage(View view, float position) {
                int pageWidth = view.getWidth();
                int pageHeight = view.getHeight();

                if (position < -1) { // [-Infinity,-1)
                    // This page is way off-screen to the left.
                    view.setAlpha(0f);

                } else if (position <= 1) { // [-1,1]
                    // Modify the default slide transition to shrink the page as well
                    float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position));
                    float vertMargin = pageHeight * (1 - scaleFactor) / 2;
                    float horzMargin = pageWidth * (1 - scaleFactor) / 2;
                    if (position < 0) {
                        view.setTranslationX(horzMargin - vertMargin / 2);
                    } else {
                        view.setTranslationX(-horzMargin + vertMargin / 2);
                    }

                    // Scale the page down (between MIN_SCALE and 1)
                    view.setScaleX(scaleFactor);
                    view.setScaleY(scaleFactor);

                    // Fade the page relative to its size.
                    view.setAlpha(MIN_ALPHA +
                            (scaleFactor - MIN_SCALE) /
                                    (1 - MIN_SCALE) * (1 - MIN_ALPHA));

                } else { // (1,+Infinity]
                    // This page is way off-screen to the right.
                    view.setAlpha(0f);
                }
            }
        });
    }
}
