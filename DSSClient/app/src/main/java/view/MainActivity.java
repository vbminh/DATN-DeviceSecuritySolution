package view;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;

import receiver.EnterpriseDeviceAdminReceiver;
import service.ForegroundService;
import util.KnoxUtils;

public class MainActivity extends AppCompatActivity {

    private final static String TAG = "MainActivity";

    private DevicePolicyManager dpm;
    private ComponentName cpn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setActiveAdmin();
        checkLocationPermission();
        FirebaseApp.initializeApp(this);
    }

    private final ActivityResultLauncher<Intent> mGetContent = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == RESULT_OK) {
                Log.e(TAG, "grant permission success");
            }
            else
                System.exit(0);
        }
    });

    private boolean setActiveAdmin() {
        dpm = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        cpn =  new ComponentName(this, EnterpriseDeviceAdminReceiver.class);
        Log.e(TAG, "setActiveAdmin");

        if (!dpm.isAdminActive(cpn)) {
            Log.e(TAG, "isActiveAdmin: false");
            // Nếu quyền chưa được cấp, hiển thị dialog yêu cầu cấp quyền.
            Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, cpn);
            intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "Please grant administrator permission to use the app.");
            mGetContent.launch(intent);
            return false;
        } else {
            return true;
        }
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
        }
        else {//Chuyen den giao dien trang chu
            Intent homeIntent = new Intent(MainActivity.this, HomeActivity.class);
            homeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(homeIntent);
        }
        finish();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                new AlertDialog.Builder(this)
                        .setTitle("Location Permission")
                        .setMessage("Allow DSS Server to access this device's location")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Log.e(TAG, "check permission");
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
            startApp();
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
                        Log.e(TAG, "agree");
                        startApp();
                    }

                } else {
                    Toast.makeText(MainActivity.this, "Can't open DSS", Toast.LENGTH_SHORT).show();
                    System.exit(0);
                }
                return;
            }

        }
    }
}