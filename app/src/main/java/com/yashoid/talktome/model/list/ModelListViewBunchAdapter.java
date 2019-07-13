package com.yashoid.talktome.model.list;

import com.yashoid.mmv.Managers;
import com.yashoid.mmv.Model;
import com.yashoid.mmv.ModelFeatures;
import com.yashoid.mmv.Target;
import com.yashoid.talktome.view.viewbunch.BaseViewBunchAdapter;
import com.yashoid.talktome.view.viewbunch.ViewBunch;

import java.util.ArrayList;
import java.util.List;

public abstract class ModelListViewBunchAdapter extends BaseViewBunchAdapter implements ModelList, Target {

    private Model mModel;

    private int mRequiredItemCount = 0;

    private List<ModelFeatures> mModelFeatures = new ArrayList<>();

    public ModelListViewBunchAdapter(ModelFeatures modelListFeatures) {
        Managers.registerTarget(this, modelListFeatures);
    }

    @Override
    public void setModel(Model model) {
        mModel = model;

        onModelChanged();
    }

    @Override
    public void onFeaturesChanged(String... featureNames) {
        onModelChanged();
    }

    private void onModelChanged() {
        List<ModelFeatures> modelFeatures = mModel.get(MODEL_LIST);

        if (modelFeatures != null) {
            mModelFeatures.clear();
            mModelFeatures.addAll(modelFeatures);

            notifyDataSetChanged();
        }
    }

    @Override
    public void setRequiredItemCount(int count) {
        mRequiredItemCount = count;

        if ((int) mModel.get(STATE) == STATE_SUCCESS && mModelFeatures.size() < mRequiredItemCount) {
            mModel.perform(GET_MODELS, mRequiredItemCount);
        }
    }

    public int getRequiredItemCount() {
        return mRequiredItemCount;
    }

    @Override
    public int getCount() {
        return mModelFeatures.size();
    }

    @Override
    public void bindItem(ViewBunch.ViewBunchItem item, int position) {
        bindItem(item, mModelFeatures.get(position));
    }

    abstract protected void bindItem(ViewBunch.ViewBunchItem item, ModelFeatures modelFeatures);

}
