package App;

import android.app.Application;

public class myApplication  extends Application {

    private static myApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public static myApplication getInstance() {
        return instance;
    }
}

