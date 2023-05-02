package service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import view.LoginActivity;

public class LoginReminderService extends Service {
    private Handler handler;
    private Runnable runnable;
    private String policy;
    private boolean isLoggedIn;
    private  boolean isPopupLogin;

    private final static String TAG = "LoginReminderService";

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                //Kiem tra trang thai dang nhap
                getPolicyLogin();
                if(isPopupLogin) {
                    Intent in = new Intent(LoginReminderService.this, LoginActivity.class);
                    in.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(in);
                }
                //Goi lai sau moi phut
                handler.postDelayed(this, 60000);
            }
        };
        //Bat dau dinh ki goi
        handler.post(runnable);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void getPolicyLogin()
    {
        SharedPreferences sharedPreferences = getSharedPreferences("my_app_prefs", Context.MODE_PRIVATE);
        policy = sharedPreferences.getString("policy", "11101");
        isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);
        Log.e(TAG, isLoggedIn + " " + policy);
        if(policy.charAt(0) == '0')
            isPopupLogin = false;
        else if(policy.charAt(0) == '1' && isLoggedIn)
            isPopupLogin = false;
        else
            isPopupLogin = true;
    }
}