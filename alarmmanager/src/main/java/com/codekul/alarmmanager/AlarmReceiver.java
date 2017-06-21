package com.codekul.alarmmanager;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

/**
 * Created by aniruddha on 21/6/17.
 */

public class AlarmReceiver extends BroadcastReceiver {

    private MyLocation location;
    private LocationManager manager;

    public AlarmReceiver() {
        location = new MyLocation();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (manager == null) {
            manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
                return;
            }
            manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 50, 0.5f, location);
        }
        Log.i("@codekeul", "Called "+System.currentTimeMillis());
        Log.i("@codekeul", "Latitude "+ location.loc.getLatitude() +" Longitude - "+location.loc.getLongitude());
    }

    private class MyLocation implements LocationListener {

        private Location loc;

        @Override
        public void onLocationChanged(Location location) {
            this.loc = location;
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }

        public Location getLoc() {
            return loc;
        }
    }
}
