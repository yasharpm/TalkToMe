package com.opentalkz.model.comment;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

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

        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        Managers.unregisterTarget(view);
        Managers.registerTarget(view, commentFeatures);
    }

}
