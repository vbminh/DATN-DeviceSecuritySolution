package service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.example.devicesecurity.R;

import java.util.Base64;

import widget.MyCustomView;

public class FloatingIconService extends Service {

    private View view;
    private WindowManager mWindowManager;
    private MyCustomView myCustomView;
    private WindowManager.LayoutParams params;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("onStart", "FloatingIconService");
        initView();

        return super.onStartCommand(intent, flags, startId);
    }

    private void initView() {
        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        createTextView();
    }

    private void createTextView() {
        myCustomView = new MyCustomView(this);

        //Kiem tra trang thai dang nhap
        boolean isLoggedIn = checkLoggedin();
        if(isLoggedIn) {
            view = View.inflate(this, R.layout.watermark_login_layout, myCustomView);
            initTextViewLogIn();
        }
        else {
            view = View.inflate(this, R.layout.watermark_logout_layout, myCustomView);
            initTextViewLogOut();
        }

        params = new WindowManager.LayoutParams();
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
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

        if(myCustomView.isAttachedToWindow()) {
            if(isLoggedIn)
                mWindowManager.updateViewLayout(myCustomView, params);
            else
                mWindowManager.updateViewLayout(myCustomView, params);
        }
        else {
            if(isLoggedIn)
                mWindowManager.addView(myCustomView, params);

            else
                mWindowManager.addView(myCustomView, params);
        }

    }

    private void initTextViewLogOut() {
        TextView txtView = view.findViewById(R.id.txtViewOut);
        TextView txtView2 = view.findViewById(R.id.txtViewOut2);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
        {
            txtView.setText(buildTextViewLogOut(Build.getSerial()));
            txtView2.setText(buildTextViewLogOut(Build.getSerial()));
        }
        else
        {
            txtView.setText(buildTextViewLogOut(Build.SERIAL));
            txtView2.setText(buildTextViewLogOut(Build.SERIAL));
        }
    }




    private String buildTextViewLogOut(String str){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            str = Base64.getEncoder().encodeToString(str.getBytes());
        }
        if(!TextUtils.isEmpty(str)) {
            StringBuilder builder = new StringBuilder();
            builder.append(str.charAt(0));
            for (int i = 1; i < str.length(); i++) {
                builder.append("\n"+str.charAt(i));
            }
            return builder.toString();
        }
        return "";
    }

    private void initTextViewLogIn() {
        TextView txtView = view.findViewById(R.id.txtViewOut);
        TextView txtView2 = view.findViewById(R.id.txtViewOut2);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
        {
            txtView.setText(buildTextViewLogIn(Build.getSerial()));
            txtView2.setText(buildTextViewLogIn(Build.getSerial()));
        }
        else
        {
            txtView.setText(buildTextViewLogIn(Build.SERIAL));
            txtView2.setText(buildTextViewLogIn(Build.SERIAL));
        }
    }




    private String buildTextViewLogIn(String str){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            str = Base64.getEncoder().encodeToString(str.getBytes());

        return str;
    }

    private boolean checkLoggedin() {
        SharedPreferences sharedPreferences = getSharedPreferences("my_app_prefs", Context.MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);

        return isLoggedIn;
    }
}