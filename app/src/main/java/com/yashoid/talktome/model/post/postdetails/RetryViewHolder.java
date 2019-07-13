package com.yashoid.talktome.model.post.postdetails;

import android.content.Context;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.yashoid.mmv.Model;
import com.yashoid.mmv.Target;
import com.yashoid.talktome.model.list.ModelList;

public class RetryViewHolder extends RecyclerView.ViewHolder implements ModelList, Target, View.OnClickListener {

    public static RetryViewHolder newInstance(Context context) {
        Button button = new Button(context);
        button.setText("Retry");

        return new RetryViewHolder(button);
    }

    private Model mModel;

    private RetryViewHolder(@NonNull View itemView) {
        super(itemView);

        itemView.setOnClickListener(this);
    }

    @Override
    public void setModel(Model model) {
        mModel = model;
    }

    @Override
    public void onFeaturesChanged(String... featureNames) { }

    @Override
    public void onClick(View v) {
        if (mModel != null) {
            mModel.perform(GET_MODELS);
        }
    }

}
