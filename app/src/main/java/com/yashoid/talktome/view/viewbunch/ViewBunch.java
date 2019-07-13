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

    private static final int MINIMUM_VIEWS_TO_SHOW = 6;
    private static final int MAXIMUM_VIEWS_TO_SHOW = 8;

    public interface ViewBunchAdapter {

        void setRequiredItemCount(int count);

        int getCount();

        ViewBunchItem createItem(ViewBunch parent);

        void bindItem(ViewBunchItem item, int position);

        void unbindItem(ViewBunchItem item);

        void registerDataSetObserver(DataSetObserver observer);

        void unregisterDataSetObserver(DataSetObserver observer);

    }

    public interface OnItemClickListener {

        void onItemClicked(ViewBunch parent, ViewBunchItem item, int position);

    }

    public interface ViewBunchItem {

        int getLines();

        void setMaximumAllowedLines(int maxLines);

        View getView();

    }

    private ViewBunchAdapter mAdapter;

    private List<ViewBunchItem> mItems = new ArrayList<>();

    private int mVisibleItemCount = 0;

    private OnItemClickListener mOnItemClickListener = null;

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

    public int getVisibleItemCount() {
        return mVisibleItemCount;
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

    public ViewBunchAdapter getAdapter() {
        return mAdapter;
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

            View view = item.getView();

            addView(view);

            view.setOnClickListener(mOnItemViewClickListener);

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

        mVisibleItemCount = mItems.size();

        for (int i = itemCount; i < mItems.size(); i++) {
            View child = mItems.get(i).getView();

            child.setVisibility(INVISIBLE);

            mVisibleItemCount--;
        }
    }

    private ViewBunchItem findItemWithMaxLines(int itemCount) {
        ViewBunchItem maxLinesItem = null;
        int maxLines = 0;

        for (int i = 0; i < itemCount; i++) {
            ViewBunchItem item = mItems.get(i);

            if (item.getLines() >= maxLines) {
                maxLinesItem = item;
                maxLines = item.getLines();
            }
        }

        return maxLinesItem;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    private OnClickListener mOnItemViewClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            notifyItemClicked(v);
        }

    };

    private void notifyItemClicked(View view) {
        if (mOnItemClickListener == null) {
            return;
        }

        for (int i = 0; i < mItems.size(); i++) {
            ViewBunchItem item = mItems.get(i);

            if (item.getView() == view) {
                mOnItemClickListener.onItemClicked(this, item, i);

                return;
            }
        }
    }

}
