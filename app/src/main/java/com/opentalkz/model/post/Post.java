package com.opentalkz.model.post;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.yashoid.mmv.Action;
import com.yashoid.mmv.Managers;
import com.yashoid.mmv.Model;
import com.yashoid.mmv.ModelFeatures;
import com.yashoid.mmv.PersistentTarget;
import com.yashoid.network.RequestResponse;
import com.yashoid.network.RequestResponseCallback;
import com.opentalkz.TTMOffice;
import com.opentalkz.evaluation.Eval;
import com.opentalkz.evaluation.Events;
import com.opentalkz.model.WithIndicator;
import com.opentalkz.model.Basics;
import com.opentalkz.model.Stateful;
import com.opentalkz.model.comment.CommentList;
import com.opentalkz.network.CommentResponse;
import com.opentalkz.network.Requests;

import java.util.ArrayList;
import java.util.List;

public interface Post extends Basics, WithIndicator, Stateful {

    String TYPE_POST = "Post";

    String ID = "id";
    String CONTENT = "content";
    String CREATED_TIME = "createdTime";
    String VIEWS = "views";
    String LIKES = "likes";
    String COMMENTS = "comments";

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

                TTMOffice.runner(mContext).runUserAction(Requests.newComment(postId, pendingComment), new RequestResponseCallback<CommentResponse>() {

                    @Override
                    public void onRequestResponse(RequestResponse<CommentResponse> response) {
                        if (response.isSuccessful()) {
                            Eval.trackEvent(Events.EVENT_COMMENTED, postId);

                            model.set(POST_COMMENT_STATE, STATE_SUCCESS);
                            model.set(PENDING_COMMENT, "");

                            List<ModelFeatures> comments = model.get(COMMENTS);

                            if (comments == null) {
                                comments = new ArrayList<>();
                            }

                            comments.add(0, response.getContent().asModelFeatures());

                            model.set(COMMENTS, comments);

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
                        else {
                            Log.e(TYPE_POST, "Failed to post the comment.", response.getException());

                            model.set(POST_COMMENT_STATE, STATE_FAILURE);
                        }
                    }

                });

                return null;
            }

        };

    }

}
