package com.yashoid.talktome.evaluation;

import android.app.Activity;
import android.app.Application;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.yashoid.network.NetworkOperator;
import com.yashoid.talktome.BuildConfig;
import com.yashoid.talktome.TTMOffice;

import ir.abmyapp.androidsdk.ABConfig;
import ir.abmyapp.androidsdk.ABResources;
import ir.abmyapp.androidsdk.IABResources;

public class Eval {

    private static final boolean DEBUG = BuildConfig.DEBUG;

    private static FirebaseAnalytics mAnalytics;
    private static IABResources mResources;

    public static void initialize(Application application) {
        mAnalytics = FirebaseAnalytics.getInstance(application);
        mAnalytics.setAnalyticsCollectionEnabled(true); // TODO !DEBUG

        ABResources.setConfiguration(
                new ABConfig.Builder()
                        .setDebug(DEBUG)
                        .setTaskManager(TTMOffice.get())
                        .setBackgroundOfficeSection(NetworkOperator.SECTION_BACKGROUND)
                        .build()
                );

        mResources = ABResources.get(application);
    }

    public static IABResources getResources() {
        return mResources;
    }

    public static void sendEvent(String event) {
        mAnalytics.logEvent(event, null);
        sendABEvent(event);
    }

    public static void sendABEvent(String event) {
        mResources.recordEvent(event);
    }

    public static void setTag(String name, String value) {
        mAnalytics.setUserProperty(name, value);
        mResources.setTag(name, value);
    }

    public static void setCurrentScreen(Activity activity, String name) {
        mAnalytics.setCurrentScreen(activity, name, null);
    }

}
