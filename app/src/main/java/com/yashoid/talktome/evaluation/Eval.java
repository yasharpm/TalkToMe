package com.yashoid.talktome.evaluation;

import android.app.Activity;
import android.app.Application;
import android.os.Handler;

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
    private static EventTracker mTracker;

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

        mTracker = new EventTracker(application);

        // Tracker sets some tags using handler post.
        // We want to attempt the fetch after the tags are set.
        new Handler().post(new Runnable() {

            @Override
            public void run() {
                mResources.fetch(new IABResources.OnFetchResultCallback() {

                    @Override
                    public void onFetchResult(IABResources.FetchResult fetchResult) {
                        if (DEBUG && fetchResult.isSuccessful()) {
                            fetchResult.activateNow();
                        }
                    }

                });
            }

        });
    }

    public static IABResources getResources() {
        return mResources;
    }

    public static void trackEvent(String event, Object... payload) {
        mTracker.trackEvent(event, payload);
    }

    public static void setTag(String name, String value) {
        mAnalytics.setUserProperty(name, value);
        mResources.setTag(name, value);
    }

    public static void setCurrentScreen(Activity activity, String name) {
        mAnalytics.setCurrentScreen(activity, name, null);
    }

    static void sendEvent(String event) {
        mAnalytics.logEvent(event, null);
        sendABEvent(event);
    }

    static void sendABEvent(String event) {
        mResources.recordEvent(event);
    }

}
