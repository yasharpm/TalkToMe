package com.yashoid.talktome.network;

import android.content.Context;

import com.yashoid.network.NetworkOperation;

import org.json.JSONObject;

public class AddPostOperation extends NetworkOperation implements ApiConstants {

    private static final String NOTE_ID = "note_id";

    public interface OnAddPostResultCallback {

        void onPostAdded(String postId);

        void onFailedToAddPost(Exception exception);

    }

    private OnAddPostResultCallback mCallback;

    private Context mContext;

    private String mContent;

    private String mPostId;

    private Exception mException = null;

    public AddPostOperation(Context context, String content, OnAddPostResultCallback callback) {
        super(TYPE_USER_ACTION);

        mCallback = callback;

        mContext = context;

        mContent = content;
    }

    @Override
    protected void operate() {
        try {
            AddPostRequest request = new AddPostRequest(mContext, mContent);

            JSONObject response = request.execute();

            if (RESULT_OK.equals(response.getString(RESULT))) {
                mPostId = response.getString(NOTE_ID);
            }
            else {
                mException = new Exception("Failed to send post for unknown reason.");
            }
        } catch (Exception e) {
            mException = e;
        }
    }

    @Override
    protected void onOperationFinished() {
        if (mException == null) {
            mCallback.onPostAdded(mPostId);
        }
        else {
            mCallback.onFailedToAddPost(mException);
        }
    }

}
