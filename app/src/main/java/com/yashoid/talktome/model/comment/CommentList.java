package com.yashoid.talktome.model.comment;

import com.yashoid.mmv.Action;
import com.yashoid.mmv.Model;
import com.yashoid.mmv.ModelFeatures;
import com.yashoid.talktome.model.list.ModelList;

public interface CommentList extends ModelList {

    String TYPE_COMMENT_LIST = "CommentList";

    String POST_ID = "postId";

    class CommentListTypeProvider extends ModelList.ModelListTypeProvider {

        public CommentListTypeProvider() {
            super(TYPE_COMMENT_LIST);
        }

        @Override
        protected Action getAction(ModelFeatures features, String actionName) {
            return null;
        }

        @Override
        protected void getModels(Model model, Object... params) {

        }

    }

}
