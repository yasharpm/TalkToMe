package com.opentalkz.model.list;

import android.content.Context;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.yashoid.mmv.Managers;
import com.yashoid.mmv.ModelFeatures;

import java.util.List;

public abstract class ModelListAdapter<L extends ModelListAdapter.OnItemClickListener>
        extends RecyclerView.Adapter<TargetViewHolder> {

    private static final int TYPE_ITEM = 0;
    private static final int TYPE_NO_ITEMS = 1;

    public interface OnItemClickListener {

        void onItemClicked(int position, ModelFeatures modelFeatures);

    }

    private List<ModelFeatures> mModels = null;

    private L mOnItemClickListener = null;

    public ModelListAdapter() {

    }

    public final void setOnItemClickListener(L listener) {
        mOnItemClickListener = listener;
    }

    protected L getOnItemClickListener() {
        return mOnItemClickListener;
    }

    public void setModels(List<ModelFeatures> models) {
        mModels = models;

        notifyDataSetChanged();
    }

    @Override
    public final int getItemViewType(int position) {
        return (mModels == null || !mModels.isEmpty()) ? TYPE_ITEM : TYPE_NO_ITEMS;
    }

    @NonNull
    @Override
    public final TargetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();

        switch (viewType) {
            case TYPE_ITEM:
                return onCreateItemViewHolder(parent, mOnItemClickListener);
            case TYPE_NO_ITEMS:
                return onCreateNoItemsViewHolder(parent, mOnItemClickListener);
        }

        throw new IllegalArgumentException("Unrecognized view type '" + viewType + "'.");
    }

    abstract protected TargetViewHolder onCreateItemViewHolder(ViewGroup parent, L itemListener);

    abstract protected TargetViewHolder onCreateNoItemsViewHolder(ViewGroup parent, L itemListener);

    @Override
    public void onBindViewHolder(@NonNull TargetViewHolder holder, int position) {
        if (mModels == null || mModels.isEmpty()) {
            // Bypassing the binding of the empty view.
            return;
        }

        ModelFeatures modelFeatures = mModels.get(position);

        Managers.unregisterTarget(holder);
        Managers.registerTarget(holder, modelFeatures);
    }

    @Override
    public final int getItemCount() {
        return mModels == null ? 0 : (mModels.isEmpty() ? 1 : mModels.size());
    }

    protected void onItemClicked(int position) {
        if (mOnItemClickListener != null) {
            mOnItemClickListener.onItemClicked(position, mModels.get(position));
        }
    }

}
