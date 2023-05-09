package service;

import android.app.Service;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.application.dssclient.R;

import java.util.Base64;

import algorithm.DES;
import widget.myCustomView;

public class PrintWatermarkService extends Service {
    private View view;
    private WindowManager mWindowManager;
    private myCustomView customView;
    private WindowManager.LayoutParams params;
    private DES des;

    private String policy;
    private boolean isLoggedIn;

    private final static String TAG = "PrintWatermarkService1";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.S)
    @Override
    public void onCreate() {
        super.onCreate();
        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        des = new DES();
        customView = new myCustomView(this);
        createParams();
        createTextView();
        showIcon();
    }

    @RequiresApi(api = Build.VERSION_CODES.S)
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "onStartCommand");
        customView.removeAllViews();
        createTextView();
        updateView();
        return START_STICKY;
    }

    private void updateView() {
        mWindowManager.updateViewLayout(customView, params);
    }

    private void showIcon() {
        mWindowManager.addView(customView , params);
    }

    private void createParams() {
        params = new WindowManager.LayoutParams();
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.gravity = Gravity.CENTER;
        params.format = PixelFormat.TRANSLUCENT;
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.O)
        {
            params.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        }
        else
        {
            //params.type = WindowManager.LayoutParams.;
        }
        params.flags = WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
        params.flags |= WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        params.flags |= WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
        params.flags |= WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
        params.flags |= WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;
    }

    @RequiresApi(api = Build.VERSION_CODES.S)
    private void createTextView() {
        SharedPreferences sharedPreferences = getSharedPreferences("my_app_prefs", Context.MODE_PRIVATE);
        policy = sharedPreferences.getString("policy", "11101");
        isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);

        if(policy.charAt(1) == '1' && !isLoggedIn) {
            view = View.inflate(this, R.layout.watermark_logout_layout, customView);
            Log.e(TAG, "onStartCommand " + isLoggedIn);
        }
        else {
            view = View.inflate(this, R.layout.watermark_login_layout, customView);
            Log.e(TAG, "onStartCommand " + isLoggedIn);
        }

        initTextView();
    }

    @RequiresApi(api = Build.VERSION_CODES.S)
    private void initTextView() {
        TextView txtView = view.findViewById(R.id.txtViewOut);
        TextView txtView2 = view.findViewById(R.id.txtViewOut2);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.P)
        {
            txtView.setText(des.Encrypt("111111"));
            txtView2.setText(des.Encrypt("111111"));
        }
        else
        {
            txtView.setText(des.Encrypt(Build.SERIAL));
            txtView2.setText(des.Encrypt(Build.SERIAL));
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.S)
    private String getSerial() {
        DevicePolicyManager devicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        devicePolicyManager.setOrganizationId(DevicePolicyManager.DELEGATION_CERT_INSTALL);

        return devicePolicyManager.getEnrollmentSpecificId();
    }
}
