package com.yashoid.talktome.model.post;

import com.yashoid.mmv.Action;
import com.yashoid.mmv.Model;
import com.yashoid.mmv.ModelFeatures;
import com.yashoid.talktome.model.list.ModelList;

import java.util.List;

public interface PostList extends ModelList {

    String TYPE_POST_LIST = "PostList";

    String PROGRESSIVE_ID = "progressive_id";

    class PostListTypeProvider extends ModelListTypeProvider {

        private static int sIdCounter = 0;

        public PostListTypeProvider() {
            super(TYPE_POST_LIST);
        }

        @Override
        protected void onModelInitialized(Model model) {
            super.onModelInitialized(model);

            model.set(PROGRESSIVE_ID, sIdCounter++);
        }

        @Override
        public void getIdentifyingFeatures(ModelFeatures features, List<String> identifyingFeatures) {
            super.getIdentifyingFeatures(features, identifyingFeatures);

            identifyingFeatures.add(PROGRESSIVE_ID);
        }

        @Override
        protected Action getAction(ModelFeatures features, String actionName) {
            return null;
        }

        @Override
        protected void getModels(final Model model, Object... params) {
            model.set(STATE, STATE_SUCCESS);
        }

    }

}
