package com.yashoid.talktome.evaluation;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;

class EventTracker implements Events, Tags {

    private static final String PREFERENCES = "com.yashoid.talktome.evaluation.EventTracker";

    private static final String KEY_COMMENTED_POSTS = "commented_posts";

    private Application mApplication;

    private UnitTracker mVisitEventTracker;
    private UnitTracker mPostEventTracker;
    private UnitTracker mMyPostsEventTracker;
    private UnitTracker mCommentEventTracker;
    private UnitTracker mShareEventTracker;
    private UnitTracker mSelfShareEventTracker;
    private UnitTracker mOthersShareEventTracker;

    EventTracker(Application application) {
        mApplication = application;

        mVisitEventTracker = new UnitTracker(application, EVENT_VISITED, true, LAST_VISIT, null, VISITS_LAST_MONTH, VISITS_LAST_SEVEN_DAYS);
        mPostEventTracker = new UnitTracker(application, EVENT_POSTED, false, LAST_POST_TIME, SENT_POSTS, null, null);
        mMyPostsEventTracker = new UnitTracker(application, EVENT_VISITED_MY_POSTS, true, MY_POSTS_LAST_VISIT, null, null, MY_POSTS_VISIT_COUNT_LAST_SEVEN_DAYS);
        mCommentEventTracker = new UnitTracker(application, EVENT_COMMENTED, false, LAST_COMMENT_TIME, null, SENT_COMMENT_LAST_MONTH, SENT_COMMENTS_LAST_SEVEN_DAYS);
        mShareEventTracker = new UnitTracker(application, EVENT_SHARED, false, null, null, SHARED_POSTS_LAST_MONTH, null);
        mSelfShareEventTracker = new UnitTracker(application, EVENT_SHARED, false, null, null, SHARED_SELF_POSTS_LAST_MONTH, null);
        mOthersShareEventTracker = new UnitTracker(application, EVENT_SHARED, false, null, null, SHARED_OTHERS_POSTS_LAST_MONTH, null);
    }

    void trackEvent(String event, Object... payload) {
        switch (event) {
            case EVENT_POSTED:
                Eval.setTag(HAS_UNSENT_POST, "0");

                mPostEventTracker.onEvent();
                return;

            case EVENT_DIDNT_POST:
                Eval.setTag(HAS_UNSENT_POST, "1");
                Eval.sendEvent(EVENT_DIDNT_POST);
                return;

            case EVENT_COMMENTED:
                String postId = (String) payload[0];
                onCommentedOnPost(postId);

                mCommentEventTracker.onEvent();
                return;

            case EVENT_SHARED:
                mShareEventTracker.onEvent();

                boolean sharedSelfPost = (boolean) payload[0];

                if (sharedSelfPost) {
                    mSelfShareEventTracker.onEvent(false);
                }
                else {
                    mOthersShareEventTracker.onEvent(false);
                }
                return;

            case EVENT_REFRESHED_POSTS:
                Eval.sendEvent(EVENT_REFRESHED_POSTS);
                return;

            case EVENT_VISITED:
                mVisitEventTracker.onEvent();
                return;

            case EVENT_VISITED_MY_POSTS:
                mMyPostsEventTracker.onEvent();
                return;

            case EVENT_CRASHED:
                Eval.sendABEvent(EVENT_CRASHED);
                return;
        }
    }

    private void onCommentedOnPost(String postId) {
        SharedPreferences prefs = mApplication.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);

        String sPosts = prefs.getString(KEY_COMMENTED_POSTS, "[]");

        try {
            JSONArray jPosts = new JSONArray(sPosts);

            for (int i = 0; i < jPosts.length(); i++) {
                if (TextUtils.equals(postId, jPosts.getString(i))) {
                    return;
                }
            }

            jPosts.put(postId);

            prefs.edit().putString(KEY_COMMENTED_POSTS, jPosts.toString()).apply();

            Eval.setTag(POSTS_THAT_COMMENTED, String.valueOf(jPosts.length()));
        } catch (JSONException e) { }
    }

}
