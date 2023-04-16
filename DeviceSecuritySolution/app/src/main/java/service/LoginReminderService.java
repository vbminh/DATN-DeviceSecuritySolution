package service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.devicesecurity.R;

import View.LoginActivity;

public class LoginReminderService extends Service {
    private Handler handler;
    private Runnable runnable;

    @Override
    public void onCreate() {
        super.onCreate();
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                //Kiem tra trang thai dang nhap
                boolean isLoggedIn = checkLoggedin();
                if(!isLoggedIn) {
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
    }

    private Notification createNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "CHANNEL_ID")
                .setSmallIcon(R.drawable.icon_app)
                .setContentText("Ban chua dang nhap")
                .setPriority(NotificationCompat.PRIORITY_LOW);

        Intent in = new Intent(this, LoginActivity.class);
        PendingIntent penIn = PendingIntent.getActivity(this,
                0, in, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        return builder.build();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

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

    private boolean checkLoggedin() {
        SharedPreferences sharedPreferences = getSharedPreferences("my_app_prefs", Context.MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);

        return isLoggedIn;
    }
}
