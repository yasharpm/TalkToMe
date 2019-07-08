package com.yashoid.talktome.network;

import android.content.Context;

import com.yashoid.mmv.ModelFeatures;
import com.yashoid.network.NetworkOperation;
import com.yashoid.talktome.post.Post;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class GetNotesOperation extends NetworkOperation implements ApiConstants, Post {

    private static final String NOTE_ID = "note_id";
    private static final String NOTE_NOTE = "note_note";
    private static final String NOTE_TIME = "note_time";
    private static final String NOTE_DISPLAYS = "note_displays";

    private static String NOTE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public interface GetNotesCallback {

        void onGetNotesResult(List<ModelFeatures> notes, Exception exception);

    }

    private GetNotesCallback mCallback;

    private Context mContext;

    private final int mCount;

    private List<JSONObject> mPosts = null;

    private Exception mException = null;

    public GetNotesOperation(Context context, int count, GetNotesCallback callback) {
        super(TYPE_UI_CONTENT);

        mCallback = callback;

        mContext = context;

        mCount = count;
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

        List<JSONObject> posts = new ArrayList<>();

        Exception getNoteException = null;

        try {
            for (int i = 0; i < mCount; i++) {
                GetNoteRequest getNoteRequest = new GetNoteRequest(mContext);

                JSONObject post = getNoteRequest.execute();

                try {
                    if (RESULT_OK.equals(post.getString(RESULT))) {
                        posts.add(post);
                    }
                } catch (JSONException e) { }
            }
        } catch (IOException e) {
            getNoteException = e;
        }

        if (getNoteException != null && posts.isEmpty()) {
            mException = getNoteException;
            return;
        }

        mPosts = new ArrayList<>(posts);
    }

    @Override
    protected void onOperationFinished() {
        ArrayList<ModelFeatures> posts = new ArrayList<>(mPosts.size());

        for (JSONObject postJson: mPosts) {
            posts.add(postFeaturesFromJson(postJson));
        }
        mCallback.onGetNotesResult(posts, mException);
    }

    private ModelFeatures postFeaturesFromJson(JSONObject json) {
        ModelFeatures.Builder builder = new ModelFeatures.Builder();

        try {
            builder.add(Post.ID, json.getString(NOTE_ID));
            builder.add(Post.CONTENT, json.getString(NOTE_NOTE));

            String timeString = json.getString(NOTE_TIME);
            SimpleDateFormat dateFormat = new SimpleDateFormat(NOTE_TIME_FORMAT);
            builder.add(Post.CREATED_TIME, dateFormat.parse(timeString).getTime());

            builder.add(Post.VIEWS, json.getInt(NOTE_DISPLAYS));
        } catch (JSONException e) {

        } catch (ParseException e) {

        }

        return builder.build();
    }

}
