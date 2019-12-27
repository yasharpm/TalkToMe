package com.yashoid.talktome.model.post;

import android.content.Context;

import com.yashoid.mmv.Action;
import com.yashoid.mmv.Model;
import com.yashoid.mmv.ModelFeatures;
import com.yashoid.network.RequestResponse;
import com.yashoid.network.RequestResponseCallback;
import com.yashoid.talktome.TTMOffice;
import com.yashoid.talktome.model.list.ModelList;
import com.yashoid.talktome.network.PostResponse;
import com.yashoid.talktome.network.RandomPostsResponse;
import com.yashoid.talktome.network.Requests;

import java.util.ArrayList;
import java.util.List;

public interface RandomPostList extends ModelList {

    String TYPE_RANDOM_POST_LIST = "RandomPostList";

    ModelFeatures FEATURES = new ModelFeatures.Builder().add(TYPE, TYPE_RANDOM_POST_LIST).build();

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
        protected void getModels(final Model model, Object... params) {
            model.set(STATE, STATE_LOADING);

            final int count = (int) params[0];

            TTMOffice.runner(mContext).runForUI(Requests.randomPosts(count, "fa", "IR"), new RequestResponseCallback<RandomPostsResponse>() {

                @Override
                public void onRequestResponse(RequestResponse<RandomPostsResponse> response) {
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
                        model.set(STATE, STATE_FAILURE);
                    }
                }

            });
        }

    }

}
