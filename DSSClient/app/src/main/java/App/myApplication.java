package App;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class myApplication extends Application {

    private static myApplication instance;
    private static final String CHANELID = "push_notification_id";
    private static final String CHANELNAME = "push_notification";

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        createChanelID();
    }

    public static myApplication getInstance() {
        return instance;
    }

    private void createChanelID() {
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.O)
        {
            NotificationChannel notificationChannel = new NotificationChannel(CHANELID ,
                    CHANELNAME , NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }
}

