package util;

import android.content.ComponentName;

import com.samsung.android.knox.EnterpriseDeviceManager;
import com.samsung.android.knox.application.ApplicationPolicy;
import com.samsung.android.knox.kiosk.KioskMode;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import App.myApplication;
import receiver.EnterpriseDeviceAdminReceiver;

public class KnoxUtils {

    private static final String TAG = KnoxUtils.class.getSimpleName();
    private static final int BACK_KEY = 4;
    private static final int HOME_KEY = 3;
    private static final int RECENT_KEY = 187;
    private static final int POWER_KEY = 26;
    private static final int VOLUME_UP_KEY = 24;
    private static final int VOLUME_DOWN_KEY = 25;
    private static final int BIGBY_KEY = 1082;

    private static EnterpriseDeviceManager getEDM() {
        EnterpriseDeviceManager edm = null;
        try {
            edm = EnterpriseDeviceManager.getInstance(myApplication.getInstance());
        } catch (Throwable e) {
            //  Log.e(TAG, e.getMessage(), e);
        }
        return edm;
    }

    private static ComponentName getComponentName() {
        return new ComponentName(myApplication.getInstance(), EnterpriseDeviceAdminReceiver.class);
    }

    private static boolean isAdminActive() {
        return getEDM().isAdminActive(getComponentName());
    }

    public static void setActiveAdmin() {
        if (!isAdminActive()) {
            Method method;
            try {
                method = EnterpriseDeviceManager.class.getMethod("setActiveAdmin",
                        ComponentName.class, boolean.class);
                method.invoke(getEDM(), getComponentName(), false);
            } catch (NoSuchMethodException e) {
                // Log.e(TAG, e.getMessage(), e);
            } catch (IllegalArgumentException e) {
                //  Log.e(TAG, e.getMessage(), e);
            } catch (IllegalAccessException e) {
                //  Log.e(TAG, e.getMessage(), e);
            } catch (InvocationTargetException e) {
                //  Log.e(TAG, e.getMessage(), e);
            }
        }
    }

    public static void setAdminRemovable(boolean removable) {
        getEDM().setAdminRemovable(removable);
    }

    public static void setBackKeyState(boolean state) {
        setKeyState(BACK_KEY, state);
    }

    public static void setHomeKeyState(boolean state) {
        setKeyState(HOME_KEY, state);
    }
    public static void setBigbyState(boolean state) {
        setKeyState(BIGBY_KEY, state);
    }

    public static void setRecentKeyState(boolean state) {
        setKeyState(RECENT_KEY, state);
    }

    public static void setVolumeKeyState(boolean state) {
        setKeyState(VOLUME_UP_KEY, state);
        setKeyState(VOLUME_DOWN_KEY, state);
    }

    public static void setPowerKeyState(boolean state) {
        setKeyState(POWER_KEY, state);
    }

    private static void setKeyState(int keyNumber, boolean state) {
        KioskMode kioskModeService = getEDM().getKioskMode();
        List<Integer> blockKeyList = new ArrayList<>();
        blockKeyList.add( Integer.valueOf(keyNumber));
        kioskModeService.allowHardwareKeys(blockKeyList, state);
    }

    public static boolean allowStatusBarExpansion(boolean allow) {
        return getEDM().getRestrictionPolicy().allowStatusBarExpansion(allow);
    }

    public static boolean allowFactoryReset(boolean allow) {
        return getEDM().getRestrictionPolicy().allowFactoryReset(allow);
    }

    public static boolean allowFirmwareRecovery(boolean allow) {
        return getEDM().getRestrictionPolicy().allowFirmwareRecovery(allow);
    }

    public static boolean allowPowerOff(boolean allow) {
        return getEDM().getRestrictionPolicy().allowPowerOff(allow);
    }

    public static boolean allowDeveloperMode(boolean allow) {
        return true;//getEDM().getRestrictionPolicy().allowDeveloperMode(allow);
    }

    public static void setApplicationForceStopDisable(boolean disable, String packageName) {
        if (null == packageName) {
            return;
        }

        List<String> list = new ArrayList<>();
        list.add(packageName);
        if (disable) {
            addPackagestoBlackList(list);
        } else {
            removePackagestoBlackList(list);
        }
    }

    private static void addPackagestoBlackList(List<String> list) {
        ApplicationPolicy applicationPolicy = getEDM().getApplicationPolicy();
        try {
            applicationPolicy.addPackagesToClearCacheBlackList(list);
            applicationPolicy.addPackagesToClearDataBlackList(list);
            applicationPolicy.addPackagesToForceStopBlackList(list);
        } catch (SecurityException e) {
            // Log.e(TAG, "SecurityException: \n" + e.getMessage(), e);
        }
    }

    public static void removePackagestoBlackList(List<String> list) {
        ApplicationPolicy applicationPolicy = getEDM().getApplicationPolicy();
        try {
            applicationPolicy.removePackagesFromForceStopBlackList(list);
            applicationPolicy.removePackagesFromClearDataBlackList(list);
            applicationPolicy.removePackagesFromClearCacheBlackList(list);
        } catch (SecurityException e) {
            //Log.e(TAG, "SecurityException: \n" + e.getMessage(), e);
        }
    }
    public static boolean setCameraState(boolean state) {
        return getEDM().getRestrictionPolicy().setCameraState(state);
    }

    public static boolean stopApp(String pakageName) {
        return getEDM().getApplicationPolicy().stopApp(pakageName);
    }

    public static void setApplicationUninstallationDisabled(String pakageName) {
        getEDM().getApplicationPolicy().setApplicationUninstallationDisabled(pakageName);
    }

}

