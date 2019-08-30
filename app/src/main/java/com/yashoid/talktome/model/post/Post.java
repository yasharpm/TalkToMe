package com.yashoid.talktome.model.post;

import android.content.Context;
import android.text.TextUtils;

import com.yashoid.mmv.Action;
import com.yashoid.mmv.Managers;
import com.yashoid.mmv.Model;
import com.yashoid.mmv.ModelFeatures;
import com.yashoid.mmv.PersistentTarget;
import com.yashoid.talktome.TTMOffice;
import com.yashoid.talktome.evaluation.Eval;
import com.yashoid.talktome.evaluation.Events;
import com.yashoid.talktome.model.WithIndicator;
import com.yashoid.talktome.model.Basics;
import com.yashoid.talktome.model.Stateful;
import com.yashoid.talktome.model.comment.CommentList;
import com.yashoid.talktome.network.AddCommentOperation;

import java.util.List;

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

        private Context mContext;

        public PostTypeProvider(Context context) {
            super(context, TYPE_POST);

            mContext = context;
        }

        @Override
        public void getIdentifyingFeatures(ModelFeatures features, List<String> identifyingFeatures) {
            super.getIdentifyingFeatures(features, identifyingFeatures);
            identifyingFeatures.add(ID);
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
                final String pendingComment = model.get(PENDING_COMMENT);

                if (TextUtils.isEmpty(pendingComment)) {
                    return null;
                }

                model.set(POST_COMMENT_STATE, STATE_LOADING);

                final String postId = model.get(ID);

                TTMOffice.network().post(new AddCommentOperation(mContext, postId, pendingComment, new AddCommentOperation.OnAddCommentResultCallback() {

                    @Override
                    public void onCommentAdded() {
                        Eval.trackEvent(Events.EVENT_COMMENTED, postId);

                        model.set(POST_COMMENT_STATE, STATE_SUCCESS);
                        model.set(PENDING_COMMENT, "");

                        ModelFeatures commentListFeatures = new ModelFeatures.Builder()
                                .add(TYPE, CommentList.TYPE_COMMENT_LIST)
                                .add(CommentList.POST_ID, postId)
                                .build();

                        Managers.registerTarget(new PersistentTarget() {

                            @Override
                            public void setModel(Model model) {
                                Managers.unregisterTarget(this);

                                model.perform(CommentList.GET_MODELS);
                            }

                            @Override
                            public void onFeaturesChanged(String... featureNames) { }

                        }, commentListFeatures);
                    }

                    @Override
                    public void onFailedToAddComment(Exception exception) {
                        model.set(POST_COMMENT_STATE, STATE_FAILURE);
                    }

                }));

                return null;
            }

        };

    }

}
