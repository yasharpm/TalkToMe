package com.yashoid.talktome.util;

import android.content.Context;

import com.yashoid.talktome.R;

import java.util.Date;

public class TimeUtil {

    private static final long ONE_SECOND = 1000L;
    private static final long ONE_MINUTE = 60L * ONE_SECOND;
    private static final long ONE_HOUR = 60L * ONE_MINUTE;
    private static final long ONE_DAY = 24L * ONE_HOUR;

    private static final long MINIMUM_TIME_DIFFERENCE = 10L * ONE_SECOND;
    private static final long MAXIMUM_TIME_DIFFERENCE = 10L * ONE_DAY;

    public static String getRelativeTime(long time, Context context) {
        long diff = System.currentTimeMillis() - time;

        if (diff < MINIMUM_TIME_DIFFERENCE) {
            return context.getString(R.string.timeutil_momentsago);
        }

        if (diff < ONE_MINUTE) {
            return context.getString(R.string.timeutil_secondsago, (diff / ONE_MINUTE));
        }

        if (diff < ONE_HOUR) {
            return context.getString(R.string.timeutil_minutesago, (diff / ONE_MINUTE));
        }

        if (diff < ONE_DAY) {
            return context.getString(R.string.timeutil_hoursago, (diff / ONE_HOUR));
        }

        if (diff < MAXIMUM_TIME_DIFFERENCE) {
            return context.getString(R.string.timeutil_daysago, (diff / ONE_DAY));
        }

        return getAbsoluteTime(time, context);
    }

    public static String getAbsoluteTime(long time, Context context) {
        JalaliCalendar calendar = JalaliCalendar.newInstanceFromGregorian(new Date(time));;

        int year = calendar.get(JalaliCalendar.YEAR);
        int month = calendar.get(JalaliCalendar.MONTH);
        int day = calendar.get(JalaliCalendar.DAY_OF_MONTH);

        return day + " " + getMonthName(month, context) + " " + getFormattedYear(year);
    }

    private static String getMonthName(int month, Context context) {
        return context.getResources().getStringArray(R.array.month_names)[month];
    }

    private static String getFormattedYear(int year) {
        year = year % 100;

        if (year == 0) {
            return "1400";
        }

        if (year < 10) {
            return "0" + year;
        }

        return "" + year;
    }

}
