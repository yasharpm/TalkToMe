package com.yashoid.talktome.notification;

import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.yashoid.talktome.R;
import com.yashoid.talktome.TTMOffice;
import com.yashoid.talktome.network.SyncResponse;

import java.io.IOException;
import java.util.Map;

public class PushService extends FirebaseMessagingService {

    private static final String TAG = "PushService";

    private static final int MAX_BODY_LENGTH = 50;

    private static final String SYNC = "sync";

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
    }

    @WorkerThread
    private void notifyChange(SyncResponse.Change change, RemoteMessage.Notification notification) {
        if (!TextUtils.equals(change.getEventType(), SyncResponse.Change.EVENT_NEW_COMMENT)) {
            return;
        }

        String title = null;
        String body = null;

        if (notification != null) {
            title = notification.getTitle();
            body = notification.getBody();
        }

        if (TextUtils.isEmpty(title)) {
            title = getString(R.string.notification_newcomment_title);
        }

        if (TextUtils.isEmpty(body)) {
            body = change.getContent().content;

            if (body != null && body.length() > MAX_BODY_LENGTH) {
                body = body.substring(0, MAX_BODY_LENGTH) + "…";
            }
        }

        Notifier.notify(this, title, body);
    }

}
