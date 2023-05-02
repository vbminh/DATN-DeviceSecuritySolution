package View;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;

import com.application.devicesecurity.R;

import service.DSSForegroundService;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_SYSTEM_ALERT_WINDOWN = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //KnoxUtils.setActiveAdmin();
        setActiveS();
    }

    private void setActiveS() {
        //Khoi chay service trong nen
        Intent serviceIntent = new Intent(this,  DSSForegroundService.class);
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

}