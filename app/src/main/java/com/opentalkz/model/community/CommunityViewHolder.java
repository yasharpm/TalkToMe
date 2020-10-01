package com.opentalkz.model.community;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.opentalkz.model.list.ModelListAdapter;
import com.opentalkz.model.list.TargetViewHolder;
import com.yashoid.mmv.Model;
import com.yashoid.mmv.ModelFeatures;

public class CommunityViewHolder extends TargetViewHolder {

    public static CommunityViewHolder newInstance(ViewGroup parent, final ModelListAdapter.OnItemClickListener onItemClickListener) {
        return new CommunityViewHolder(new CommunityItemView(parent.getContext()), onItemClickListener);
    }

    private CommunityViewHolder(@NonNull View itemView, final ModelListAdapter.OnItemClickListener onItemClickListener) {
        super(itemView);

        itemView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                ModelFeatures features = null;

                if (getModel() != null) {
                    features = new ModelFeatures.Builder()
                            .addAll(getModel().getAllFeatures())
                            .build();
                }

                onItemClickListener.onItemClicked(getBindingAdapterPosition(), features);
            }

        });
    }

    @Override
    protected void onModelChanged(Model model) {
        ((CommunityItemView) itemView).setModel(model);
    }

}
