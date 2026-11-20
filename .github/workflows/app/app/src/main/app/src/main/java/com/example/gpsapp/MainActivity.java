
package com.example.gpsapp;

import android.Manifest;
import android.app.Activity;
import android.os.Bundle;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.content.Intent;
import android.net.Uri;
import android.widget.Button;
import android.widget.TextView;
import android.widget.LinearLayout;
import android.view.Gravity;
import android.view.View;
import android.graphics.Typeface;

public class MainActivity extends Activity {

    private TextView display;
    private LocationManager locationManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(30, 50, 30, 30);
        layout.setGravity(Gravity.CENTER);

        display = new TextView(this);
        display.setTextSize(20);
        display.setGravity(Gravity.CENTER);
        display.setText("GPS Only\n\nWaiting for location...");
        layout.addView(display);

        Button refresh = new Button(this);
        refresh.setText("Get GPS Location");
        layout.addView(refresh);

        Button map = new Button(this);
        map.setText("Open Google Maps");
        layout.addView(map);

        setContentView(layout);

        locationManager =
            (LocationManager) getSystemService(LOCATION_SERVICE);

        refresh.setOnClickListener(v -> getGPS());

        map.setOnClickListener(v -> {
            if (lastLocation != null) {
                String url = "geo:" + lastLocation.getLatitude()
                    + "," + lastLocation.getLongitude()
                    + "?q=" + lastLocation.getLatitude()
                    + "," + lastLocation.getLongitude();
                startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse(url)));
            }
        });

        getGPS();
    }

    private Location lastLocation;

    private void getGPS() {
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            }, 100);
            return;
        }

        try {
            LocationListener listener = location -> {
                lastLocation = location;
                display.setText(
                    "GPS LOCATION\n\nLatitude: "
                    + location.getLatitude()
                    + "\n\nLongitude: "
                    + location.getLongitude()
                    + "\n\nAccuracy: "
                    + location.getAccuracy() + " meters"
                );
            };

            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                1000,
                0,
                listener
            );

            Location old = locationManager.getLastKnownLocation(
                LocationManager.GPS_PROVIDER
            );

            if (old != null) {
                lastLocation = old;
                display.setText(
                    "GPS LOCATION\n\nLatitude: "
                    + old.getLatitude()
                    + "\n\nLongitude: "
                    + old.getLongitude()
                    + "\n\nAccuracy: "
                    + old.getAccuracy() + " meters"
                );
            }

        } catch (SecurityException e) {
            display.setText("GPS permission error");
        }
    }

    @Override
    public void onRequestPermissionsResult(
        int requestCode, String[] permissions, int[] results) {
        super.onRequestPermissionsResult(requestCode, permissions, results);
        if (requestCode == 100 && results.length > 0
                && results[0] == PackageManager.PERMISSION_GRANTED) {
            getGPS();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (locationManager != null) {
            locationManager.removeUpdates(
                location -> {}
            );
        }
    }
}
