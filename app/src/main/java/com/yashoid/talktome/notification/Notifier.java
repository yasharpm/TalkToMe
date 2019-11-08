package com.yashoid.talktome.notification;

import android.app.Activity;
import android.app.Application;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import androidx.annotation.WorkerThread;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.yashoid.talktome.R;
import com.yashoid.talktome.ui.StartUpActivity;

public class Notifier {

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
    public static void notify(final Context context, final String title, final String body) {
        if (mOpenActivitiesCount > 0) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {

                @Override
                public void run() {
                    Toast.makeText(context, title + "\n" + body, Toast.LENGTH_LONG).show();
                }

            });
            return;
        }

        String channelId = context.getString(R.string.default_notification_channel_id);

        Intent intent = StartUpActivity.newIntent(context);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new NotificationCompat.Builder(context, channelId)
                .setColor(ContextCompat.getColor(context, R.color.themeColor))
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setContentText(body)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build();

        NotificationManagerCompat.from(context).notify(0, notification);
    }

}
