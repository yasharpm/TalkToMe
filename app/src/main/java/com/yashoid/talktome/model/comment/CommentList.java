package com.yashoid.talktome.model.comment;

import android.content.Context;
import android.util.Log;

import com.yashoid.mmv.Action;
import com.yashoid.mmv.Model;
import com.yashoid.mmv.ModelFeatures;
import com.yashoid.network.NetworkOperator;
import com.yashoid.talktome.model.list.ModelList;
import com.yashoid.talktome.network.GetCommentsOperation;

import java.util.List;

public interface CommentList extends ModelList {

    String TYPE_COMMENT_LIST = "CommentList";

    String POST_ID = "postId";

    class CommentListTypeProvider extends ModelList.ModelListTypeProvider {

        private Context mContext;

        public CommentListTypeProvider(Context context) {
            super(TYPE_COMMENT_LIST);

            mContext = context;
        }

        @Override
        protected Action getAction(ModelFeatures features, String actionName) {
            return null;
        }

        @Override
        protected void getModels(final Model model, Object... params) {
            model.set(STATE, STATE_LOADING);

            NetworkOperator.getInstance().post(new GetCommentsOperation(mContext, (String) model.get(POST_ID), new GetCommentsOperation.GetCommentsCallback() {

                @Override
                public void onGetCommentsResult(List<ModelFeatures> comments, Exception exception) {
                    if (exception == null) {
                        for (ModelFeatures comment: comments) {
                            comment.set(Comment.TYPE, Comment.TYPE_COMMENT);
                        }

                        model.set(MODEL_LIST, comments);
                        model.set(STATE, STATE_SUCCESS);
                    }
                    else {
                        Log.e(TYPE_COMMENT_LIST, "Failed to get comment list.", exception);

                        model.set(STATE, STATE_FAILURE);
                    }
                }

            }));
        }

    }

}
