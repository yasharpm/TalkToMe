package com.yashoid.talktome.post;

import android.os.Handler;

import com.yashoid.mmv.Action;
import com.yashoid.mmv.Model;
import com.yashoid.mmv.ModelFeatures;
import com.yashoid.talktome.list.ModelList;

import java.util.ArrayList;
import java.util.List;

public interface PostList extends ModelList {

    String TYPE_POST_LIST = "PostList";

    class PostListTypeProvider extends ModelListTypeProvider {

        public PostListTypeProvider() {
            super(TYPE_POST_LIST);
        }

        @Override
        protected Action getAction(ModelFeatures features, String actionName) {
            return null;
        }

        @Override
        protected void getModels(final Model model, Object... params) {
            model.set(STATE, STATE_LOADING);

            final int count = (int) params[0];

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    List<ModelFeatures> bunch = new ArrayList<>();

                    for (int i = 0; i < count; i++) {
                        ModelFeatures post = new ModelFeatures.Builder()
                                .add(TYPE, Post.TYPE_POST)
                                .add(Post.ID, i)
                                .add(Post.CONTENT, "این یک آزمایش واقعی به زبان سلیس فارسی میباشد \nکه میتواند خطاهای موجود را \nنمایان سازد!")
                                .build();

                        bunch.add(post);
                    }

                    model.set(MODEL_LIST, bunch);
                    model.set(STATE, STATE_SUCCESS);
                }
            }, 1000);
        }

    }

}
