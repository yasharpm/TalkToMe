package com.yashoid.talktome.network;

import android.content.Context;

import com.yashoid.network.NetworkOperation;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class ReportOperation extends NetworkOperation implements ApiConstants {

    public interface ReportCallback {

        void onReportResult(boolean successful, Exception exception);

    }

    private ReportCallback mCallback;

    private Context mContext;

    private final String mPostId;

    private boolean mSuccessful = false;
    private Exception mException = null;

    public ReportOperation(Context context, String postId, ReportCallback callback) {
        super(TYPE_UI_CONTENT);

        mCallback = callback;

        mContext = context;

        mPostId = postId;
    }

    @Override
    protected void operate() {
        Api api = Api.get(mContext);

        if (api.getAduId() == null) {
            try {
                LoginRequest loginRequest = new LoginRequest(mContext);

                JSONObject loginResult = loginRequest.execute();

                try {
                    String aduId = loginResult.getString(ADU_ID);

                    api.setAduId(aduId);
                } catch (JSONException e) {
                    mException = new Exception("Failed to read ADU ID from login request.", e);
                    return;
                }
            } catch (IOException e) {
                mException = e;
                return;
            }
        }

        Exception reportException = null;

        try {
            ReportRequest request = new ReportRequest(mContext, mPostId);

            JSONObject result = request.execute();

            try {
                mSuccessful = RESULT_OK.equals(result.getString(RESULT));
            } catch (JSONException e) {
                reportException = e;
            }
        } catch (IOException e) {
            reportException = e;
        }

        if (reportException != null) {
            mException = reportException;
            return;
        }
    }

    @Override
    protected void onOperationFinished() {
        mCallback.onReportResult(mSuccessful, mException);
    }

}
