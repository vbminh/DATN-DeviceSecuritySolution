package service;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;

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

import java.util.HashMap;
import java.util.Map;

import algorithm.DES;
import module.InformationDevice;

public class FCMService extends FirebaseMessagingService {
    private static final String TAG = "MyFirebaseService";

    private InformationDevice informationDevice;
    private DES des;

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        Log.e(TAG, "onNewToken: token: "+ token + "\nSerialNo: " + "15032001" /*Build.getSerial()*/ );

        SharedPreferences sharedPreferences = getSharedPreferences("my_app_prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("policy", "11101");
        editor.apply();

        informationDevice = new InformationDevice("11101" , "15032001"/*Build.getSerial()*/ , token);
        des = new DES();
        pushNewInformationToServer();
    }

    private void pushNewInformationToServer() {
        Map<String , Object> info = new HashMap<>();
        info.put("policy" , des.Encrypt(informationDevice.getPolicy()));
        info.put("serialNo" , informationDevice.getSerialNo());
        info.put("token" , informationDevice.getToken());
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("informations")
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

        Map<String , Object> loca = new HashMap<>();
        loca.put("latitude" ,null);
        loca.put("longitude" , null);

        db.collection("locations")
                .document(informationDevice.getSerialNo())
                .set(loca)
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

        Map<String , Object> user = new HashMap<>();
        user.put("username" ,informationDevice.getSerialNo());
        user.put("password" , des.Encrypt("123"));

        db.collection("users")
                .document(informationDevice.getSerialNo())
                .set(user)
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

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
//        super.onMessageReceived(remoteMessage);
//        RemoteMessage.Notification notification = remoteMessage.getNotification();
//        if (notification != null) {
//            Log.e(TAG, "Message Notification Body: " + remoteMessage.getData().get("title"));
//            String action = notification.getTitle();
//
//            if(action.equals("lockDevice")) {
//              //  Log.e(TAG, "Message Notification Body:1 " + notification.getTitle());
//                checkDataFromServer();
//                Log.e(TAG, "Message Notification Body:2 " + notification.getTitle());
//            }
//
//        }
        if(remoteMessage.getData().size() > 0)
        {
            String action = remoteMessage.getData().get("title");
            Log.e(TAG, "onMessageReceived: "+ action );
            if(action.equals("lockDevice")) {
                checkDataFromServerLock();
            }
            else if(action.equals("unLockDevice"))
            {
                checkDataFromServerUnLock();
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

    private void checkDataFromServerLock()
    {
        String serialNo = "15032001"; //Build.getSerial();
        Log.e(TAG, "sendDataToServer: "+ serialNo );
        FirebaseFirestore.getInstance().collection("information")
                .whereEqualTo("serialNo", serialNo)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful())
                        {
                            for(QueryDocumentSnapshot doc : task.getResult())
                            {
                                String policy = doc.getString("policy");
                                String []words = policy.split("");
                                //Log.e(TAG, "getLockState: "+ words[3] );
                                int stateLockDevice = Integer.parseInt(words[3]);
                                if(stateLockDevice == 0)
                                {
                                    //lockDevice("1234");
                                    int lenght = policy.length();
                                    policy = "";
                                    for(int i = 0 ; i< lenght ;i++)
                                    {
                                        if(i == 3)
                                        {
                                            policy += '1';    continue;
                                        }
                                        policy += words[i];
                                    }
                                    Log.e(TAG, "onComplete: "+ policy );
                                    sendDatatoServer(policy);
                                }
                            }
                        }
                    }
                });
    }

    private void checkDataFromServerUnLock()
    {
        String serialNo = "15032001";//Build.getSerial();
        Log.e(TAG, "sendDataToServer: "+ serialNo );
        FirebaseFirestore.getInstance().collection("information")
                .whereEqualTo("serialNo", serialNo)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful())
                        {
                            for(QueryDocumentSnapshot doc : task.getResult())
                            {
                                String policy = doc.getString("policy");
                                String []words = policy.split("");
                                //Log.e(TAG, "getLockState: "+ words[3] );
                                int stateLockDevice = Integer.parseInt(words[3]);
                                if(stateLockDevice == 1)
                                {
                                    //unLockDevice();
                                    int lenght = policy.length();
                                    policy = "";
                                    for(int i = 0 ; i< lenght ;i++)
                                    {
                                        if(i == 3)
                                        {
                                            policy += '0';    continue;
                                        }
                                        policy += words[i];
                                    }
                                    Log.e(TAG, "onComplete: "+ policy );
                                    sendDatatoServer(policy);
                                }
                            }
                        }
                    }
                });
    }

    private void sendDatatoServer(String policy) {
        DocumentReference dr = FirebaseFirestore.getInstance().collection("information")
                .document("15032001"); //Build.getSerial());
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

    /*public void lockDevice(String password) {
        LockPatternUtils mLockPatternUtils = new LockPatternUtils(this);
        byte[] sha1;
        try {
            sha1 = MessageDigest.getInstance("SHA-1").digest(password.getBytes());
            mLockPatternUtils.saveRemoteLockPassword(LockPatternUtils.FMM_LOCK, sha1, 1000);
            IWindowManager winMgr = IWindowManager.Stub.asInterface(ServiceManager.getService(Context.WINDOW_SERVICE));
            winMgr.lockNow(null);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void unLockDevice()
    {
        LockPatternUtils mLockPatternUtils = new LockPatternUtils(this);
        mLockPatternUtils.saveRemoteLockPassword(LockPatternUtils.FMM_LOCK, null, 1000);
        mLockPatternUtils.setLockScreenDisabled(true, 1000);
    }*/

}