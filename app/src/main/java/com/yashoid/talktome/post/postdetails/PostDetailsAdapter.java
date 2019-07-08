package com.yashoid.talktome.post.postdetails;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.yashoid.mmv.ModelFeatures;

public class PostDetailsAdapter extends RecyclerView.Adapter {

    private ModelFeatures mPostFeatures;

    public PostDetailsAdapter(ModelFeatures postFeatures) {
        mPostFeatures = postFeatures;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return DetailedPostContentViewHolder.newInstance(parent.getContext());
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((DetailedPostContentViewHolder) holder).setPost(mPostFeatures);
    }

    @Override
    public int getItemCount() {
        return 1;
    }

}
