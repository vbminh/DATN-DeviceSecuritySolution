package com.example.dssserver;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class UpdatePolicy {
    static final private String serverKey = "key=" + "AAAA2J89iNg:APA91bHY4NZGfiy6rrGPYkJisVPgLQHAtXhrx-hG-3J0Zq4OjmvfDGmdguVapwfD4SNZycZVgDyyRH2QOFEmNH-KEKLEbu-WEGgze9By4t2kB0BUkpbAD7ObUL5ceUNSkuUe75EhCMY8";
    static final private String contentType = "application/json";
    static final private String FCM_API = "https://fcm.googleapis.com/fcm/send";
    private static InformationDevice informationDevice;
    private static String TOPIC;
    private static String NOTIFICATION_TITLE;
    private static String TAG = "UpdatePolicy";

    static void loadDataFromServer()
    {
        String serialNo = "R3CN811NFNM";
        FirebaseFirestore.getInstance().collection("information")
                .whereEqualTo("serialNo", serialNo)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful())
                        {
                            for(QueryDocumentSnapshot doc : task.getResult())
                            {
                                informationDevice = new InformationDevice(doc.getString("policy"),
                                        doc.getString("serialNo"),
                                        doc.getString("token"));
                            }
                        }
                    }
                });
    }

    public static void postNotification(Context context, String policy)
    {
        //informationDevice = new InformationDevice("11111", )
        TOPIC = "c-WTtBwpTWif131M2sc7Hw:APA91bEvqIaQPi54LhwUR_FpKVyxuxhXnCXE-7wiwTchU88dwEJNQG9fGwYMnAcS8O_XhkDzePzU17Jr6JsZdDzqGAc9kuABfyWPu0o8TEVNCLSUu5iYzTtx0TA4XRheA6Ijl-orVG78"; //topic must match with what the receiver subscribed to
        NOTIFICATION_TITLE = "updatePolicy";
        JSONObject notification = new JSONObject();
        JSONObject notifcationBody = new JSONObject();
        try{
            notifcationBody.put("title" , NOTIFICATION_TITLE);
            notifcationBody.put("policy", policy);
            notification.put("to", TOPIC);
            notification.put("data", notifcationBody);
        }
        catch (JSONException e)
        {
            Log.e(TAG, "postNotification: "+ "didn't work" );
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
}
