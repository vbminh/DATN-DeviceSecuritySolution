package service;

import static android.app.PendingIntent.FLAG_IMMUTABLE;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.devicesecurity.R;

import View.MainActivity;

public class LoginForegroundService extends Service {
    private static final String CHANNEL_ID = "foregroundServiceChannel";

    @Override
    public void onCreate() {
        super.onCreate();
        //Tao notification cho Foreground Service
        createNotificationChannel();
        Notification notification = createNotification();
        startForeground(1, notification);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //Xu ly su kien mo khoa man hinh
        Intent loginIntent = new Intent(LoginForegroundService.this, LoginReminderService.class);
        startService(loginIntent);

        Intent watermarkIntent = new Intent(LoginForegroundService.this, FloatingIconService.class);
        startService(watermarkIntent);

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Intent intent = new Intent(this, LoginForegroundService.class);
        startService(intent);
        Intent watermarkIntent = new Intent(LoginForegroundService.this, FloatingIconService.class);
        startService(watermarkIntent);
        super.onDestroy();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        Intent intent = new Intent(this, LoginForegroundService.class);
        startService(intent);
        Intent watermarkIntent = new Intent(LoginForegroundService.this, FloatingIconService.class);
        startService(watermarkIntent);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private Notification createNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentText("DSS is running")
                .setSmallIcon(R.drawable.icon_app)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        //Tao intent de khoi dong ung dung khi nguoi dung nhan vao notification
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 , notificationIntent, FLAG_IMMUTABLE);
        builder.setContentIntent(pendingIntent);

        return  builder.build();
    }

    //Tao Notification Channel cho Foreground Service
    private void createNotificationChannel() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Foreground Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
