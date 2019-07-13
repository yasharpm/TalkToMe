package com.yashoid.talktome.model.post.postdetails;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class NoCommentsViewHolder extends RecyclerView.ViewHolder {

    public static NoCommentsViewHolder newInstance(Context context) {
        TextView view = new TextView(context);

        view.setText("No Comments");

        view.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
                ));

        return new NoCommentsViewHolder(view);
    }

    private NoCommentsViewHolder(@NonNull View itemView) {
        super(itemView);
    }

}
