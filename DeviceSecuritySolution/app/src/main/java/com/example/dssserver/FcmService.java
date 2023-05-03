package com.example.dssserver;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class FcmService extends FirebaseMessagingService {
    private static final String TAG = "MyFirebaseService";

    private InformationDevice informationDevice;
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        Log.e(TAG, "onNewToken: "+token );
        //informationDevice = new InformationDevice("00000" , Build.getSerial() , token);
    }

    private final String ADMIN_CHANNEL_ID = "admin_channel";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        NotificationManager notificationManager = (NotificationManager)
                getSystemService(Context.NOTIFICATION_SERVICE);
        int notificationID = new Random().nextInt(3000);
   /*
    Apps targeting SDK 26 or above (Android O) must implement notification channels and add their notifications to at least one of them.
Therefore, confirm if the version is Oreo or higher, then setup notification channel
   */
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            setupChannels(notificationManager);
        }


        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher_background);
        Uri notificationSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, ADMIN_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setLargeIcon(largeIcon)
                .setContentTitle(remoteMessage.getData().get("title"))
                .setContentText(remoteMessage.getData().get("message"))
                .setAutoCancel(true)
                .setSound(notificationSoundUri);


        notificationManager.notify(notificationID, notificationBuilder.build());
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setupChannels(NotificationManager notificationManager) {
        CharSequence adminChannelName = "New notification";
        String adminChannelDescription = "Device to device notification ";

        NotificationChannel adminChannel;
        adminChannel = new NotificationChannel(ADMIN_CHANNEL_ID,
                adminChannelName,
                NotificationManager.IMPORTANCE_HIGH);

        adminChannel.setDescription(adminChannelDescription);
        adminChannel.enableLights(true);
        adminChannel.setLightColor(Color.RED);
        adminChannel.enableVibration(true);

        if (notificationManager != null) {
            notificationManager.createNotificationChannel(adminChannel);
        }
    }

    private void pushNewInformationToServer() {
        Map<String , Object> info = new HashMap<>();
        info.put("policy" , informationDevice.getPolicy());
        info.put("serialNo" , informationDevice.getSerialNo());
        info.put("token" , informationDevice.getToken());
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("information")
                .document(informationDevice.getSerialNo())
                .set(info)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.e(TAG, "onSuccess: " );
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "onFailure: " );
                    }
                });
    }
}
