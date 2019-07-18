package com.yashoid.talktome.model.pendingpost;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.yashoid.mmv.Action;
import com.yashoid.mmv.Model;
import com.yashoid.mmv.ModelFeatures;
import com.yashoid.mmv.TypeProvider;
import com.yashoid.network.NetworkOperator;
import com.yashoid.talktome.R;
import com.yashoid.talktome.model.Basics;
import com.yashoid.talktome.model.Stateful;
import com.yashoid.talktome.network.AddPostOperation;

public interface PendingPost extends Basics, Stateful {

    int MINIMUM_CONTENT_LENGTH = 24;

    String TYPE_PENDING_POST = "PendingPost";

    String CONTENT = "content";

    String SEND_POST = "sendPost";

    class PendingPostTypeProvider implements TypeProvider {

        private Context mContext;

        public PendingPostTypeProvider(Context context) {
            mContext = context;
        }

        @Override
        public Action getAction(ModelFeatures features, String actionName, Object... params) {
            if (!TYPE_PENDING_POST.equals(features.get(TYPE))) {
                return null;
            }

            switch (actionName) {
                case Action.ACTION_MODEL_CREATED:
                    return mInitializationAction;
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

                NetworkOperator.getInstance().post(new AddPostOperation(mContext, content, new AddPostOperation.OnAddPostResultCallback() {

                    @Override
                    public void onPostAdded(String postId) {
                        // TODO Add post to user posts

                        model.set(STATE, STATE_SUCCESS);

                        Toast.makeText(mContext, R.string.pendingpost_success, Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onFailedToAddPost(Exception exception) {
                        Log.e(TYPE_PENDING_POST, "Failed to add post.", exception);

                        model.set(STATE, STATE_FAILURE);

                        Toast.makeText(mContext, R.string.pendingpost_error, Toast.LENGTH_LONG).show();
                    }

                }));

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

    }

}
