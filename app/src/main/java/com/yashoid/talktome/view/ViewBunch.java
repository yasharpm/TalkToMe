package com.yashoid.talktome.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.yashoid.mmv.Managers;
import com.yashoid.mmv.Model;
import com.yashoid.mmv.ModelFeatures;
import com.yashoid.mmv.Target;

import java.util.List;

public class ViewBunch extends LinearLayout {

    private static final int MINIMUM_VIEWS_TO_SHOW = 4;
    private static final int MAXIMUM_VIEWS_TO_SHOW = 5;

    public interface ViewBunchAdapter {

        void getBunch(int count, BunchCallback callback);

        ViewBunchItem createItem();

    }

    public interface ViewBunchItem extends Target {

        Model getModel();

        int measureHeight(int width);

        int getLines();

        void setMaximumAllowedLines(int maxLines);

    }

    public interface BunchCallback {

        void onBunchResult(boolean success, List<ModelFeatures> bunch);

    }

    public interface OnLoadCallback {

        void onLoadResult(boolean success);

    }

    private ViewBunchAdapter mAdapter;

    public ViewBunch(Context context) {
        super(context);
        initialize(context, null, 0);
    }

    public ViewBunch(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initialize(context, attrs, 0);
    }

    public ViewBunch(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context, attrs, defStyleAttr);
    }

    private void initialize(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        setOrientation(VERTICAL);
    }

    public void setAdapter(ViewBunchAdapter adapter) {
        mAdapter = adapter;
    }

    public void load(final OnLoadCallback callback) {
        mAdapter.getBunch(MAXIMUM_VIEWS_TO_SHOW, new BunchCallback() {

            @Override
            public void onBunchResult(boolean success, List<ModelFeatures> bunch) {
                if (!success) {
                    callback.onLoadResult(false);
                    return;
                }

                final int width = getWidth();
                final int maxHeight = getHeight();

                int height = 0;

                int bunchIndex = 0;
                int childIndex = 0;

                final int previousChildCount = getChildCount();

                while (bunchIndex < bunch.size() && bunchIndex < MAXIMUM_VIEWS_TO_SHOW && (bunchIndex < MINIMUM_VIEWS_TO_SHOW || height < maxHeight)) {
                    ViewBunchItem item;

                    if (getChildCount() > bunchIndex) {
                        item = (ViewBunchItem) getChildAt(childIndex);

                        Managers.unregisterTarget(item, item.getModel());

                        item.setMaximumAllowedLines(-1);
                    }
                    else {
                        item = mAdapter.createItem();
                    }

                    Managers.registerTarget(item, bunch.get(bunchIndex));

                    int itemHeight = item.measureHeight(width);

                    if (bunchIndex < MINIMUM_VIEWS_TO_SHOW) {
                        height += itemHeight;

                        if (((View) item).getParent() == null) {
                            View child = (View) item;
                            addView(child, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
                        }
                        else {
                            childIndex++;
                        }
                    }
                    else {
                        if (height + itemHeight < maxHeight) {
                            height += itemHeight;

                            if (((View) item).getParent() == null) {
                                addView((View) item);
                            }
                            else {
                                childIndex++;
                            }
                        }
                    }

                    bunchIndex++;
                }

                if (childIndex < previousChildCount) {
                    removeViews(childIndex, getChildCount() - childIndex);
                }

                while (height > maxHeight) {
                    ViewBunchItem item = findItemWithMaxLines();

                    if (item.getLines() > 1) {
                        height -= item.measureHeight(width);

                        item.setMaximumAllowedLines(item.getLines() - 1);

                        height += item.measureHeight(width);
                    }
                    else {
                        break;
                    }
                }

                callback.onLoadResult(true);
            }

        });
    }

    private ViewBunchItem findItemWithMaxLines() {
        ViewBunchItem item = null;
        int maxLines = 0;

        final int childCount = getChildCount();

        for (int i = 0; i < childCount; i++) {
            ViewBunchItem child = (ViewBunchItem) getChildAt(i);

            if (child.getLines() > maxLines) {
                item = child;
                maxLines = child.getLines();
            }
        }

        return item;
    }

}
