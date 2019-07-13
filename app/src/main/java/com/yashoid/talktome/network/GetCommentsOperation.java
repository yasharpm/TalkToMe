package com.yashoid.talktome.network;

import android.content.Context;

import com.yashoid.mmv.ModelFeatures;
import com.yashoid.network.NetworkOperation;
import com.yashoid.talktome.model.comment.Comment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GetCommentsOperation extends NetworkOperation implements ApiConstants, Comment {

    private static final String COMMENTS = "comments";
    private static final String COMMENT_ID = "comment_id";
    private static final String COMMENT_COMMENT = "comment_comment";

    public interface GetCommentsCallback {

        void onGetCommentsResult(List<ModelFeatures> comments, Exception exception);

    }

    private GetCommentsCallback mCallback;

    private Context mContext;

    private final String mPostId;

    private List<JSONObject> mComments = null;

    private Exception mException = null;

    public GetCommentsOperation(Context context, String postId, GetCommentsCallback callback) {
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

        List<JSONObject> comments = new ArrayList<>();

        Exception getCommentException = null;

        try {
            GetCommentsRequest request = new GetCommentsRequest(mContext, mPostId);

            JSONObject result = request.execute();

            try {
                if (RESULT_OK.equals(result.getString(RESULT))) {
                    JSONArray commentsJson = result.getJSONArray(COMMENTS);

                    for (int i = 0; i < commentsJson.length(); i++) {
                        comments.add(commentsJson.getJSONObject(i));
                    }
                }
            } catch (JSONException e) {
                getCommentException = e;
            }
        } catch (IOException e) {
            getCommentException = e;
        }

        if (getCommentException != null && comments.isEmpty()) {
            mException = getCommentException;
            return;
        }

        mComments = new ArrayList<>(comments);
    }

    @Override
    protected void onOperationFinished() {
        if (mComments != null) {
            ArrayList<ModelFeatures> comments = new ArrayList<>(mComments.size());

            for (JSONObject commentJson: mComments) {
                comments.add(commentFeaturesFromJson(commentJson));
            }

            mCallback.onGetCommentsResult(comments, mException);
        }
        else {
            mCallback.onGetCommentsResult(null, mException);
        }
    }

    private ModelFeatures commentFeaturesFromJson(JSONObject json) {
        ModelFeatures.Builder builder = new ModelFeatures.Builder();

        try {
            builder.add(ID, json.getString(COMMENT_ID));
            builder.add(CONTENT, json.getString(COMMENT_COMMENT));
        } catch (JSONException e) {

        }

        return builder.build();
    }

}
