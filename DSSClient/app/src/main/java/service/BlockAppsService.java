package service;

import android.app.ActivityManager;
import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import util.KnoxUtils;

public class BlockAppsService extends Service {
    String policy;

    private final static String TAG = "BlockAppService";
    private static final List<String> blacklist = new ArrayList<String>();

    {
        blacklist.add("com.facebook.katana");
        blacklist.add("com.google.android.apps.youtube.music");
        blacklist.add("com.android.chrome");
        blacklist.add("com.google.android.youtube");
    }
    private ActivityManager activityManager;

    private UsageStatsManager usageStatsManager;

    private Handler handler;
    private Runnable runnable;

    @Override
    public void onCreate() {
        super.onCreate();

    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        init();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);
    }

    private void init() {
        SharedPreferences sharedPreferences = getSharedPreferences("my_app_prefs", Context.MODE_PRIVATE);
        policy = sharedPreferences.getString("policy", "11101");

        Log.e(TAG, policy);

        if(policy.charAt(2) == '1') {
            usageStatsManager = (UsageStatsManager)  getSystemService(Context.USAGE_STATS_SERVICE);
            activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);

            handler = new Handler();
            runnable = new Runnable() {
                @Override
                public void run() {
                    blockApp();
                    //Goi lai sau 2s
                    handler.postDelayed(this, 1000);
                }
            };
            //Bat dau dinh ki goi
            handler.post(runnable);
        }
        else
            stopSelf();
    }

    private void blockApp() {
        long time = System.currentTimeMillis();
        List<UsageStats> runningApps = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, time - 1000, time);
        Log.e("BlockAppService", runningApps.size() + "");
        for(UsageStats app : runningApps) {
            if(blacklist.contains(app.getPackageName())) {
                Log.e("BlockAppService", app.getPackageName());
                KnoxUtils.stopApp(app.getPackageName());
            }
        }
    }
}
