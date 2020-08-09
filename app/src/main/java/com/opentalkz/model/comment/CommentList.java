package com.opentalkz.model.comment;

import android.content.Context;

import com.yashoid.mmv.Action;
import com.yashoid.mmv.Managers;
import com.yashoid.mmv.Model;
import com.yashoid.mmv.ModelFeatures;
import com.yashoid.mmv.PersistentTarget;
import com.opentalkz.model.list.ModelList;
import com.opentalkz.model.post.Post;
import com.yashoid.mmv.SingleShotTarget;

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
        public void getIdentifyingFeatures(ModelFeatures features, List<String> identifyingFeatures) {
            super.getIdentifyingFeatures(features, identifyingFeatures);
            identifyingFeatures.add(POST_ID);
        }

        @Override
        protected Action getAction(ModelFeatures features, String actionName) {
            return null;
        }

        @Override
        protected void getModels(final Model model, Object... params) {
            model.set(STATE, STATE_LOADING);

            String postId = model.get(POST_ID);

            ModelFeatures postFeatures = new ModelFeatures.Builder()
                    .add(TYPE, Post.TYPE_POST)
                    .add(Post.ID, postId)
                    .build();

            SingleShotTarget.get(postFeatures, new SingleShotTarget.ModelCallback() {

                @Override
                public void onModelReady(Model post) {
                    List<ModelFeatures> comments = post.get(Post.COMMENTS);

                    model.set(MODEL_LIST, comments);
                    model.set(STATE, STATE_SUCCESS);
                }

            });
        }

    }

}
