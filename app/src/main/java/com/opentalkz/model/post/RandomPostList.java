package com.opentalkz.model.post;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.opentalkz.model.community.CommunityList;
import com.yashoid.mmv.Action;
import com.yashoid.mmv.Managers;
import com.yashoid.mmv.Model;
import com.yashoid.mmv.ModelFeatures;
import com.yashoid.mmv.PersistentTarget;
import com.yashoid.network.RequestResponse;
import com.yashoid.network.RequestResponseCallback;
import com.opentalkz.TTMOffice;
import com.opentalkz.model.list.ModelList;
import com.opentalkz.network.PostResponse;
import com.opentalkz.network.RandomPostsResponse;
import com.opentalkz.network.Requests;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface RandomPostList extends ModelList {

    String TYPE_RANDOM_POST_LIST = "RandomPostList";

    ModelFeatures FEATURES = new ModelFeatures.Builder().add(TYPE, TYPE_RANDOM_POST_LIST).build();

    String COMMUNITY_ID = "communityId";

    String POSTS_SEEN = "postsSeen";

    String LAST_COMMUNITY_ID = "lastCommunityId";
    String COMMUNITY_POSTS = "communityPosts";

    class RandomPostListTypeProvider extends ModelListTypeProvider {

        private Context mContext;

        public RandomPostListTypeProvider(Context context) {
            super(TYPE_RANDOM_POST_LIST);

            mContext = context;
        }

        @Override
        protected Action getAction(ModelFeatures features, String actionName) {
            return null;
        }

        @Override
        protected void onModelInitialized(Model model) {
            super.onModelInitialized(model);

            model.set(LAST_COMMUNITY_ID, null);
            model.set(COMMUNITY_POSTS, new HashMap());

            Managers.registerTarget(new PersistentTarget() {

                private Model mModel;

                @Override
                public void setModel(Model model) {
                    mModel = model;

                    Managers.registerTarget(new PersistentTarget() {

                        private Model mCommunityList;

                        @Override
                        public void setModel(Model model) {
                            mCommunityList = model;

                            updateSelectedCommunity();
                        }

                        @Override
                        public void onFeaturesChanged(String... featureNames) {
                            for (String featureName: featureNames) {
                                if (CommunityList.SELECTED_COMMUNITY_ID.equalsIgnoreCase(featureName)) {
                                    updateSelectedCommunity();
                                    return;
                                }
                            }
                        }

                        private void updateSelectedCommunity() {
                            String selectedCommunityId = mCommunityList.get(CommunityList.SELECTED_COMMUNITY_ID);

                            mModel.set(COMMUNITY_ID, selectedCommunityId);
                        }

                    }, CommunityList.FEATURES);
                }

                @Override
                public void onFeaturesChanged(String... featureNames) {
                    for (String featureName: featureNames) {
                        if (COMMUNITY_ID.equalsIgnoreCase(featureName)) {
                            onCommunityChanged();
                            return;
                        }
                    }
                }

                private void onCommunityChanged() {
                    String communityId = mModel.get(LAST_COMMUNITY_ID);

                    int state = mModel.get(STATE);

                    HashMap snapShot = new HashMap();
                    snapShot.put(STATE, state);
                    snapShot.put(MODEL_LIST, mModel.get(MODEL_LIST));
                    snapShot.put(POSTS_SEEN, state == STATE_SUCCESS);

                    Map communityPosts = mModel.get(COMMUNITY_POSTS);

                    communityPosts.put(communityId, snapShot);

                    communityId = mModel.get(COMMUNITY_ID);

                    snapShot = (HashMap) communityPosts.get(communityId);

                    if (snapShot != null) {
                        mModel.set(STATE, snapShot.get(STATE));
                        mModel.set(MODEL_LIST, snapShot.get(MODEL_LIST));

                        boolean postsSeen = (Boolean) snapShot.get(POSTS_SEEN);

                        if (!postsSeen) {
                            List<ModelFeatures> posts = (List<ModelFeatures>) snapShot.get(MODEL_LIST);

                            if (posts != null) {
                                SeenPostsTracker.get(mContext).onSeenPosts(posts);
                            }

                            snapShot.put(POSTS_SEEN, true);
                        }
                    }
                    else {
                        mModel.set(STATE, STATE_IDLE);
                        mModel.set(MODEL_LIST, null);
                    }

                    mModel.set(LAST_COMMUNITY_ID, communityId);
                }

            }, FEATURES);
        }

        @Override
        protected void getModels(final Model model, Object... params) {
            final String communityId = model.get(COMMUNITY_ID);

            model.set(STATE, STATE_LOADING);

            final int count = (int) params[0];

            TTMOffice.runner(mContext).runForUI(
                    Requests.randomPosts(count, "fa", "IR", communityId),
                    new RequestResponseCallback<RandomPostsResponse>() {

                @Override
                public void onRequestResponse(RequestResponse<RandomPostsResponse> response) {
                    String currentCommunityId = model.get(COMMUNITY_ID);

                    if (TextUtils.equals(communityId, currentCommunityId)) {
                        if (response.isSuccessful()) {
                            List<PostResponse> postsResponse = response.getContent().getPosts();

                            List<ModelFeatures> posts = new ArrayList<>(postsResponse.size());

                            for (PostResponse postResponse: postsResponse) {
                                posts.add(postResponse.asModelFeatures());
                            }

                            SeenPostsTracker.get(mContext).onSeenPosts(posts);

                            model.set(MODEL_LIST, posts);
                            model.set(STATE, STATE_SUCCESS);
                        }
                        else {
                            Log.e(TYPE_RANDOM_POST_LIST, "Failed to get posts.", response.getException());

                            model.set(STATE, STATE_FAILURE);
                        }
                        return;
                    }

                    Map communityPosts = model.get(COMMUNITY_POSTS);

                    Map snapShot = (Map) communityPosts.get(communityId);

                    if (snapShot == null) {
                        snapShot = new HashMap();

                        communityPosts.put(communityId, snapShot);
                    }

                    if (response.isSuccessful()) {
                        List<PostResponse> postsResponse = response.getContent().getPosts();

                        List<ModelFeatures> posts = new ArrayList<>(postsResponse.size());

                        for (PostResponse postResponse: postsResponse) {
                            posts.add(postResponse.asModelFeatures());
                        }

                        snapShot.put(POSTS_SEEN, false);
                        snapShot.put(MODEL_LIST, posts);
                        snapShot.put(STATE, STATE_SUCCESS);
                    }
                    else {
                        Log.e(TYPE_RANDOM_POST_LIST, "Failed to get posts.", response.getException());

                        snapShot.put(STATE, STATE_FAILURE);
                    }
                }

            });
        }

    }

}
