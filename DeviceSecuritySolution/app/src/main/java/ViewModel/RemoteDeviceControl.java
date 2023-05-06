package ViewModel;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import model.DeviceInformation;

public class RemoteDeviceControl {
    static final private String serverKey = "key=" + "AAAAp2DBRvM:APA91bHa-jmp4QUaSSXwlQyCmafGJv6jFFyuMYgM1OLxbvH3GyCvRXvr0fJfFhpuv8c4JYMV8WQu-FOHaz_r3x5Vbk2F7QvLKeHsQTbdEzx3OdEJGzSv8yPnRUZ7YErNL7Put0iH_0nq";
    static final private String contentType = "application/json";
    static final private String FCM_API = "https://fcm.googleapis.com/fcm/send";
    private static DeviceInformation deviceInformation;
    private static String TOPIC;
    private static String NOTIFICATION_TITLE;
    private static String TAG = "RemoteDeviceControl1";

    public static void postNotification(Context context , String TOPIC , String policy, String NOTIFICATION_TITLE)
    {
        Log.e(TAG, TOPIC);
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
                        Log.e(TAG, "success" );
                        Toast.makeText(context.getApplicationContext(), "Done", Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "error: " + error.getMessage());
                        Toast.makeText(context.getApplicationContext(), "Can't be applied to this device", Toast.LENGTH_SHORT).show();
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
