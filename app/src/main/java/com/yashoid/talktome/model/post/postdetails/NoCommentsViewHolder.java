package com.yashoid.talktome.model.post.postdetails;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.yashoid.talktome.R;
import com.yashoid.talktome.view.item.PointedInfoView;

public class NoCommentsViewHolder extends RecyclerView.ViewHolder {

    public static NoCommentsViewHolder newInstance(Context context) {
        PointedInfoView view = new PointedInfoView(context);

        view.setContentView(R.layout.view_nocomments);

        TextView textInfo = view.findViewById(R.id.text_info);

        textInfo.setText("هنوز نظری نوشته نشده است. اولین نظر را بنویس!");

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
