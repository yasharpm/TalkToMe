package com.opentalkz.model.post;

import android.content.Context;

import com.opentalkz.TTMOffice;
import com.opentalkz.model.list.ModelList;
import com.opentalkz.network.PostListResponse;
import com.opentalkz.network.PostResponse;
import com.opentalkz.network.Requests;
import com.yashoid.mmv.Action;
import com.yashoid.mmv.Managers;
import com.yashoid.mmv.Model;
import com.yashoid.mmv.ModelFeatures;
import com.yashoid.network.RequestResponse;
import com.yashoid.network.RequestResponseCallback;

import java.util.ArrayList;
import java.util.List;

public interface UserPostList extends ModelList {

    String TYPE_USER_POST_LIST = "UserPostList";

    String USER_ID = "userId";

    class UserPostListTypeProvider extends ModelListTypeProvider {

        private Context mContext;

        public UserPostListTypeProvider(Context context) {
            super(TYPE_USER_POST_LIST);

            mContext = context;
        }

        @Override
        public void getIdentifyingFeatures(ModelFeatures features, List<String> identifyingFeatures) {
            super.getIdentifyingFeatures(features, identifyingFeatures);

            identifyingFeatures.add(USER_ID);
        }

        @Override
        protected Action getAction(ModelFeatures features, String actionName) {
            return null;
        }

        @Override
        protected void getModels(final Model model, Object... params) {
            model.set(STATE, STATE_LOADING);

            String userId = model.get(USER_ID);

            TTMOffice.runner(mContext).runForUI(Requests.userPosts(userId, 50, 0),
                    new RequestResponseCallback<PostListResponse>() {

                @Override
                public void onRequestResponse(RequestResponse<PostListResponse> response) {
                    if (response.isSuccessful()) {
                        List<PostResponse> postsResponse = response.getContent().getPosts();

                        List<ModelFeatures> posts = new ArrayList<>(postsResponse.size());

                        for (PostResponse postResponse: postsResponse) {
                            ModelFeatures postFeatures = postResponse.asModelFeatures();

                            Managers.registerModel(postFeatures);

                            posts.add(postFeatures);
                        }

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
