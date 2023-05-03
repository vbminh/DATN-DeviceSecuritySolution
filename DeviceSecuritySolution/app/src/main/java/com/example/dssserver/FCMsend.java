package com.example.dssserver;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class FCMsend {
    static final private String FCM_API = "https://fcm.googleapis.com/fcm/send";
    static final private String serverKey = "key=" + "AAAA2J89iNg:APA91bHY4NZGfiy6rrGPYkJisVPgLQHAtXhrx-hG-3J0Zq4OjmvfDGmdguVapwfD4SNZycZVgDyyRH2QOFEmNH-KEKLEbu-WEGgze9By4t2kB0BUkpbAD7ObUL5ceUNSkuUe75EhCMY8";
    static final private String contentType = "application/json";
    private static final String TAG ="FCMSend" ;
    private static String NOTIFICATION_TITLE;

    private static String NOTIFICATION_MESSAGE;

    private static String TOPIC;

    public static void postNotification(Context context)
    {
        TOPIC = "cgAwbIT2QPW4e5GNbCS4ix:APA91bEIu-9OfkG3ths6UL_euZXDDTurlhOIfG7eXNvCsJ2VkBhNNz8uurfyFmnaAdG-tqLOCO1WBGxRRd6XMu8IcURioeDgV02-HFA625dLSsuc6fPT0Gm1MjNwJc1KDQxCI4cqY6_V"; //topic must match with what the receiver subscribed to
        NOTIFICATION_TITLE = "unLockDevice";
        NOTIFICATION_MESSAGE = "holo";

        JSONObject notification = new JSONObject();
        JSONObject notifcationBody = new JSONObject();
        try {
            notifcationBody.put("title", NOTIFICATION_TITLE);
            notifcationBody.put("message", NOTIFICATION_MESSAGE);
            notification.put("to", TOPIC);
            notification.put("data", notifcationBody);
        } catch (JSONException e) {
            Log.e(TAG, "onCreate: " + e.getMessage() );
        }
        sendNotification(notification , (Context) context);
    }

    private static void sendNotification(JSONObject notification, Context context) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(FCM_API, notification,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e(TAG, "onResponse: "+ response );
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "onResponse: "+ error );
                    }
                }){
            @Override
            public Map getHeaders() throws AuthFailureError {
                Map params = new HashMap<>();
                params.put("Authorization", serverKey);
                params.put("Content-Type", contentType);
                return params;
            }
        };
        MySingleton.getInstance(context).addToRequestQueue(jsonObjectRequest);
    }

    public static void getDataFromServer(String serialNo , Context context)
    {
        Log.e(TAG, "getDataFromServer: "+ serialNo );
        FirebaseFirestore.getInstance().collection("information")
                .whereEqualTo("serialNo", serialNo)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful())
                        {
                            for(QueryDocumentSnapshot doc : task.getResult())
                            {
                                Log.e(TAG, "onComplete: "+ doc.getString("policy") );
                                TOPIC = doc.getString("token");
                                Log.e(TAG, "onComplete: "+ TOPIC );
                                postNotification(context);
                            }
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "onFailure: "+ "didn't work" );
                    }
                });
    }


}
