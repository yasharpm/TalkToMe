package com.opentalkz.notification.post;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.opentalkz.Preferences;

public class RefreshHandler {

    private static final String KEY_LAST_REFRESH_TIME = "last_refresh_time";
    private static final String KEY_REFRESH_PERIOD = "refresh_period";

    public static long getRefreshPeriod(Context context, String name) {
        return Preferences.get(context).readLong(getRefreshPeriodKey(name), 0);
    }

    public static void setRefreshPeriod(Context context, String name, long period) {
        Preferences.get(context).write(getRefreshPeriodKey(name), period);
    }

    public static long getLastRefreshTime(Context context, String name) {
        return Preferences.get(context).readLong(getLastRefreshTimeKey(name), 0);
    }

    public static void setLastRefreshTime(Context context, String name, long time) {
        Preferences.get(context).write(getLastRefreshTimeKey(name), time);
    }

    private Context mContext;

    private String mName;

    private String mRefreshClassName;

    public RefreshHandler(Context context, String name, Class refreshClass) {
        mContext = context;

        mName = name;

        mRefreshClassName = refreshClass.getName();
    }

    public boolean shouldRefresh() {
        long lastRefreshTime = getLastRefreshTime();
        long refreshPeriod = getRefreshPeriod();

        if (refreshPeriod == 0) {
            return false;
        }

        long now = System.currentTimeMillis();

        return now - lastRefreshTime >= refreshPeriod;
    }

    public void scheduleNextRefresh() {
        long nextRefreshTime = getNextRefreshTime();

        if (nextRefreshTime != 0) {
            Intent refreshIntent = RefreshReceiver.getIntent(mContext, mRefreshClassName);

            PendingIntent refreshPendingIntent = PendingIntent.getBroadcast(
                    mContext,
                    0,
                    refreshIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT
            );

            AlarmManager am = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);

            if (am != null) {
                am.set(AlarmManager.RTC, nextRefreshTime, refreshPendingIntent);
            }
        }
    }

    public long getNextRefreshTime() {
        long refreshPeriod = getRefreshPeriod();

        if (refreshPeriod == 0) {
            return 0;
        }

        return System.currentTimeMillis() + refreshPeriod;
    }

    public long getRefreshPeriod() {
        return getRefreshPeriod(mContext, mName);
    }

    public long getLastRefreshTime() {
        return getLastRefreshTime(mContext, mName);
    }

    public void setRefreshPeriod(long period) {
        setRefreshPeriod(mContext, mName, period);
    }

    public void onRefreshed() {
        setLastRefreshTime(mContext, mName, System.currentTimeMillis());
    }

    public void reset() {
        setRefreshPeriod(0);
        setLastRefreshTime(mContext, mName, 0);
    }

    private static String getLastRefreshTimeKey(String name) {
        return name + "." + KEY_LAST_REFRESH_TIME;
    }

    private static String getRefreshPeriodKey(String name) {
        return name + "." + KEY_REFRESH_PERIOD;
    }

}
