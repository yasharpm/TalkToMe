package com.opentalkz.notification;

import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.opentalkz.Share;
import com.yashoid.office.task.TaskManager;
import com.opentalkz.R;
import com.opentalkz.TTMOffice;
import com.opentalkz.network.SyncResponse;

import java.io.IOException;
import java.util.Map;

public class PushService extends FirebaseMessagingService {

    private static final String TAG = "PushService";

    private static final int MAX_BODY_LENGTH = 50;

    public static final String SYNC = "sync";
    public static final String POST_ID = "postId";

    @Override
    @WorkerThread
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Log.d(TAG, "Push message received.");

        Map<String, String> data = remoteMessage.getData();
        String sync = data.get(SYNC);

        if (sync != null) {
            try {
                SyncResponse.Change change = TTMOffice.network().getYashson().parse(sync, SyncResponse.Change.class);

                notifyChange(change, remoteMessage.getNotification());
            } catch (IOException e) {
                Log.e(TAG, "Failed to read sync object from json.");
            }
        }
        else if (data.containsKey(POST_ID)) {
            String postId = data.get(POST_ID);

            notifyWithPostId(null, remoteMessage.getNotification(), postId);
        }
    }

    @WorkerThread
    private void notifyChange(SyncResponse.Change change, RemoteMessage.Notification notification) {
        switch (change.getEventType()) {
            case SyncResponse.Change.EVENT_NEW_COMMENT:
                notifyWithPostId(change, notification, change.getContent().postId);
                return;
        }
    }

    private void notifyWithPostId(SyncResponse.Change change, RemoteMessage.Notification notification, String postId) {
        String title = null;
        String body = null;

        if (notification != null) {
            title = notification.getTitle();
            body = notification.getBody();
        }

        if (TextUtils.isEmpty(title)) {
            title = getString(R.string.notification_newcomment_title);
        }

        if (TextUtils.isEmpty(body) && change != null) {
            body = change.getContent().content;

//            if (body != null && body.length() > MAX_BODY_LENGTH) {
//                body = body.substring(0, MAX_BODY_LENGTH) + "…";
//            }
        }

        Uri uri = Share.getShareUri(postId);

        Notifier.notify(this, title, body, uri);

        TTMOffice.get().runTask(TaskManager.MAIN, new Runnable() {

            @Override
            public void run() {
                ChangeTracker.get(PushService.this).refresh();
            }

        }, 0);
    }

}
