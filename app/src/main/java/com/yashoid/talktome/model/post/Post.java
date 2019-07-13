package com.yashoid.talktome.model.post;

import android.content.Context;
import android.os.Handler;

import com.yashoid.mmv.Action;
import com.yashoid.mmv.Model;
import com.yashoid.mmv.ModelFeatures;
import com.yashoid.talktome.model.WithIndicator;
import com.yashoid.talktome.model.Basics;
import com.yashoid.talktome.model.Stateful;

public interface Post extends Basics, WithIndicator, Stateful {

    String TYPE_POST = "Post";

    String ID = "id";
    String CONTENT = "content";
    String CREATED_TIME = "createdTime";
    String VIEWS = "views";

    String PENDING_COMMENT = "pendingComment";
    String POST_COMMENT_STATE = "postCommentState";

    String POST_COMMENT = "postComment";

    class PostTypeProvider extends WithIndicatorTypeProvider {

        public PostTypeProvider(Context context) {
            super(context, TYPE_POST);
        }

        @Override
        protected void onModelCreated(Model model) {
            model.set(POST_COMMENT_STATE, STATE_IDLE);
        }

        @Override
        protected Action getAction(ModelFeatures features, String actionName) {
            switch (actionName) {
                case POST_COMMENT:
                    return mPostCommentAction;
            }

            return null;
        }

        private Action mPostCommentAction = new Action() {

            @Override
            public Object perform(final Model model, Object... params) {
                // TODO
                model.set(POST_COMMENT_STATE, STATE_LOADING);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        model.set(POST_COMMENT_STATE, STATE_SUCCESS);
                        model.set(PENDING_COMMENT, "");
                    }
                }, 1000);

                return null;
            }

        };

    }

}
