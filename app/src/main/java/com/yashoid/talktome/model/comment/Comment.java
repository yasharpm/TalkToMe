package com.yashoid.talktome.model.comment;

import android.content.Context;

import com.yashoid.mmv.Action;
import com.yashoid.mmv.Model;
import com.yashoid.mmv.ModelFeatures;
import com.yashoid.talktome.model.WithIndicator;
import com.yashoid.talktome.model.Basics;

public interface Comment extends Basics, WithIndicator {

    String TYPE_COMMENT = "Comment";

    String ID = "id";
    String CONTENT = "content";

    class CommentTypeProvider extends WithIndicator.WithIndicatorTypeProvider {

        public CommentTypeProvider(Context context) {
            super(context, TYPE_COMMENT);
        }

        @Override
        protected void onModelCreated(Model model) {

        }

        @Override
        protected Action getAction(ModelFeatures features, String actionName) {
            return null;
        }

    }

}
