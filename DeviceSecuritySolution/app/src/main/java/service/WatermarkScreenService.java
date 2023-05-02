package service;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.Nullable;
import com.application.devicesecurity.R;

import java.util.Base64;

import widget.myCustomScreen;

public class WatermarkScreenService extends Service {

    private View view;
    private WindowManager mWindowManager;
    private myCustomScreen customScreen;
    private WindowManager.LayoutParams params;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initView();
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    private void initView() {
        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        createWatermark();
        showWatermark();
    }

    private void showWatermark() {
        mWindowManager.addView(customScreen , params);
    }

    private void createWatermark() {
        customScreen = new myCustomScreen(this);

        view = View.inflate(this, R.layout.watermark_logout_layout,customScreen);
        initWatermark();
        params = new WindowManager.LayoutParams();
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.gravity = Gravity.CENTER;
        params.format = PixelFormat.TRANSLUCENT;
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.O)
        {
            params.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        }
        params.flags = WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
        params.flags |= WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        params.flags |= WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
        params.flags |= WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
        params.flags |= WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;

    }

    private void initWatermark() {
        TextView txtView = view.findViewById(R.id.txtViewOut);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
        {
            txtView.setText(build(Build.getSerial()));
        }
        else
        {
            txtView.setText(build(Build.SERIAL));
        }
    }


    private String build(String str){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            str = Base64.getEncoder().encodeToString(str.getBytes());
        }

        return str;
    }
}