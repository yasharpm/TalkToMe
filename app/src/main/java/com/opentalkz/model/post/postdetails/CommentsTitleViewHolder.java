package com.opentalkz.model.post.postdetails;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.opentalkz.R;

public class CommentsTitleViewHolder extends RecyclerView.ViewHolder {

    public static CommentsTitleViewHolder newInstance(ViewGroup parent) {
        return new CommentsTitleViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_commentstitle, parent, false));
    }

    private CommentsTitleViewHolder(@NonNull View itemView) {
        super(itemView);
    }

}
