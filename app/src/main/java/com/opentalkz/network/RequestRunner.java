package com.opentalkz.network;

import android.content.Context;
import android.content.SharedPreferences;

import com.yashoid.network.PreparedRequest;
import com.yashoid.network.Priorities;
import com.yashoid.network.RequestResponse;
import com.yashoid.network.RequestResponseCallback;
import com.opentalkz.TTMOffice;

import java.util.ArrayList;
import java.util.List;

public class RequestRunner {

    private static final int TOKEN_RETRIES = 3;

    private static final int STATUS_UNAUTHORIZED = 401;

    private static final String PREFERENCES = "RequestRunner";

    private static final String USER_ID = "user_id";
    private static final String REFRESH_TOKEN = "refresh_token";
    private static final String TOKEN = "token";
    private static final String TOKEN_EXPIRATION = "token_expiration";

    private static RequestRunner mInstance = null;

    public static RequestRunner get(Context context) {
        if (mInstance == null) {
            mInstance = new RequestRunner(context);
        }

        return mInstance;
    }

    private Context mContext;

    private String mUserId;
    private String mRefreshToken;
    private String mToken;
    private long mTokenExpiration;

    private boolean mIsGettingToken = false;

    private int mRemainingTries = TOKEN_RETRIES;

    private List<PendingRequest> mPendingRequests = new ArrayList<>();

    private RequestRunner(Context context) {
        mContext = context.getApplicationContext();

        SharedPreferences preferences = mContext.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);

        mUserId = preferences.getString(USER_ID, null);
        mRefreshToken = preferences.getString(REFRESH_TOKEN, null);
        mToken = preferences.getString(TOKEN, null);
        mTokenExpiration = preferences.getLong(TOKEN_EXPIRATION, 0);
    }

    public<T> void runUserAction(PreparedRequest<T> request, RequestResponseCallback<T> callback) {
        runRequest(request, Priorities.HIGH, callback);
    }

    public<T> void runForUI(PreparedRequest<T> request, RequestResponseCallback<T> callback) {
        runRequest(request, Priorities.MEDIUM, callback);
    }

    public<T> void runInBackground(PreparedRequest<T> request, RequestResponseCallback<T> callback) {
        runRequest(request, Priorities.LOW, callback);
    }

    public<T> void runRequest(PreparedRequest<T> request, String priority, RequestResponseCallback<T> callback) {
        if (mIsGettingToken) {
            mPendingRequests.add(new PendingRequest<>(request, priority, callback));
        }
        else if (mToken == null || System.currentTimeMillis() > mTokenExpiration) {
            getToken();
            mPendingRequests.add(new PendingRequest<>(request, priority, callback));
        }
        else {
            runRequestInternal(request, priority, callback);
        }
    }

    private<T> void runRequestInternal(final PreparedRequest<T> request, final String priority,
                                       final RequestResponseCallback<T> callback) {
        request.setHeader(TOKEN, mToken);

        TTMOffice.network().runRequest(request, priority, new RequestResponseCallback<T>() {

            @Override
            public void onRequestResponse(RequestResponse<T> response) {
                if (response.getResponseCode() == STATUS_UNAUTHORIZED) {
                    mPendingRequests.add(new PendingRequest<>(request, priority, callback));

                    getToken();
;                }
                else {
                    callback.onRequestResponse(response);
                }
            }

        });
    }

    private void getToken() {
        if (mIsGettingToken) {
            return;
        }

        mIsGettingToken = true;

        if (mToken == null) {
            TTMOffice.network().runRequest(Requests.register(), Priorities.HIGH, mRegisterCallback);
        }
        else {
            TTMOffice.network().runRequest(Requests.getToken(mUserId, mRefreshToken), Priorities.HIGH, mTokenCallback);
        }
    }

    private RequestResponseCallback<RegisterResponse> mRegisterCallback = new RequestResponseCallback<RegisterResponse>() {

        @Override
        public void onRequestResponse(RequestResponse<RegisterResponse> response) {
            mIsGettingToken = false;

            if (response.isSuccessful()) {
                RegisterResponse content = response.getContent();

                mUserId = content.getUserId();
                mRefreshToken = content.getRefreshToken();
                mToken = content.getToken();
                mTokenExpiration = System.currentTimeMillis() + (content.getTokenLife() * 1000);

                saveTokenInfo();

                runPendingRequests();
            }
            else if (mRemainingTries > 0) {
                mRemainingTries--;

                getToken();
            }
            else {
                notifyRequestsFailed(response);
            }
        }

    };

    private RequestResponseCallback<GetTokenResponse> mTokenCallback = new RequestResponseCallback<GetTokenResponse>() {

        @Override
        public void onRequestResponse(RequestResponse<GetTokenResponse> response) {
            mIsGettingToken = false;

            if (response.isSuccessful()) {
                GetTokenResponse content = response.getContent();

                mToken = content.getToken();
                mTokenExpiration = System.currentTimeMillis() + (content.getTokenLife() * 1000);

                saveTokenInfo();

                runPendingRequests();
            }
            else if (mRemainingTries > 0) {
                mRemainingTries--;

                getToken();
            }
            else {
                notifyRequestsFailed(response);
            }
        }

    };

    private void saveTokenInfo() {
        SharedPreferences preferences = mContext.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);

        preferences.edit()
                .putString(USER_ID, mUserId)
                .putString(REFRESH_TOKEN, mRefreshToken)
                .putString(TOKEN, mToken)
                .putLong(TOKEN_EXPIRATION, mTokenExpiration)
                .apply();
    }

    private void runPendingRequests() {
        List<PendingRequest> pendingRequests = new ArrayList<>(mPendingRequests);

        mPendingRequests.clear();

        for (PendingRequest pendingRequest: pendingRequests) {
            runRequestInternal(pendingRequest.request, pendingRequest.priority, pendingRequest.callback);
        }
    }

    private void notifyRequestsFailed(RequestResponse response) {
        List<PendingRequest> pendingRequests = new ArrayList<>(mPendingRequests);

        mPendingRequests.clear();

        for (PendingRequest pendingRequest: pendingRequests) {
            pendingRequest.callback.onRequestResponse(response);
        }
    }

    private static class PendingRequest<T> {

        final private PreparedRequest<T> request;
        final private String priority;
        final private RequestResponseCallback<T> callback;

        private PendingRequest(PreparedRequest<T> request, String priority, RequestResponseCallback<T> callback) {
            this.request = request;
            this.priority = priority;
            this.callback = callback;
        }

    }

}
