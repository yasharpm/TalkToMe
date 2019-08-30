package com.yashoid.talktome.model.post;

import android.content.Context;
import android.text.TextUtils;

import com.yashoid.mmv.Action;
import com.yashoid.mmv.Managers;
import com.yashoid.mmv.Model;
import com.yashoid.mmv.ModelFeatures;
import com.yashoid.talktome.TTMCompat;
import com.yashoid.talktome.TTMOffice;
import com.yashoid.talktome.model.list.ModelList;
import com.yashoid.talktome.network.GetPostUpdatesOperation;
import com.yashoid.talktome.network.postupdate.ChangedPostUpdate;
import com.yashoid.talktome.network.postupdate.NewPostUpdate;
import com.yashoid.talktome.network.postupdate.PostUpdate;
import com.yashoid.talktome.network.postupdate.RemovedPostUpdate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public interface MyPostList extends ModelList {

    String TYPE_MY_POST_LIST = "MyPostList";

    ModelFeatures FEATURES = new ModelFeatures.Builder().add(TYPE, TYPE_MY_POST_LIST).build();

    class MyPostListTypeProvider extends ModelListTypeProvider {

        private Context mContext;

        public MyPostListTypeProvider(Context context) {
            super(TYPE_MY_POST_LIST);

            mContext = context;
        }

        @Override
        protected Action getAction(ModelFeatures features, String actionName) {
            if (Action.ACTION_MODEL_NOT_EXISTED_IN_CACHE.equals(actionName)) {
                return mFirstTimeAction;
            }

            return null;
        }

        @Override
        protected void getModels(final Model model, Object... params) {
            model.set(STATE, STATE_LOADING);

            List<ModelFeatures> posts = model.get(MODEL_LIST);

            List<String> postIds = new ArrayList<>();

            if (posts != null) {
                for (ModelFeatures post: posts) {
                    postIds.add((String) post.get(Post.ID));
                }
            }

            TTMOffice.network().post(new GetPostUpdatesOperation(mContext, postIds, new GetPostUpdatesOperation.GetPostUpdatesCallback() {

                @Override
                public void onPostUpdatedResult(List<PostUpdate> updates, Exception exception) {
                    if (updates != null) {
                        applyPostUpdates(model, updates);

                        model.cache(true);
                        model.set(STATE, STATE_SUCCESS);
                    }
                    else {
                        model.set(STATE, STATE_FAILURE);
                    }
                }

            }));
        }

        private Action mFirstTimeAction = new Action() {

            @Override
            public Object perform(Model model, Object... params) {
                List<ModelFeatures> legacyPosts = TTMCompat.getLegacyNotes(mContext);

                if (legacyPosts != null && !legacyPosts.isEmpty()) {
                    model.set(MODEL_LIST, legacyPosts);
                }

                return null;
            }

        };

        private void applyPostUpdates(Model model, List<PostUpdate> updates) {
            for (PostUpdate update: updates) {
                applyPostUpdate(model, update);
            }
        }

        private void applyPostUpdate(Model model, PostUpdate update) {
            List<ModelFeatures> postFeatures = model.get(MODEL_LIST);

            if (postFeatures == null) {
                postFeatures = new ArrayList<>();
            }

            if (update instanceof NewPostUpdate) {
                postFeatures.add(0, ((NewPostUpdate) update).getPost());
            }
            else if (update instanceof ChangedPostUpdate) {
                ModelFeatures postUpdates = ((ChangedPostUpdate) update).getChanges();

                Managers.registerModel(postUpdates);

                ModelFeatures post = findPost(postFeatures, (String) postUpdates.get(Post.ID));

                if (post != null) {
                    Map<String, Object> updateMap = postUpdates.getAll();

                    for (Map.Entry<String, Object> updateEntry: updateMap.entrySet()) {
                        post.set(updateEntry.getKey(), updateEntry.getValue());
                    }
                }
            }
            else if (update instanceof RemovedPostUpdate) {
                ModelFeatures post = findPost(postFeatures, ((RemovedPostUpdate) update).getPostId());

                if (post != null) {
                    postFeatures.remove(post);
                }
            }

            model.set(MODEL_LIST, postFeatures);
        }

        private ModelFeatures findPost(List<ModelFeatures> list, String postId) {
            for (ModelFeatures post: list) {
                if (TextUtils.equals(postId, (String) post.get(Post.ID))) {
                    return post;
                }
            }

            return null;
        }

    }

}
