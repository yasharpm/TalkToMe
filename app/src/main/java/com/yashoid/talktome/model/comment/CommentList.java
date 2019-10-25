package com.yashoid.talktome.model.comment;

import android.content.Context;

import com.yashoid.mmv.Action;
import com.yashoid.mmv.Managers;
import com.yashoid.mmv.Model;
import com.yashoid.mmv.ModelFeatures;
import com.yashoid.mmv.PersistentTarget;
import com.yashoid.talktome.model.list.ModelList;
import com.yashoid.talktome.model.post.Post;

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

            Managers.registerTarget(new PersistentTarget() {

                @Override
                public void setModel(Model post) {
                    Managers.unregisterTarget(this);

                    List<ModelFeatures> comments = post.get(Post.COMMENTS);

                    model.set(MODEL_LIST, comments);
                    model.set(STATE, STATE_SUCCESS);
                }

                @Override
                public void onFeaturesChanged(String... featureNames) { }

            }, postFeatures);
        }

    }

}
