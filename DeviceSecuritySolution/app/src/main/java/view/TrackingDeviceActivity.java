package view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.application.devicesecuritysolution.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import model.DeviceInformation;

public class TrackingDeviceActivity extends AppCompatActivity implements OnMapReadyCallback {
    public static GoogleMap map;
    public static MapView mapView;
    private DeviceInformation deviceInformation;

    private Handler handler;
    private Runnable runnable;

    private static final String TAG = "TrackingDeviceActivity1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking_device);
        deviceInformation = (DeviceInformation) getIntent().getSerializableExtra("infoDevice");
        Log.e(TAG, "create");
        mapView = (MapView) findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;
        Log.e(TAG, "onMapReady");
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        map.setMyLocationEnabled(true);
        map.getUiSettings().setZoomControlsEnabled(true);
        map.getUiSettings().setCompassEnabled(true);
        map.getUiSettings().setMyLocationButtonEnabled(true);
        map.getUiSettings().setRotateGesturesEnabled(true);
        map.getUiSettings().setScrollGesturesEnabled(true);
        map.getUiSettings().setTiltGesturesEnabled(true);
        map.getUiSettings().setZoomGesturesEnabled(true);

        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                getDataFromSever();
                //Goi lai sau moi phut
                handler.postDelayed(this, 60000);
            }
        };
        //Bat dau dinh ki goi
        handler.post(runnable);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        handler.removeCallbacks(runnable);
    }

    public void getDataFromSever() {
        boolean networkConnected = isNetworkConnected();
        if(networkConnected) {
            Log.e(TAG, "getDataFromSever: " + deviceInformation.getSerialNo());
            FirebaseFirestore.getInstance().collection("locations")
                    .whereEqualTo("serialNo", deviceInformation.getSerialNo())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if(task.isSuccessful()) {
                                Log.e(TAG, "Successful: " + task.getResult().size());
                                for(DocumentSnapshot doc : task.getResult()) {
                                    double latitude = doc.getDouble("latitude");
                                    double longitude = doc.getDouble("longitude");
                                    if(latitude != Double.MIN_VALUE && longitude != Double.MIN_VALUE) {
                                        map.clear();
                                        map.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).title(doc.getString("serialNo")));
                                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 5));
                                    }
                                    else
                                        Toast.makeText(TrackingDeviceActivity.this, "This device hasn't updated its location", Toast.LENGTH_SHORT).show();

                                }
                            }
                        }
                    });
        }
    }

    public boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetwork() != null; // return true =(connected),false=(not connected)
    }
}