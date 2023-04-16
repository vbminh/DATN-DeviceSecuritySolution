package View;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;

import service.FloatingIconService;
import service.LoginForegroundService;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_SYSTEM_ALERT_WINDOWN = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //KnoxUtils.setActiveAdmin();
        //setPermission();
        setActiveS();
        finish();
    }

    private void setActiveS() {
        //Khoi chay service trong nen
        Intent serviceIntent = new Intent(this,  LoginForegroundService.class);
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
    }

    private void setPermission() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this))
        {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent,REQUEST_CODE_SYSTEM_ALERT_WINDOWN);
        }
        else {
            Intent intent = new Intent(this, FloatingIconService.class);
            startService(intent);

        }
    }

}