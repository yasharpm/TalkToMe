package com.opentalkz.notification;

import android.app.Activity;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import androidx.annotation.WorkerThread;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.opentalkz.R;
import com.opentalkz.ui.StartUpActivity;

public class Notifier {

    public static final int NOTIFICATION_ID_DEFAULT = 0;
    public static final String CHANNEL_EVENTS = "Events";

    public static final int NOTIFICATION_ID_POST = 1;
    public static final String CHANNEL_POST = "RandomPosts";

    private static int mOpenActivitiesCount = 0;

    public static void init(Application application) {
        application.registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {

            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                mOpenActivitiesCount++;
            }

            @Override public void onActivityStarted(Activity activity) { }

            @Override public void onActivityResumed(Activity activity) { }

            @Override public void onActivityPaused(Activity activity) { }

            @Override public void onActivityStopped(Activity activity) { }

            @Override public void onActivitySaveInstanceState(Activity activity, Bundle outState) { }

            @Override
            public void onActivityDestroyed(Activity activity) {
                mOpenActivitiesCount--;
            }

        });
    }

    @WorkerThread
    public static void notify(final Context context, final String title, final String body, final Uri uri) {
        if (mOpenActivitiesCount > 0) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {

                @Override
                public void run() {
                    Toast.makeText(context, title + "\n" + body, Toast.LENGTH_LONG).show();
                }

            });
            return;
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_EVENTS,
                    context.getString(R.string.event_notification_channel),
                    NotificationManager.IMPORTANCE_DEFAULT
            );

            NotificationManagerCompat.from(context).createNotificationChannel(channel);
        }

        Intent intent = StartUpActivity.newIntent(context);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

        if (uri != null) {
            intent.setData(uri);
        }

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new NotificationCompat.Builder(context, CHANNEL_EVENTS)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setColor(ContextCompat.getColor(context, R.color.themeColor))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_SOCIAL)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setContentText(body)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setOngoing(true)
                .build();

        NotificationManagerCompat.from(context).notify(NOTIFICATION_ID_DEFAULT, notification);
    }

}
