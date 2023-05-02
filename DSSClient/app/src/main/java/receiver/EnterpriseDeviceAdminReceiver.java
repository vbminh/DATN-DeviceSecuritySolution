package receiver;

import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class EnterpriseDeviceAdminReceiver extends DeviceAdminReceiver {

    private static final String TAG = EnterpriseDeviceAdminReceiver.class.getSimpleName();

    @Override
    public CharSequence onDisableRequested(Context context, Intent intent) {
        return super.onDisableRequested(context, intent);
    }

    @Override
    public void onDisabled(Context context, Intent intent) {
        super.onDisabled(context, intent);
        Log.d(TAG, "[EnterpriseDeviceAdminReceiver][onDisabled]");
    }

    @Override
    public void onEnabled(Context context, Intent intent) {
        super.onEnabled(context, intent);
        Log.d(TAG, "[EnterpriseDeviceAdminReceiver][onEnabled]");
    }
}

