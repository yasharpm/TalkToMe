package com.opentalkz.model.list;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.yashoid.mmv.Model;
import com.yashoid.mmv.Target;

public abstract class TargetViewHolder extends RecyclerView.ViewHolder implements Target {

    private Model mModel;

    public TargetViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    protected Model getModel() {
        return mModel;
    }

    @Override
    public void setModel(Model model) {
        mModel = model;

        onModelChanged(mModel);
    }

    @Override
    public void onFeaturesChanged(String... featureNames) {
        onModelChanged(mModel);
    }

    abstract protected void onModelChanged(Model model);

}
