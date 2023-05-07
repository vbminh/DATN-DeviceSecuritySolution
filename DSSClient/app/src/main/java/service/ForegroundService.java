package service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.application.dssclient.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import view.MainActivity;
import algorithm.DES;

public class ForegroundService extends Service {
    private Handler handler;
    private Runnable runnable;
    private String policy;
    private DES des;
    private boolean networkState;
    private static final String CHANNEL_ID = "foregroundServiceChannel";
    private static final String TAG = "foregroundService";

    @Override
    public void onCreate() {
        super.onCreate();
        //Tao notification cho Foreground Service
        createNotificationChannel();
        Notification notification = createNotification();
        startForeground(1, notification);

        handler = new Handler();
        des = new DES();
        runnable = new Runnable() {
            @Override
            public void run() {
                //Kiem tra policy
                getPolicyFromServer();
                //Goi lai sau moi phut
                handler.postDelayed(this, 60000);
            }
        };
        //Bat dau dinh ki goi
        handler.post(runnable);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //Pop-up login
        Intent loginIntent = new Intent(ForegroundService.this, LoginReminderService.class);
        startService(loginIntent);

        //Watermark
        Intent watermarkIntent = new Intent(ForegroundService.this, PrintWatermarkService.class);
        startService(watermarkIntent);

        //Block App
        Intent blockAppIntent = new Intent(ForegroundService.this, BlockAppsService.class);
        startService(blockAppIntent);

        //Tracking Device
        Intent trackingLocationIntent = new Intent(ForegroundService.this, LocationTrackingService.class);
        startService(trackingLocationIntent);

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Intent intent = new Intent(this, ForegroundService.class);
        startService(intent);
        super.onDestroy();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        Intent intent = new Intent(this, ForegroundService.class);
        startService(intent);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private Notification createNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentText("DSS is running")
                .setSmallIcon(R.drawable.secure)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        //Tao intent de khoi dong ung dung khi nguoi dung nhan vao notification
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 , notificationIntent, PendingIntent.FLAG_IMMUTABLE);
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

    private void getPolicyFromServer()
    {
        SharedPreferences sharedPreferences = getSharedPreferences("my_app_prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        boolean networkConnected = isNetworkConnected();
        if(networkConnected) {
            if(!networkState) {
                networkState = true;

                String serialNo = "15032001"; //Build.getSerial();

                Log.e(TAG, "getPolicyFromServer: " + serialNo);
                FirebaseFirestore.getInstance().collection("informations")
                        .whereEqualTo("serialNo", serialNo)
                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    if (!task.getResult().isEmpty()) {
                                        for (QueryDocumentSnapshot doc : task.getResult())
                                            policy = doc.getString("policy");

                                        editor.putString("policy", des.Decrypt(policy));
                                        editor.apply();
                                    }
                                }
                            }
                        });
            }
        }
        else {
            networkState = false;

            policy = "11101";
            editor.putBoolean("isLoggedIn", false);
            editor.putString("policy", policy);
            editor.apply();
        }


    }

    //Kiem tra ket noi mang
    public boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetwork() != null; // return true =(connected),false=(not connected)
    }
}
