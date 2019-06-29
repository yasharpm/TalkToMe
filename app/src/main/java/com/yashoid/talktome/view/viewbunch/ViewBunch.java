package com.yashoid.talktome.view.viewbunch;

import android.content.Context;
import android.database.DataSetObserver;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ViewBunch extends ViewGroup {

    private static final int MINIMUM_VIEWS_TO_SHOW = 4;
    private static final int MAXIMUM_VIEWS_TO_SHOW = 5;

    public interface ViewBunchAdapter {

        void setRequiredItemCount(int count);

        int getCount();

        ViewBunchItem createItem(ViewBunch parent);

        void bindItem(ViewBunchItem item, int position);

        void unbindItem(ViewBunchItem item);

        void registerDataSetObserver(DataSetObserver observer);

        void unregisterDataSetObserver(DataSetObserver observer);

    }

    public interface ViewBunchItem {

        int getLines();

        void setMaximumAllowedLines(int maxLines);

        View getView();

    }

    private ViewBunchAdapter mAdapter;

    private List<ViewBunchItem> mItems = new ArrayList<>();

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

    }

    public int getRequiredItemCount() {
        return MAXIMUM_VIEWS_TO_SHOW;
    }

    public void setAdapter(ViewBunchAdapter adapter) {
        if (mAdapter != null) {
            mAdapter.unregisterDataSetObserver(mAdapterObserver);

            for (ViewBunchItem item: mItems) {
                mAdapter.unbindItem(item);

                removeView(item.getView());
            }

            mItems.clear();
        }

        mAdapter = adapter;

        if (mAdapter != null) {
            mAdapter.registerDataSetObserver(mAdapterObserver);

            mAdapter.setRequiredItemCount(MAXIMUM_VIEWS_TO_SHOW);

            refreshAdapterData();
        }
        else {
            requestLayout();
        }
    }

    private DataSetObserver mAdapterObserver = new DataSetObserver() {

        @Override
        public void onChanged() {
            refreshAdapterData();
        }

    };

    private void refreshAdapterData() {
        for (ViewBunchItem item: mItems) {
            mAdapter.unbindItem(item);
        }

        int count = Math.min(mAdapter.getCount(), MAXIMUM_VIEWS_TO_SHOW);

        while (mItems.size() > count) {
            ViewBunchItem item = mItems.remove(0);

            removeView(item.getView());
        }

        while (mItems.size() < count) {
            ViewBunchItem item = mAdapter.createItem(this);

            addView(item.getView());

            mItems.add(item);
        }

        for (int i = 0; i < mItems.size(); i++) {
            ViewBunchItem item = mItems.get(i);

            mAdapter.bindItem(item, i);

            item.setMaximumAllowedLines(-1);
        }

        requestLayout();
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final int width = getWidth();

        int height = 0;
        int itemCount = 0;

        final int childWidthSpec = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY);
        final int childHeightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);

        for (int i = 0; i < mItems.size(); i++) {
            ViewBunchItem item = mItems.get(i);
            View child = item.getView();

            child.measure(childWidthSpec, childHeightSpec);

            if (i < MINIMUM_VIEWS_TO_SHOW || height + child.getMeasuredHeight() < getHeight()) {
                height += child.getMeasuredHeight();
                itemCount++;
            }
        }

        while (height > getHeight()) {
            ViewBunchItem item = findItemWithMaxLines(itemCount);
            View child = item.getView();

            height -= child.getMeasuredHeight();

            item.setMaximumAllowedLines(item.getLines() - 1);

            child.measure(childWidthSpec, childHeightSpec);

            height += child.getMeasuredHeight();
        }

        int top = 0;

        for (int i = 0; i < itemCount; i++) {
            View child = mItems.get(i).getView();

            child.setVisibility(VISIBLE);
            child.layout(0, top, width, top + child.getMeasuredHeight());

            top += child.getMeasuredHeight();
        }

        for (int i = itemCount; i < mItems.size(); i++) {
            View child = mItems.get(i).getView();

            child.setVisibility(INVISIBLE);
        }
    }

    private ViewBunchItem findItemWithMaxLines(int itemCount) {
        ViewBunchItem maxLinesItem = null;
        int maxLines = 0;

        for (int i = 0; i < itemCount; i++) {
            ViewBunchItem item = mItems.get(i);

            if (item.getLines() > maxLines) {
                maxLinesItem = item;
                maxLines = item.getLines();
            }
        }

        return maxLinesItem;
    }

//    @Nullable
//    @Override
//    protected Parcelable onSaveInstanceState() {
//        final int childCount = getChildCount();
//
//        int[] ids = new int[childCount];
//
//        for (int i = 0; i < childCount; i++) {
//            ViewBunchItem child = (ViewBunchItem) getChildAt(i);
//
//            Model model = child.getModel();
//
//            ids[i] = model.get(Post.ID);
//        }
//
//        return new SavedState(super.onSaveInstanceState(), ids);
//    }
//
//    @Override
//    protected void onRestoreInstanceState(Parcelable state) {
//        SavedState savedState = (SavedState) state;
//
//        super.onRestoreInstanceState(savedState.getSuperState());
//
//        int[] ids = savedState.mIds;
//
//        List<ModelFeatures> posts = new ArrayList<>(ids.length);
//
//        for (int i = 0; i < ids.length; i++) {
//            ModelFeatures features = new ModelFeatures.Builder()
//                    .add(Post.TYPE, Post.TYPE_POST)
//                    .add(Post.ID, ids[i])
//                    .build();
//
//            posts.add(features);
//        }
//
//        onBunchResult(true, posts);
//    }

    private static class SavedState extends BaseSavedState {

        private int[] mIds;

        private SavedState(Parcel source) {
            super(source);

            mIds = source.createIntArray();
        }

        public SavedState(Parcelable superState, int[] ids) {
            super(superState);

            mIds = ids;
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);

            out.writeIntArray(mIds);
        }

        public static Creator<SavedState> CREATOR = new Creator<SavedState>() {

            @Override
            public SavedState createFromParcel(Parcel source) {
                return new SavedState(source);
            }

            @Override
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }

        };

    }

}
