package com.yashoid.talktome.network;

import android.content.Context;

import com.yashoid.mmv.ModelFeatures;
import com.yashoid.network.NetworkOperation;
import com.yashoid.talktome.model.post.Post;
import com.yashoid.talktome.network.postupdate.ChangedPostUpdate;
import com.yashoid.talktome.network.postupdate.PostUpdate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GetPostUpdatesOperation extends NetworkOperation implements ApiConstants {

    private static final String DISPLAYS = "displays";
    private static final String NOTE_ID = "note_id";
    private static final String NOTE_DISPLAYS = "note_displays";

    public interface GetPostUpdatesCallback {

        void onPostUpdatedResult(List<PostUpdate> updates, Exception exception);

    }

    private GetPostUpdatesCallback mCallback;

    private Context mContext;

    private List<String> mPostIds;

    private List<PostUpdate> mUpdates = null;

    private Exception mException = null;

    public GetPostUpdatesOperation(Context context, List<String> postIds, GetPostUpdatesCallback callback) {
        super(TYPE_UI_CONTENT);

        mCallback = callback;

        mContext = context;

        mPostIds = postIds;
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

        List<PostUpdate> updates = new ArrayList<>();

        Exception getDisplaysException = null;

        try {
            GetDisplaysRequest request = new GetDisplaysRequest(mContext, mPostIds.toArray(new String[mPostIds.size()]));

            JSONObject result = request.execute();

            try {
                if (RESULT_OK.equals(result.getString(RESULT))) {
                    JSONArray displaysJson = result.getJSONArray(DISPLAYS);

                    for (int i = 0; i < displaysJson.length(); i++) {
                        JSONObject display = displaysJson.getJSONObject(i);

                        updates.add(new DisplaysChange(display));
                    }
                }
            } catch (JSONException e) {
                getDisplaysException = e;
            }
        } catch (IOException e) {
            getDisplaysException = e;
        }

        if (getDisplaysException != null && updates.isEmpty()) {
            mException = getDisplaysException;
            return;
        }

        mUpdates = new ArrayList<>(updates);
    }

    @Override
    protected void onOperationFinished() {
        if (mUpdates != null) {
            mCallback.onPostUpdatedResult(mUpdates, mException);
        }
        else {
            mCallback.onPostUpdatedResult(null, mException);
        }
    }

    private static class DisplaysChange extends ChangedPostUpdate implements Post {

        private String mPostId;
        private int mDisplays;

        private DisplaysChange(JSONObject object) throws JSONException {
            mPostId = object.getString(NOTE_ID);
            mDisplays = object.getInt(NOTE_DISPLAYS);
        }

        @Override
        public ModelFeatures getChanges() {
            return new ModelFeatures.Builder()
                    .add(TYPE, TYPE_POST)
                    .add(ID, mPostId)
                    .add(VIEWS, mDisplays)
                    .build();
        }

    }

}
