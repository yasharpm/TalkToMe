package com.yashoid.talktome.model.post;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.yashoid.mmv.ModelFeatures;
import com.yashoid.network.RequestResponse;
import com.yashoid.network.RequestResponseCallback;
import com.yashoid.talktome.TTMOffice;
import com.yashoid.talktome.network.Requests;
import com.yashoid.talktome.network.SeenPostsResponse;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class SeenPostsTracker implements Post {

    private static final String TAG = "SeenPostsTracker";

    private static final int MINIMUM_COUNT_TO_SEND = 6;

    private static final String PREFERENCES = "com.yashoid.talktome.model.post.SeenPostsTracker";
    private static final String KEY_POST_IDS = "post_ids";

    private static SeenPostsTracker mInstance = null;

    public static SeenPostsTracker get(Context context) {
        if (mInstance == null) {
            mInstance = new SeenPostsTracker(context);
        }

        return mInstance;
    }

    private Context mContext;
    private SharedPreferences mPreferences;

    private List<String> mSeenPostIds;

    private boolean mIsSendingViews = false;

    private SeenPostsTracker(Context context) {
        mContext = context.getApplicationContext();

        mPreferences = mContext.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);

        try {
            JSONArray jPostIds = new JSONArray(mPreferences.getString(KEY_POST_IDS, "[]"));

            mSeenPostIds = new ArrayList<>(jPostIds.length());

            for (int i = 0; i < jPostIds.length(); i++) {
                mSeenPostIds.add(jPostIds.getString(i));
            }
        } catch (JSONException e) { }
    }

    public void onSeenPosts(List<ModelFeatures> posts) {
        for (ModelFeatures post: posts) {
            mSeenPostIds.add((String) post.get(ID));
        }

        updateSeenPosts();

        checkToSend();
    }

    private void updateSeenPosts() {
        JSONArray jPostIds = new JSONArray();

        for (String postId: mSeenPostIds) {
            jPostIds.put(postId);
        }

        mPreferences.edit().putString(KEY_POST_IDS, jPostIds.toString()).apply();
    }

    public void checkToSend() {
        if (mIsSendingViews) {
            return;
        }

        if (mSeenPostIds.size() >= MINIMUM_COUNT_TO_SEND) {
            mIsSendingViews = true;

            TTMOffice.runner(mContext).runInBackground(Requests.seenPosts(mSeenPostIds), new RequestResponseCallback<SeenPostsResponse>() {

                @Override
                public void onRequestResponse(RequestResponse<SeenPostsResponse> response) {
                    mIsSendingViews = false;

                    if (response.isSuccessful()) {
                        List<String> seenPostIds = response.getContent().getPostIds();

                        Log.i(TAG, "Seen posts sent for posts: " + seenPostIds);

                        mSeenPostIds.removeAll(seenPostIds);

                        updateSeenPosts();

                        checkToSend();
                    }
                    else {
                        Log.e(TAG, "Failed to send seen posts.", response.getException());
                    }
                }

            });
        }
    }

}
