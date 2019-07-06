package com.yashoid.talktome.view.contentpager;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import androidx.annotation.NonNull;
import androidx.core.view.NestedScrollingParent2;
import androidx.core.view.ViewCompat;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.yashoid.talktome.R;

public class ContentPagerView extends ViewGroup implements NestedScrollingParent2 {

    private static final double SCROLL_REDUCTION_RATE = 0.9d;

    private PopupWindow mWindow;

    private ViewPager mPager;

    private PagerAdapter mAdapter;

    private float mRawScroll = 0;
    private int mRealScroll = 0;

    public ContentPagerView(Context context, PopupWindow window, PagerAdapter adapter, int startPage) {
        super(context);

        mWindow = window;

        mAdapter = adapter;

        mPager = new ViewPager(context);
        mPager.setId(R.id.contentpager_pager_id);
        mPager.setAdapter(mAdapter);

        if (startPage != -1) {
            mPager.setCurrentItem(startPage);
        }

        addView(mPager);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = getMeasuredWidth();
        int height = getMeasuredHeight();

        mPager.measure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        layoutPager();
    }

    private void layoutPager() {
        mRealScroll = (int) (Math.signum(mRawScroll) * Math.abs(Math.pow(Math.abs(mRawScroll), SCROLL_REDUCTION_RATE)));

        mPager.layout(0, mRealScroll, mPager.getMeasuredWidth(), mRealScroll + mPager.getMeasuredHeight());
    }

    @Override
    public boolean onStartNestedScroll(@NonNull View child, @NonNull View target, int axes, int type) {
        if (axes != ViewCompat.SCROLL_AXIS_VERTICAL) {
            return false;
        }

        if (type == ViewCompat.TYPE_NON_TOUCH) {
            return false;
        }

        return true;
    }

    @Override
    public void onNestedScrollAccepted(@NonNull View child, @NonNull View target, int axes, int type) {

    }

    @Override
    public void onStopNestedScroll(@NonNull View target, int type) {
        if (Math.abs(mRealScroll) > getHeight() / 5) {
            mWindow.dismiss();
        }
        else {
            mRawScroll = 0;
            mRealScroll = 0;

            layoutPager();
        }
    }

    private int mConsumedY = 0;

    @Override
    public void onNestedScroll(@NonNull View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int type) {
        mConsumedY = dyUnconsumed;

        mRawScroll += -dyUnconsumed;

        layoutPager();
    }

    @Override
    public void onNestedPreScroll(@NonNull View target, int dx, int dy, @NonNull int[] consumed, int type) {
        consumed[1] = mConsumedY;
    }

}
