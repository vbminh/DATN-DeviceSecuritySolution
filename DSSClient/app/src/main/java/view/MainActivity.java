package view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.firebase.FirebaseApp;

import service.ForegroundService;
import util.KnoxUtils;

public class MainActivity extends AppCompatActivity {

    private final static String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //KnoxUtils.setActiveAdmin();
        //checkLocationPermission();
        //lockDeviceCover();
        FirebaseApp.initializeApp(this);
        startApp();
    }

    private void startApp() {
        //Khoi chay service trong nen
        Intent serviceIntent = new Intent(this,  ForegroundService.class);
        startService(serviceIntent);

        //Kiem tra trang thai dang nhap
        SharedPreferences sharedPreferences = getSharedPreferences("my_app_prefs", Context.MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);

        //Chuyen den giao dien dang nhap
        if(!isLoggedIn) {
            Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
            loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(loginIntent);
            finish();
        }
        else {//Chuyen den giao dien trang chu
            Intent homeIntent = new Intent(MainActivity.this, HomeActivity.class);
            homeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(homeIntent);
            finish();
        }
    }

    /*private void lockDeviceCover() {
        mCoverManager = new ScoverManager(getApplicationContext());
        mListener = new CoverAttachmentListener(this);
        try {
            Log.e(TAG, "unLockDeviceCover" );
            mCoverManager.registerListener(mListener);
        } catch (SsdkUnsupportedException e) {
            e.printStackTrace();
        }
    }*/


    @Override
    protected void onDestroy() {
        super.onDestroy();
        /*try {
            mCoverManager.unregisterListener(mListener);
        } catch (SsdkUnsupportedException e) {
            e.printStackTrace();
        }*/
    }

    /*public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                new AlertDialog.Builder(this)
                        .setTitle("Location Permission")
                        .setMessage("Allow DSS to access this device's location")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(MainActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 99);
                            }
                        })
                        .create()
                        .show();


            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 99);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case 99: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                        lockDeviceCover();
                        setActiveS();
                    }
                } else {
                    lockDeviceCover();
                    setActiveS();
                }
                return;
            }

        }
    }*/

}