package com.yashoid.talktome.model.post;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.yashoid.mmv.ModelFeatures;

import java.util.List;

public class PostListAdapter extends RecyclerView.Adapter {

    private static final int POST_MAX_LINES = 4;

    public interface OnItemClickListener {

        void onItemClicked(int position, ModelFeatures modelFeatures);

    }

    private List<ModelFeatures> mPosts = null;

    private OnItemClickListener mOnItemClickListener = null;

    public PostListAdapter() {

    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mOnItemClickListener = listener;
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

        final RecyclerView.ViewHolder viewHolder = new RecyclerView.ViewHolder(view) { };

        view.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                onItemClicked(viewHolder.getAdapterPosition());
            }

        });

        return viewHolder;
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

    private void onItemClicked(int position) {
        if (mOnItemClickListener != null) {
            mOnItemClickListener.onItemClicked(position, mPosts.get(position));
        }
    }

}
