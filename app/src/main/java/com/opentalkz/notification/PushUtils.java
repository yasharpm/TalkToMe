package com.opentalkz.notification;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.yashoid.network.RequestResponse;
import com.yashoid.network.RequestResponseCallback;
import com.opentalkz.Preferences;
import com.opentalkz.TTMOffice;
import com.opentalkz.network.BaseResponse;
import com.opentalkz.network.Requests;

public class PushUtils {

    private static final String TAG = "Notification";

    private static final String FCM_TOKEN = "fcm_token";

    public static void verifyFCMToken(final Context context) {
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {

            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                final String token = instanceIdResult.getToken();

                String cachedToken = Preferences.get(context).readString(FCM_TOKEN);

                if (!TextUtils.equals(token, cachedToken)) {
                    Log.d(TAG, "About to send fcm token to server.");

                    TTMOffice.runner(context).runInBackground(Requests.updateFCMToken(token), new RequestResponseCallback<BaseResponse>() {

                        @Override
                        public void onRequestResponse(RequestResponse<BaseResponse> response) {
                            if (response.isSuccessful()) {
                                Log.d(TAG, "FCM token successfully handed to server.");

                                Preferences.get(context).write(FCM_TOKEN, token);
                            }
                            else {
                                Log.e(TAG, "Failed to send fcm token to server.", response.getException());
                            }
                        }

                    });
                }
            }

        });
    }

}
