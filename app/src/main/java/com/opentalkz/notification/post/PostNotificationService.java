package com.opentalkz.notification.post;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.opentalkz.Preferences;
import com.opentalkz.R;
import com.opentalkz.Share;
import com.opentalkz.model.post.Post;
import com.opentalkz.model.post.SeenPostsTracker;
import com.opentalkz.notification.Notifier;
import com.opentalkz.ui.StartUpActivity;
import com.yashoid.mmv.Model;

import ir.abmyapp.androidsdk.ABResources;

public class PostNotificationService extends Service {

    private static final String TAG = "PostNotificationService";

    private static final int MAXIMUM_CONTENT_LENGTH = 150;

    private static final Uri GO_NEXT = Uri.parse("opentalkz://goNext/");
    private static final Uri TURN_OFF = Uri.parse("opentalkz://turnOff/");

    public static Intent getIntent(Context context) {
        Intent intent = new Intent(context, PostNotificationService.class);

        return intent;
    }

    public static Intent getNextIntent(Context context) {
        Intent intent = getIntent(context);

        intent.setData(GO_NEXT);

        return intent;
    }

    public static Intent getTurnOffIntent(Context context) {
        Intent intent = getIntent(context);

        intent.setData(TURN_OFF);

        return intent;
    }

    public static long getRefreshPeriod(Context context) {
        return RefreshHandler.getRefreshPeriod(context, TAG);
    }

    public static void setRefreshPeriod(Context context, long period) {
        RefreshHandler.setRefreshPeriod(context, TAG, period);
    }

    private RefreshHandler mRefreshHandler;

    @Override
    public void onCreate() {
        super.onCreate();

        mRefreshHandler = new RefreshHandler(this, TAG, PostNotificationService.class);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, final int startId) {
        Uri data = intent == null ? null : intent.getData();

        boolean turnOff = data != null && data.equals(TURN_OFF);

        if (turnOff) {
            mRefreshHandler.reset();
            NotificationManagerCompat.from(this).cancel(Notifier.NOTIFICATION_ID_POST);

            stopSelf(startId);

            return START_NOT_STICKY;
        }

        boolean goNext = data != null && data.equals(GO_NEXT);

        if (goNext || mRefreshHandler.shouldRefresh()) {
            RandomPostBox.getInstance(this).next(new RandomPostBox.NextPostCallback() {

                @Override
                public void onNextPostResult(Model post, Object... args) {
                    if (post != null) {
                        mRefreshHandler.onRefreshed();

                        notifyPost(post);

                        SeenPostsTracker.get(PostNotificationService.this).onSeenPost(post);
                    }
                }

            });

            long nextRefreshTime = mRefreshHandler.getNextRefreshTime();

            if (nextRefreshTime != 0) {
                Intent refreshIntent = RefreshReceiver.getIntent(this, getIntent(PostNotificationService.this));

                PendingIntent refreshPendingIntent = PendingIntent.getBroadcast(
                        PostNotificationService.this,
                        0,
                        refreshIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

                AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);

                if (am != null) {
                    am.set(AlarmManager.RTC, nextRefreshTime, refreshPendingIntent);
                }
            }
        }

        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void notifyPost(Model post) {
        final String channelId = Notifier.CHANNEL_POST;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    getString(R.string.post_notification_channel),
                    NotificationManager.IMPORTANCE_LOW
            );

            NotificationManagerCompat.from(this).createNotificationChannel(channel);
        }

        Intent nextIntent = PostNotificationService.getNextIntent(this);
        PendingIntent pendingNextIntent = PendingIntent.getService(this, 0, nextIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent turnOffIntent = getTurnOffIntent(this);
        PendingIntent pendingOffIntent = PendingIntent.getService(this, 0, turnOffIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        String content = post.get(Post.CONTENT);
        String postId = post.get(Post.ID);

        if (content.length() > MAXIMUM_CONTENT_LENGTH) {
            content = content.substring(0, MAXIMUM_CONTENT_LENGTH) + "...";
        }

        Uri postUri = Share.getShareUri(postId);
        Intent openIntent = StartUpActivity.newIntent(this);
        openIntent.setData(postUri);
        PendingIntent openPendingIntent = PendingIntent.getActivity(this, 0, openIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new NotificationCompat.Builder(this, channelId)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setColorized(true)
                .setColor(ABResources.get(this).getColor(R.color.themeColor))
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setCategory(NotificationCompat.CATEGORY_SOCIAL)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentIntent(openPendingIntent)
                .setContentText(content)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(content))
                .addAction(new NotificationCompat.Action.Builder(R.drawable.ic_submit, ABResources.get(this).getString(R.string.notification_next), pendingNextIntent)
                        .setSemanticAction(NotificationCompat.Action.SEMANTIC_ACTION_MARK_AS_READ)
                        .setContextual(false)
                        .setShowsUserInterface(false)
                        .build())
                .addAction(new NotificationCompat.Action.Builder(R.drawable.ic_submit, ABResources.get(this).getString(R.string.notification_turnoff), pendingOffIntent)
                        .setSemanticAction(NotificationCompat.Action.SEMANTIC_ACTION_DELETE)
                        .setContextual(false)
                        .setShowsUserInterface(false)
                        .build())
                .setAutoCancel(false)
                .setOnlyAlertOnce(true)
                .setNotificationSilent()
                .setOngoing(true)
                .build();

        NotificationManagerCompat.from(this).notify(Notifier.NOTIFICATION_ID_POST, notification);
    }

}
