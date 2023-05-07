package service;

import android.Manifest;
import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;


public class LocationTrackingService extends Service {
    private String policy;
    private boolean isLoggedIn;
    private FirebaseFirestore db;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private final static String TAG = "TrackingLocationService1";


    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "onStartCommand");
        init();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            stopLocationUpdate();
        }
        catch (Exception e) {
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void init() {
        SharedPreferences sharedPreferences = getSharedPreferences("my_app_prefs", Context.MODE_PRIVATE);
        policy = sharedPreferences.getString("policy", "11111");
        isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);
        Log.e(TAG, isLoggedIn + " " + policy);

        db = FirebaseFirestore.getInstance();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if(policy.charAt(4) == '1' && isLoggedIn)
            startLocationUpdate();
        else
            stopSelf();
    }

    private void startLocationUpdate() {
        Log.e(TAG, "startLocationUpdate");
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                sendLocationToServer(location);
            }
        };


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(new Activity(),new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 1);

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 5000, locationListener);

        if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 5000, locationListener);
    }

    private void stopLocationUpdate() {
        Log.e(TAG, "stop service");
        locationManager.removeUpdates(locationListener);
    }

    private void sendLocationToServer(Location location) {
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();

        Log.e(TAG, "sendLocationToServer");

        db.collection("locations")
                .document("15032001") //Build.getSerial())
                .update("latitude", latitude, "longitude", longitude)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.e(TAG, "update success" );
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "update failure: " + e.getMessage() );
                    }
                });
    }
}

