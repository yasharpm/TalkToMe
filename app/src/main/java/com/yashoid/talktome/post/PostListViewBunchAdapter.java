package com.yashoid.talktome.post;

import android.content.Context;
import android.view.View;

import com.yashoid.mmv.Managers;
import com.yashoid.mmv.ModelFeatures;
import com.yashoid.talktome.list.ModelListViewBunchAdapter;
import com.yashoid.talktome.view.viewbunch.ViewBunch;

public class PostListViewBunchAdapter extends ModelListViewBunchAdapter {

    public PostListViewBunchAdapter(ModelFeatures postListFeatures) {
        super(postListFeatures);
    }

    @Override
    public ViewBunch.ViewBunchItem createItem(ViewBunch parent) {
        return new PostBunchItem(parent.getContext());
    }

    @Override
    protected void bindItem(ViewBunch.ViewBunchItem item, ModelFeatures modelFeatures) {
        Managers.registerTarget((PostItemView) item.getView(), modelFeatures);
    }

    @Override
    public void unbindItem(ViewBunch.ViewBunchItem item) {
        PostItemView view = (PostItemView) item.getView();

        Managers.unregisterTarget(view);
    }

    private static class PostBunchItem implements ViewBunch.ViewBunchItem {

        private PostItemView mView;

        protected PostBunchItem(Context context) {
            mView = new PostItemView(context);
        }

        @Override
        public int getLines() {
            return mView.getLines();
        }

        @Override
        public void setMaximumAllowedLines(int maxLines) {
            mView.setMaximumAllowedLines(maxLines);
        }

        @Override
        public View getView() {
            return mView;
        }

    }

}
