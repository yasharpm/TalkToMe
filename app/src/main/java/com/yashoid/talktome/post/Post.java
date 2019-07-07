package com.yashoid.talktome.post;

import android.content.Context;
import android.os.Handler;

import com.yashoid.mmv.Action;
import com.yashoid.mmv.Model;
import com.yashoid.mmv.ModelFeatures;
import com.yashoid.mmv.TypeProvider;
import com.yashoid.talktome.Basics;
import com.yashoid.talktome.R;
import com.yashoid.talktome.Stateful;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public interface Post extends Basics, Stateful {

    String TYPE_POST = "Post";

    String ID = "id";
    String CONTENT = "content";

    String INDICATOR_COLOR = "indicatorColor";
    String PENDING_COMMENT = "pendingComment";
    String POST_COMMENT_STATE = "postCommentState";

    String POST_COMMENT = "postComment";

    class PostTypeProvider implements TypeProvider {

        private int[] mColors;
        private int mColorIndex = 0;

        public PostTypeProvider(Context context) {
            int[] colors = context.getResources().getIntArray(R.array.indicatorColors);

            List<Integer> tempColors = new ArrayList<>(colors.length);

            for (int color: colors) {
                tempColors.add(color);
            }

            mColors = new int[colors.length];

            Random random = new Random();

            for (int i = 0; i < mColors.length; i++) {
                int index = random.nextInt(tempColors.size());

                mColors[i] = tempColors.remove(index);
            }
        }

        @Override
        public Action getAction(ModelFeatures features, String actionName, Object... params) {
            switch (actionName) {
                case Action.ACTION_MODEL_CREATED:
                    return mCreationAction;
                case POST_COMMENT:
                    return mPostCommentAction;
            }

            return null;
        }

        private Action mCreationAction = new Action() {

            @Override
            public Object perform(Model model, Object... params) {
                int color = mColors[mColorIndex++];

                if (mColorIndex == mColors.length) {
                    mColorIndex = 0;
                }

                model.set(INDICATOR_COLOR, color);

                model.set(POST_COMMENT_STATE, STATE_IDLE);

                return null;
            }

        };

        private Action mPostCommentAction = new Action() {

            @Override
            public Object perform(final Model model, Object... params) {
                // TODO
                model.set(POST_COMMENT_STATE, STATE_LOADING);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        model.set(POST_COMMENT_STATE, STATE_SUCCESS);
                        model.set(PENDING_COMMENT, "");
                    }
                }, 1000);

                return null;
            }

        };

    }

}
