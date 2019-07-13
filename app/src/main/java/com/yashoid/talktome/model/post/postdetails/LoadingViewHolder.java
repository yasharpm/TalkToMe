package com.yashoid.talktome.model.post.postdetails;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.yashoid.talktome.view.LoadingDrawable;

public class LoadingViewHolder extends RecyclerView.ViewHolder {

    public static LoadingViewHolder newInstance(Context context) {
        ImageView loadingView = new ImageView(context);
        loadingView.setImageDrawable(new LoadingDrawable(context));

        return new LoadingViewHolder(loadingView);
    }

    private LoadingViewHolder(@NonNull View itemView) {
        super(itemView);
    }

}
