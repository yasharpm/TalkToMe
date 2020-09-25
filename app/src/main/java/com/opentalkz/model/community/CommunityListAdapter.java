package com.opentalkz.model.community;

import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.opentalkz.model.list.ModelListAdapter;
import com.opentalkz.model.list.TargetViewHolder;
import com.yashoid.mmv.Managers;
import com.yashoid.mmv.ModelFeatures;

import java.util.ArrayList;
import java.util.List;

public class CommunityListAdapter extends ModelListAdapter<ModelListAdapter.OnItemClickListener> {

    @Override
    public void setModels(List<ModelFeatures> models) {
        if (models == null) {
            models = new ArrayList<>();
        }
        else {
            models = new ArrayList<>(models);
        }

        models.add(0, null);

        super.setModels(models);
    }

    @Override
    protected TargetViewHolder onCreateItemViewHolder(ViewGroup parent, OnItemClickListener itemListener) {
        return CommunityViewHolder.newInstance(parent, itemListener);
    }

    @Override
    protected TargetViewHolder onCreateNoItemsViewHolder(ViewGroup parent, OnItemClickListener itemListener) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull TargetViewHolder holder, int position) {
        if (position == 0) {
            Managers.unregisterTarget(holder);
            holder.setModel(null);
            return;
        }

        super.onBindViewHolder(holder, position);
    }

}
