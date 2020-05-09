package com.opentalkz.notification;

import android.content.Context;
import android.util.Log;

import com.yashoid.network.PreparedRequest;
import com.yashoid.network.RequestResponse;
import com.yashoid.network.RequestResponseCallback;
import com.opentalkz.Preferences;
import com.opentalkz.TTMOffice;
import com.opentalkz.network.Requests;
import com.opentalkz.network.SyncResponse;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class ChangeTracker {

    private static final String TAG = "AppUpdateTracker";

    private static final String UPDATE_TOKEN = "update_token";
    private static final String UPDATE_COUNT = "update_count";

    public interface ChangeCountObserver {

        void onChangeCountUpdated(int changeCount);

    }

    private static ChangeTracker mInstance = null;

    public static ChangeTracker get(Context context) {
        if (mInstance == null) {
            mInstance = new ChangeTracker(context);
        }

        return mInstance;
    }

    private Context mContext;

    private int mChangeCount = -1;

    private List<WeakReference<ChangeCountObserver>> mObservers = new ArrayList<>();

    private ChangeTracker(Context context) {
        mContext = context.getApplicationContext();

        updateChangeCount(0);
    }

    public void refresh() {
        final String updateToken = TTMOffice.preferences(mContext).readString(UPDATE_TOKEN);

        PreparedRequest<SyncResponse> request = Requests.sync(updateToken);

        TTMOffice.runner(mContext).runInBackground(request, new RequestResponseCallback<SyncResponse>() {

            @Override
            public void onRequestResponse(RequestResponse<SyncResponse> response) {
                if (!response.isSuccessful()) {
                    Log.e(TAG, "Failed to perform sync.", response.getException());
                    return;
                }

                SyncResponse syncResponse = response.getContent();

                String newUpdateToken = syncResponse.getUpdateToken();

                TTMOffice.preferences(mContext).write(UPDATE_TOKEN, newUpdateToken);

                int changeCount = countChanges(syncResponse.getChanges());

                Log.i(TAG, "Sync successfully called resulting in " + changeCount + " changes.");

                updateChangeCount(changeCount);

                if (!syncResponse.isSyncCompleted()) {
                    refresh();
                }
            }

        });
    }

    public void reset() {
        mChangeCount = 0;

        TTMOffice.preferences(mContext).write(UPDATE_COUNT, mChangeCount);

        notifyChangeCount();
    }

    public void registerChangeCountObserver(ChangeCountObserver observer) {
        mObservers.add(new WeakReference<>(observer));

        notifyChangeCount(observer);
    }

    private void updateChangeCount(int newChangeCount) {
        Preferences preferences = TTMOffice.preferences(mContext);

        if (mChangeCount == -1) {
            mChangeCount = preferences.readInt(UPDATE_COUNT, 0);
        }

         mChangeCount += newChangeCount;

        preferences.write(UPDATE_COUNT, mChangeCount);

        notifyChangeCount();
    }

    private void notifyChangeCount() {
        List<WeakReference<ChangeCountObserver>> observers = new ArrayList<>(mObservers);

        for (WeakReference<ChangeCountObserver> observerReferences: observers) {
            ChangeCountObserver observer = observerReferences.get();

            if (observer == null) {
                mObservers.remove(observerReferences);
            }
            else {
                notifyChangeCount(observer);
            }
        }
    }

    private void notifyChangeCount(ChangeCountObserver observer) {
        observer.onChangeCountUpdated(mChangeCount);
    }

    private int countChanges(List<SyncResponse.Change> changes) {
        if (changes == null || changes.isEmpty()) {
            return 0;
        }

        int count = 0;

        for (SyncResponse.Change change: changes) {
            if (SyncResponse.Change.EVENT_NEW_COMMENT.equals(change.getEventType()) || SyncResponse.Change.EVENT_NEW_VIEWS.equals(change.getEventType())) {
                count++;
            }
        }

        return count;
    }

}
