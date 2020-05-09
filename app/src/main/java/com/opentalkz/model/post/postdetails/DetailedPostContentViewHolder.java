package com.opentalkz.model.post.postdetails;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.yashoid.mmv.ModelFeatures;

public class DetailedPostContentViewHolder extends RecyclerView.ViewHolder {

    public static DetailedPostContentViewHolder newInstance(Context context) {
        return new DetailedPostContentViewHolder(new DetailedPostContentView(context));
    }

    private DetailedPostContentViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    public void setPost(ModelFeatures postFeatures) {
        ((DetailedPostContentView) itemView).setPost(postFeatures);
    }

}
