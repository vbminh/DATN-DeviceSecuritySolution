package service;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.util.Log;
import android.view.IWindowManager;

import androidx.annotation.NonNull;

import com.android.internal.widget.LockPatternUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import algorithm.DES;
import module.InformationDevice;

public class FCMService extends FirebaseMessagingService {
    private static final String TAG = "FCMService1";

    private InformationDevice informationDevice;

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        Log.e(TAG, "onNewToken: "+ token );
        Log.e(TAG, "onNewToken: "+ Build.getSerial() );

        SharedPreferences sharedPreferences = getSharedPreferences("my_app_prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("policy", "11111");
        editor.apply();

        informationDevice = new InformationDevice("11111" , Build.getSerial() , token);
        pushNewInformationToServer();
    }

    private void pushNewInformationToServer() {
        Map<String, Object> info = new HashMap<>();
        info.put("policy", informationDevice.getPolicy());
        info.put("serialNo", informationDevice.getSerialNo());
        info.put("token", informationDevice.getToken());
        info.put("Model Name", Build.MODEL);
        info.put("Android SDK", Build.VERSION.SDK_INT);
        info.put("Android Version", Build.VERSION.RELEASE);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("informations")
                .document(informationDevice.getSerialNo())
                .set(info)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.e(TAG, "onSuccess: ");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "onFailure: ");
                    }
                });

        Map<String, Object> loca = new HashMap<>();
        loca.put("serialNo",Build.getSerial());
        loca.put("latitude", Double.MIN_VALUE);
        loca.put("longitude", Double.MIN_VALUE);

        db.collection("locations")
                .document(informationDevice.getSerialNo())
                .set(loca)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.e(TAG, "onSuccess: ");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "onFailure: ");
                    }
                });

        Map<String, Object> user = new HashMap<>();
        user.put("username",Build.getSerial());
        user.put("password", "123");

        db.collection("user")
                .document(informationDevice.getSerialNo())
                .set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.e(TAG, "onSuccess: ");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "onFailure: ");
                    }
                });
    }



    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if(remoteMessage.getData().size() > 0)
        {
            String action = remoteMessage.getData().get("title");
            Log.e(TAG, "onMessageReceived: "+ action );
            if(action.equals("lockDevice")) {
                lockDevice("246800");
            }
            else if(action.equals("unLockDevice"))
            {
                Log.e(TAG, "onMessageReceived: +unlock" );
                unLockDevice();
            }
            else if(action.equals("updatePolicy"))
            {
                String policy = remoteMessage.getData().get("policy");
                Log.e(TAG, "onMessageReceived: "+policy );
                startUpdateServices(policy);
                updatePolicy(policy);
            }
        }
    }

    private void startUpdateServices(String policy) {
        SharedPreferences sharedPreferences = getSharedPreferences("my_app_prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("policy", policy);
        editor.apply();

        //Watermark
        Intent watermarkIntent = new Intent(this, PrintWatermarkService.class);
        startService(watermarkIntent);

        //Block App
        Intent blockAppIntent = new Intent(this, BlockAppsService.class);
        startService(blockAppIntent);

        //Tracking Device
        Intent trackingLocationIntent = new Intent(this, LocationTrackingService.class);
        startService(trackingLocationIntent);
    }

    private void updatePolicy(String policy) {
        sendDatatoServer(policy);
    }

    private void sendDatatoServer(String policy) {
        DocumentReference dr = FirebaseFirestore.getInstance().collection("informations")
                .document(Build.getSerial());
        dr.update("policy" , policy).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.e(TAG, "onSuccess: " );
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "onFailure: " + e.toString() );
            }
        });
    }

    public void lockDevice(String password) {
        LockPatternUtils mLockPatternUtils = new LockPatternUtils(this);
        byte[] sha1;
        try {
            sha1 = MessageDigest.getInstance("SHA-1").digest(password.getBytes());
            mLockPatternUtils.saveRemoteLockPassword(LockPatternUtils.FMM_LOCK, sha1, 1000);
            IWindowManager winMgr = IWindowManager.Stub.asInterface(ServiceManager.getService(Context.WINDOW_SERVICE));
            winMgr.lockNow(null);
        } catch (NoSuchAlgorithmException e) {
            Log.e(TAG, e.getMessage());
        } catch (RemoteException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    private void unLockDevice()
    {
        Log.e(TAG, "unlock");
        LockPatternUtils mLockPatternUtils = new LockPatternUtils(this);
        mLockPatternUtils.saveRemoteLockPassword(LockPatternUtils.FMM_LOCK, null, 1000);
        mLockPatternUtils.setLockScreenDisabled(true, 1000);
    }

}