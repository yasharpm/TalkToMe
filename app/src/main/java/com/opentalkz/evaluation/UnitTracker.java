package com.opentalkz.evaluation;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class UnitTracker {

    private static final String PREFERENCES = "com.opentalkz.talktome.evaluation_";

    private static final String KEY_LAST_CHECKED_TIME = "last_checked_time";
    private static final String KEY_LAST_EVENT_TIME = "last_event_time";
    private static final String KEY_EVENT_COUNT = "event_count";
    private static final String KEY_EVENTS = "events";

    private static final long ONE_DAY = 24L * 60L * 60L * 1000L;

    private SharedPreferences mPrefs;

    private boolean mExplicitAB;

    private String mEventName;

    private String mLastTimeTag;
    private String mCountTag;
    private String mThirtyDaysTag;
    private String mSevenDaysTag;

    private long mLastCheckedTime;
    private long mLastEventTime;
    private int mEventCount;
    private List<Integer> mEvents;

    UnitTracker(Context context, String eventName, boolean explicitAB,
                String lastTimeTag, String countTag, String thirtyDaysTag, String sevenDaysTag) {
        mPrefs = context.getSharedPreferences(PREFERENCES + eventName, Context.MODE_PRIVATE);

        mExplicitAB = explicitAB;

        mEventName = eventName;

        mLastTimeTag = lastTimeTag;
        mCountTag = countTag;
        mThirtyDaysTag = thirtyDaysTag;
        mSevenDaysTag = sevenDaysTag;

        mLastCheckedTime = mPrefs.getLong(KEY_LAST_CHECKED_TIME, 0);
        mLastEventTime = mPrefs.getLong(KEY_LAST_EVENT_TIME, 0);
        mEventCount = mPrefs.getInt(KEY_EVENT_COUNT, 0);

        String sEvents = mPrefs.getString(KEY_EVENTS, null);

        if (sEvents == null) {
            mEvents = new ArrayList<>(Collections.nCopies(30, 0));
        }
        else {
            try {
                JSONArray jEvents = new JSONArray(sEvents);

                mEvents = new ArrayList<>(jEvents.length());

                for (int i = 0; i < jEvents.length(); i++) {
                    mEvents.add(jEvents.getInt(i));
                }
            } catch (JSONException e) { }
        }

        new Handler().post(new Runnable() {

            @Override
            public void run() {
                init();
            }

        });
    }

    private void init() {
        boolean hasChange = handleDayChange();

        if (hasChange) {
            updateCache();
            updateTags();
        }
    }

    void onEvent() {
        onEvent(true);
    }

    void onEvent(boolean sendEvent) {
        handleDayChange();

        mLastEventTime = System.currentTimeMillis();
        mEventCount++;
        mEvents.set(mEvents.size() - 1, mEvents.get(mEvents.size() - 1) + 1);

        updateCache();
        updateTags();

        if (sendEvent) {
            if (mExplicitAB) {
                Eval.sendABEvent(mEventName);
            }
            else {
                Eval.sendEvent(mEventName);
            }
        }
    }

    private boolean handleDayChange() {
        long now = System.currentTimeMillis();

        long passedDays = getPassedDays(mLastCheckedTime, now);

        boolean hasChange = false;

        if (passedDays > 30) {
            for (int i = 0; i < mEvents.size(); i++) {
                mEvents.set(i, 0);
            }

            hasChange = true;
        }
        else {
            while (passedDays > 0) {
                hasChange = true;

                shiftEvents();

                passedDays--;
            }
        }

        mLastCheckedTime = now;
        mPrefs.edit().putLong(KEY_LAST_CHECKED_TIME, mLastCheckedTime).apply();

        return hasChange;
    }

    private void shiftEvents() {
        for (int i = 0; i < mEvents.size() - 1; i++) {
            mEvents.set(i, mEvents.get(i + 1));
        }

        mEvents.set(mEvents.size() - 1, 0);
    }

    private void updateCache() {
        JSONArray jEvents = new JSONArray();

        for (int dayCount: mEvents) {
            jEvents.put(dayCount);
        }

        mPrefs.edit()
                .putLong(KEY_LAST_EVENT_TIME, mLastEventTime)
                .putInt(KEY_EVENT_COUNT, mEventCount)
                .putString(KEY_EVENTS, jEvents.toString())
                .apply();
    }

    private void updateTags() {
        if (mLastTimeTag != null) {
            Eval.setTag(mLastTimeTag, String.valueOf(mLastEventTime / 1000));
        }

        if (mCountTag != null) {
            Eval.setTag(mCountTag, String.valueOf(mEventCount));
        }

        if (mSevenDaysTag != null) {
            int count = 0;

            for (int i = mEvents.size() - 1; i >= mEvents.size() - 7; i--) {
                count += mEvents.get(i);
            }

            Eval.setTag(mSevenDaysTag, String.valueOf(count));
        }

        if (mThirtyDaysTag != null) {
            int count = 0;

            for (int dayCount: mEvents) {
                count += dayCount;
            }

            Eval.setTag(mThirtyDaysTag, String.valueOf(count));
        }
    }

    private static long getPassedDays(long start, long end) {
        if (end < start) {
            return 0;
        }
        else {
            return millisToDays(end) - millisToDays(start);
        }
    }

    private static long millisToDays(long time) {
        return time / ONE_DAY;
    }

}
