package com.opentalkz.model.pendingpost;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.yashoid.mmv.Action;
import com.yashoid.mmv.Managers;
import com.yashoid.mmv.Model;
import com.yashoid.mmv.ModelFeatures;
import com.yashoid.mmv.PersistentTarget;
import com.yashoid.network.RequestResponse;
import com.yashoid.network.RequestResponseCallback;
import com.opentalkz.R;
import com.opentalkz.TTMOffice;
import com.opentalkz.model.Basics;
import com.opentalkz.model.Stateful;
import com.opentalkz.model.post.MyPostList;
import com.opentalkz.model.post.Post;
import com.opentalkz.network.PostResponse;
import com.opentalkz.network.Requests;

import java.util.ArrayList;
import java.util.List;

public interface PendingPost extends Basics, Stateful {

    int MINIMUM_CONTENT_LENGTH = 24;

    String TYPE_PENDING_POST = "PendingPost";

    String CONTENT = "content";

    String ON_CONTENT_CHANGED = "onContentChanged";
    String SEND_POST = "sendPost";

    class PendingPostTypeProvider extends BaseTypeProvider {

        private Context mContext;

        public PendingPostTypeProvider(Context context) {
            super(TYPE_PENDING_POST);

            mContext = context;
        }

        @Override
        public Action getAction(ModelFeatures features, String actionName, Object... params) {
            switch (actionName) {
                case Action.ACTION_MODEL_NOT_EXISTED_IN_CACHE:
                case Action.ACTION_MODEL_LOADED_FROM_CACHE:
                    return mInitializationAction;
                case ON_CONTENT_CHANGED:
                    return mOnContentChangedAction;
                case SEND_POST:
                    return mSendPostAction;
            }

            return null;
        }

        private Action mSendPostAction = new Action() {

            @Override
            public Object perform(final Model model, Object... params) {
                String content = model.get(CONTENT);

                if (TextUtils.isEmpty(content)) {
                    Toast.makeText(mContext, R.string.pendingpost_no_content, Toast.LENGTH_SHORT).show();
                    return null;
                }

                if (content.length() < MINIMUM_CONTENT_LENGTH) {
                    Toast.makeText(mContext, mContext.getString(R.string.pendingpost_short_content, MINIMUM_CONTENT_LENGTH), Toast.LENGTH_SHORT).show();
                    return null;
                }

                model.set(STATE, STATE_LOADING);

                TTMOffice.runner(mContext).runUserAction(Requests.newPost(content, "fa", "IR"), new RequestResponseCallback<PostResponse>() {

                    @Override
                    public void onRequestResponse(RequestResponse<PostResponse> response) {
                        if (response.isSuccessful()) {
//                            addPostToMyPosts(postId, (String) model.get(CONTENT));

                            model.set(STATE, STATE_SUCCESS);

                            Toast.makeText(mContext, R.string.pendingpost_success, Toast.LENGTH_LONG).show();
                        }
                        else {
                            Log.e(TYPE_PENDING_POST, "Failed to add post.", response.getException());

                            model.set(STATE, STATE_FAILURE);

                            Toast.makeText(mContext, R.string.pendingpost_error, Toast.LENGTH_LONG).show();
                        }
                    }

                });

                return null;
            }

        };

        private Action mOnContentChangedAction = new Action() {

            @Override
            public Object perform(Model model, Object... params) {
                Integer state = model.get(STATE);

                if (state != null) {
                    model.set(CONTENT, params[0]);
                    model.cache(true);
                }

                return null;
            }

        };

        private Action mInitializationAction = new Action() {

            @Override
            public Object perform(Model model, Object... params) {
                model.set(STATE, STATE_IDLE);
                return null;
            }

        };

        private void addPostToMyPosts(final String postId, String content) {
            final ModelFeatures newPostFeatures = new ModelFeatures.Builder()
                    .add(TYPE, Post.TYPE_POST)
                    .add(Post.ID, postId)
                    .add(Post.CONTENT, content)
                    .add(Post.VIEWS, 0)
                    .add(Post.CREATED_TIME, System.currentTimeMillis())
                    .build();

            Managers.registerTarget(new PersistentTarget() {

                @Override
                public void setModel(Model model) {
                    Managers.unregisterTarget(this);

                    List<ModelFeatures> postList = model.get(MyPostList.MODEL_LIST);

                    if (postList == null) {
                        postList = new ArrayList<>();
                    }

                    postList.add(0, newPostFeatures);

                    model.set(MyPostList.MODEL_LIST, postList);
                    model.cache(true);
                }

                @Override
                public void onFeaturesChanged(String... featureNames) { }

            }, MyPostList.FEATURES);
        }

    }

}
