package com.yashoid.talktome.model.post.postdetails;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.yashoid.mmv.Managers;
import com.yashoid.mmv.Model;
import com.yashoid.mmv.ModelFeatures;
import com.yashoid.mmv.Target;
import com.yashoid.talktome.model.comment.CommentList;
import com.yashoid.talktome.model.comment.CommentViewHolder;
import com.yashoid.talktome.model.post.Post;

import java.util.List;

public class PostDetailsAdapter extends RecyclerView.Adapter implements CommentList, Target {

    private static final int VIEW_TYPE_POST = 0;
    private static final int VIEW_TYPE_COMMENTS_TITLE = 1;
    private static final int VIEW_TYPE_LOADING = 2;
    private static final int VIEW_TYPE_RETRY = 3;
    private static final int VIEW_TYPE_COMMENT = 4;

    private ModelFeatures mPostFeatures;

    private ModelFeatures mCommentListFeatures;
    private Model mCommentListModel;

    public PostDetailsAdapter(ModelFeatures postFeatures) {
        mPostFeatures = postFeatures;

        mCommentListFeatures = new ModelFeatures.Builder()
                .add(TYPE, TYPE_COMMENT_LIST)
                .add(POST_ID, postFeatures.get(Post.ID))
                .build();

        Managers.registerTarget(this, mCommentListFeatures);
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return VIEW_TYPE_POST;
        }

        if (position == 1) {
            return VIEW_TYPE_COMMENTS_TITLE;
        }

        int state = mCommentListModel.get(STATE);

        if (state == STATE_LOADING) {
            return VIEW_TYPE_LOADING;
        }

        if (state == STATE_FAILURE) {
            return VIEW_TYPE_RETRY;
        }

        return VIEW_TYPE_COMMENT;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_TYPE_POST:
                return DetailedPostContentViewHolder.newInstance(parent.getContext());
            case VIEW_TYPE_COMMENTS_TITLE:
                return CommentsTitleViewHolder.newInstance(parent);
            case VIEW_TYPE_LOADING:
                return LoadingViewHolder.newInstance(parent.getContext());
            case VIEW_TYPE_RETRY:
                return RetryViewHolder.newInstance(parent.getContext());
            case VIEW_TYPE_COMMENT:
                return CommentViewHolder.newInstance(parent.getContext());
        }

        throw new IllegalArgumentException("Undefined view type '" + viewType + "'.");
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof DetailedPostContentViewHolder) {
            ((DetailedPostContentViewHolder) holder).setPost(mPostFeatures);
        }
        else if (holder instanceof RetryViewHolder) {
            RetryViewHolder retryTarget = (RetryViewHolder) holder;

            Managers.unregisterTarget(retryTarget);
            Managers.registerTarget(retryTarget, mCommentListFeatures);
        }
        else if (holder instanceof CommentViewHolder) {
            List<ModelFeatures> comments = mCommentListModel.get(MODEL_LIST);

            ModelFeatures commentFeatures = comments.get(position - 2);

            ((CommentViewHolder) holder).setComment(commentFeatures);
        }
    }

    @Override
    public int getItemCount() {
        int state = mCommentListModel.get(STATE);
        List<ModelFeatures> comments = mCommentListModel.get(MODEL_LIST);

        return 2 + (state == STATE_IDLE ? 0 : (state == STATE_SUCCESS ? comments.size() : 1));
    }

    @Override
    public void setModel(Model model) {
        mCommentListModel = model;

        onModelChanged();
    }

    @Override
    public void onFeaturesChanged(String... featureNames) {
        onModelChanged();
    }

    private void onModelChanged() {
        int state = mCommentListModel.get(STATE);

        if (state == STATE_IDLE) {
            mCommentListModel.perform(GET_MODELS);
        }

        notifyDataSetChanged();
    }

}
