package com.yashoid.talktome.model.post;

import android.content.Context;

import com.yashoid.mmv.Action;
import com.yashoid.mmv.Managers;
import com.yashoid.mmv.Model;
import com.yashoid.mmv.ModelFeatures;
import com.yashoid.talktome.TTMOffice;
import com.yashoid.talktome.model.list.ModelList;
import com.yashoid.talktome.network.GetNotesOperation;

import java.util.List;

public interface MyPostList extends ModelList {

    String TYPE_MY_POST_LIST = "MyPostList";

    ModelFeatures FEATURES = new ModelFeatures.Builder().add(TYPE, TYPE_MY_POST_LIST).build();

    class MyPostListTypeProvider extends ModelListTypeProvider {

        private Context mContext;

        public MyPostListTypeProvider(Context context) {
            super(TYPE_MY_POST_LIST);

            mContext = context;

            Managers.registerModel(FEATURES);
        }

        @Override
        protected Action getAction(ModelFeatures features, String actionName) {
            return null;
        }

        @Override
        protected void getModels(final Model model, Object... params) {
//            model.set(STATE, STATE_LOADING);
//
//            final int count = (int) params[0];
//
//            TTMOffice.network().post(new GetNotesOperation(mContext, count, new GetNotesOperation.GetNotesCallback() {
//
//                @Override
//                public void onGetNotesResult(List<ModelFeatures> posts, Exception exception) {
//                    if (exception == null) {
//                        for (int i = 0; i < posts.size(); i++) {
//                            posts.get(i).set(TYPE, Post.TYPE_POST);
//                        }
//
//                        model.set(MODEL_LIST, posts);
//                        model.set(STATE, STATE_SUCCESS);
//                    }
//                    else {
//                        model.set(STATE, STATE_FAILURE);
//                    }
//                }
//
//            }));
        }

    }

}
