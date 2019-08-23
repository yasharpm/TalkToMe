package com.yashoid.talktome.model.post;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.yashoid.mmv.ModelFeatures;

import java.util.List;

public class PostListAdapter extends RecyclerView.Adapter {

    private static final int POST_MAX_LINES = 4;

    private List<ModelFeatures> mPosts = null;

    public PostListAdapter() {

    }

    public void setPosts(List<ModelFeatures> posts) {
        mPosts = posts;

        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        PostFullItemView view = new PostFullItemView(parent.getContext());

        view.setMaxLines(POST_MAX_LINES);

        return new RecyclerView.ViewHolder(view) { };
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ModelFeatures postFeatures = mPosts.get(position);

        PostFullItemView itemView = (PostFullItemView) holder.itemView;

        itemView.setPost(postFeatures);
    }

    @Override
    public int getItemCount() {
        return mPosts == null ? 0 : mPosts.size();
    }

}
