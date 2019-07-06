package com.yashoid.talktome.post;

import android.content.Context;
import android.os.Handler;

import com.yashoid.mmv.Action;
import com.yashoid.mmv.Model;
import com.yashoid.mmv.ModelFeatures;
import com.yashoid.network.NetworkOperator;
import com.yashoid.talktome.list.ModelList;
import com.yashoid.talktome.network.GetNotesOperation;

import java.util.ArrayList;
import java.util.List;

public interface PostList extends ModelList {

    String TYPE_POST_LIST = "PostList";

    class PostListTypeProvider extends ModelListTypeProvider {

        private Context mContext;

        public PostListTypeProvider(Context context) {
            super(TYPE_POST_LIST);

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

            NetworkOperator.getInstance().post(new GetNotesOperation(mContext, count, new GetNotesOperation.GetNotesCallback() {

                @Override
                public void onGetNotesResult(List<ModelFeatures> posts, Exception exception) {
                    if (exception == null) {
                        for (int i = 0; i < posts.size(); i++) {
                            posts.get(i).set(TYPE, Post.TYPE_POST);
                        }

                        model.set(MODEL_LIST, posts);
                        model.set(STATE, STATE_SUCCESS);
                    }
                    else {
                        model.set(STATE, STATE_FAILURE);
                    }
                }

            }));
        }

    }

}
