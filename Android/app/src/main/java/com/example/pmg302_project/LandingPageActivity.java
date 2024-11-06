// LandingPageActivity.java
package com.example.pmg302_project;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.components.BuildConfig;

import org.osmdroid.config.Configuration;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

public class LandingPageActivity extends AppCompatActivity {
    private MapView mapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing_page);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.nav_map);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            Intent intent;
            if (itemId == R.id.nav_home) {
                // Handle home action
                intent = new Intent(this, HomePageActivity.class);
                startActivity(intent);
                finish();
                return true;
            } else if (itemId == R.id.nav_order) {
                // Handle order action
                intent = new Intent(this, CartActivity.class);
                startActivity(intent);
                finish();
                return true;
            } else if (itemId == R.id.nav_map) {
                // Handle map action
                intent = new Intent(this, LandingPageActivity.class);
                startActivity(intent);
                finish();
                return true;
            }
            return false;
        });
        // Initialize osmdroid configuration
        Configuration.getInstance().setUserAgentValue(BuildConfig.VERSION_NAME);

        mapView = findViewById(R.id.mapView1);
        mapView.setMultiTouchControls(true);

        // Set the map's default zoom level and center point
        mapView.getController().setZoom(17.0);
        GeoPoint fptUniversityPoint = new GeoPoint(21.0129, 105.5272); // FPT University's coordinates
        mapView.getController().setCenter(fptUniversityPoint);

        // Add a marker at the store location
        Marker storeMarker = new Marker(mapView);
        storeMarker.setPosition(fptUniversityPoint);
        storeMarker.setTitle("Store Location");
        mapView.getOverlays().add(storeMarker);

        // Load animation
        Animation fallDown = AnimationUtils.loadAnimation(this, R.anim.fall_down);

        // Apply animation to CardViews
        CardView storeInfoCard = findViewById(R.id.storeInfoCard1);
        CardView addressCard = findViewById(R.id.addressCard1);
        CardView contactCard = findViewById(R.id.contactCard1);
        CardView newsletterCard = findViewById(R.id.newsletterCard);

        storeInfoCard.startAnimation(fallDown);
        addressCard.startAnimation(fallDown);
        contactCard.startAnimation(fallDown);
        newsletterCard.startAnimation(fallDown);
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }
}