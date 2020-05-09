package com.opentalkz.model.post;

import android.content.Context;
import android.text.TextUtils;

import com.yashoid.mmv.Action;
import com.yashoid.mmv.Model;
import com.yashoid.mmv.ModelFeatures;
import com.yashoid.network.RequestResponse;
import com.yashoid.network.RequestResponseCallback;
import com.opentalkz.TTMCompat;
import com.opentalkz.TTMOffice;
import com.opentalkz.model.list.ModelList;
import com.opentalkz.network.PostListResponse;
import com.opentalkz.network.PostResponse;
import com.opentalkz.network.Requests;

import java.util.ArrayList;
import java.util.List;

public interface MyPostList extends ModelList {

    String TYPE_MY_POST_LIST = "MyPostList";

    ModelFeatures FEATURES = new ModelFeatures.Builder().add(TYPE, TYPE_MY_POST_LIST).build();

    class MyPostListTypeProvider extends ModelListTypeProvider {

        private Context mContext;

        public MyPostListTypeProvider(Context context) {
            super(TYPE_MY_POST_LIST);

            mContext = context;
        }

        @Override
        protected Action getAction(ModelFeatures features, String actionName) {
            switch (actionName) {
                case Action.ACTION_MODEL_NOT_EXISTED_IN_CACHE:
                    return mFirstTimeAction;
                case Action.ACTION_MODEL_LOADED_FROM_CACHE:
                    return mResetAction;
            }

            return null;
        }

        @Override
        protected void getModels(final Model model, Object... params) {
            model.set(STATE, STATE_LOADING);

            List<ModelFeatures> posts = model.get(MODEL_LIST);

            List<String> postIds = new ArrayList<>();

            if (posts != null) {
                for (ModelFeatures post: posts) {
                    postIds.add((String) post.get(Post.ID));
                }
            }

            TTMOffice.runner(mContext).runForUI(Requests.myPosts(20, 0), new RequestResponseCallback<PostListResponse>() {

                @Override
                public void onRequestResponse(RequestResponse<PostListResponse> response) {
                    if (response.isSuccessful()) {
//                        applyPostUpdates(model, updates);

                        List<PostResponse> postsResponse = response.getContent().getPosts();

                        List<ModelFeatures> posts = new ArrayList<>(postsResponse.size());

                        for (PostResponse postResponse: postsResponse) {
                            posts.add(postResponse.asModelFeatures());
                        }

                        model.set(MODEL_LIST, posts);

                        model.cache(true);
                        model.set(STATE, STATE_SUCCESS);
                    }
                    else {
                        model.set(STATE, STATE_FAILURE);
                    }
                }

            });
        }

        private Action mFirstTimeAction = new Action() {

            @Override
            public Object perform(Model model, Object... params) {
                List<ModelFeatures> legacyPosts = TTMCompat.getLegacyNotes(mContext);

                if (legacyPosts != null && !legacyPosts.isEmpty()) {
                    model.set(MODEL_LIST, legacyPosts);
                }

                return null;
            }

        };

        private Action mResetAction = new Action() {

            @Override
            public Object perform(Model model, Object... params) {
                model.set(STATE, STATE_IDLE);
                return null;
            }

        };

        private ModelFeatures findPost(List<ModelFeatures> list, String postId) {
            for (ModelFeatures post: list) {
                if (TextUtils.equals(postId, (String) post.get(Post.ID))) {
                    return post;
                }
            }

            return null;
        }

    }

}
