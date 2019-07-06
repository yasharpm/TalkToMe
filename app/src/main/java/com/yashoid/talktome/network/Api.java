package com.yashoid.talktome.network;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.provider.Settings;

public class Api implements ApiConstants {

    private static final String PREFERENCES = "com.yashoid.talktome.network";

    private static Api mInstance = null;

    public static Api get(Context context) {
        if (mInstance == null) {
            mInstance = new Api(context);
        }

        return mInstance;
    }

    private SharedPreferences mPrefs;

    private Api(Context context) {
        mPrefs = context.getApplicationContext().getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);

        String deviceId = getDeviceId();

        if (deviceId == null) {
            deviceId = Build.FINGERPRINT + " id:" + Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);

            mPrefs.edit().putString(DEVICE_ID, deviceId).commit();
        }
    }

    public String getDeviceId() {
        return mPrefs.getString(DEVICE_ID, null);
    }

    synchronized public String getAduId() {
        return mPrefs.getString(ADU_ID, null);
    }

    synchronized protected void setAduId(String id) {
        mPrefs.edit().putString(ADU_ID, id).commit();
    }

}
