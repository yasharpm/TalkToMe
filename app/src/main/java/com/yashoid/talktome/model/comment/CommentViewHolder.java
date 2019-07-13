package com.yashoid.talktome.model.comment;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.yashoid.mmv.Managers;
import com.yashoid.mmv.ModelFeatures;

public class CommentViewHolder extends RecyclerView.ViewHolder {

    public static CommentViewHolder newInstance(Context context) {
        return new CommentViewHolder(new CommentItemView(context));
    }

    private CommentViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    public void setComment(ModelFeatures commentFeatures) {
        CommentItemView view = (CommentItemView) itemView;

        Managers.unregisterTarget(view);
        Managers.registerTarget(view, commentFeatures);
    }

}
