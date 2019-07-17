package com.yashoid.talktome.network;

import android.content.Context;

import com.yashoid.network.NetworkOperation;

import org.json.JSONObject;

public class AddCommentOperation extends NetworkOperation implements ApiConstants {

    public interface OnAddCommentResultCallback {

        void onCommentAdded();

        void onFailedToAddComment(Exception exception);

    }

    private OnAddCommentResultCallback mCallback;

    private Context mContext;

    private String mPostId;
    private String mComment;

    private Exception mException = null;

    public AddCommentOperation(Context context, String postId, String comment, OnAddCommentResultCallback callback) {
        super(TYPE_USER_ACTION);

        mCallback = callback;

        mContext = context;

        mPostId = postId;
        mComment = comment;
    }

    @Override
    protected void operate() {
        try {
            AddCommentRequest request = new AddCommentRequest(mPostId, mComment, mContext);

            JSONObject response = request.execute();

            if (!RESULT_OK.equals(response.getString(RESULT))) {
                mException = new Exception("Failed to post comment for unknown reason.");
            }
        } catch (Exception e) {
            mException = e;
        }
    }

    @Override
    protected void onOperationFinished() {
        if (mException == null) {
            mCallback.onCommentAdded();
        }
        else {
            mCallback.onFailedToAddComment(mException);
        }
    }

}
