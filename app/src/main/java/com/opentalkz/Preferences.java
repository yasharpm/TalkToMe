package com.opentalkz;

import android.content.Context;
import android.content.SharedPreferences;

public class Preferences {

    private static final String PREFERENCES = "com.opentalkz.Preferences";

    private static Preferences mInstance = null;

    public static Preferences get(Context context) {
        if (mInstance == null) {
            mInstance = new Preferences(context);
        }

        return mInstance;
    }

    private SharedPreferences mPreferences;

    private Preferences(Context context) {
        mPreferences = context.getApplicationContext().getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
    }

    public void write(String key, Object value) {
        mPreferences.edit().putString(key, value == null ? null : value.toString()).apply();
    }

    public String readString(String key) {
        return mPreferences.getString(key, null);
    }

    public int readInt(String key, int defaultValue) {
        return Integer.valueOf(mPreferences.getString(key, String.valueOf(defaultValue)));
    }

    public boolean readBoolean(String key, boolean defaultValue) {
        return Boolean.valueOf(mPreferences.getString(key, Boolean.valueOf(defaultValue).toString()));
    }

}
