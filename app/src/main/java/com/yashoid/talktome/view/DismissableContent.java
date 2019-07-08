package com.yashoid.talktome.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.view.NestedScrollingParent2;
import androidx.core.view.ViewCompat;

public class DismissableContent extends ViewGroup implements NestedScrollingParent2 {

    private static final double SCROLL_REDUCTION_RATE = 0.9d;

    public interface OnDismissListener {

        void onDismissed(DismissableContent view);

    }

    private OnDismissListener mOnDismissListener = null;

    private View mContent = null;

    private float mRawScroll = 0;
    private int mRealScroll = 0;

    public DismissableContent(Context context) {
        super(context);
        initialize(context, null, 0);
    }

    public DismissableContent(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context, attrs, 0);
    }

    public DismissableContent(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context, attrs, defStyleAttr);
    }

    private void initialize(Context context, AttributeSet attrs, int defStyleAttr) {

    }

    @Override
    public void onViewAdded(View child) {
        super.onViewAdded(child);

        if (getChildCount() > 1) {
            throw new RuntimeException("DismissableContent can not have more than one child.");
        }

        mContent = child;
    }

    public void setOnDismissListener(OnDismissListener listener) {
        mOnDismissListener = listener;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = getMeasuredWidth();
        int height = getMeasuredHeight();

        if (mContent != null) {
            mContent.measure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        layoutContent();
    }

    private void layoutContent() {
        mRealScroll = (int) (Math.signum(mRawScroll) * Math.abs(Math.pow(Math.abs(mRawScroll), SCROLL_REDUCTION_RATE)));

        if (mContent != null) {
            mContent.measure(MeasureSpec.makeMeasureSpec(getWidth(), MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(getHeight(), MeasureSpec.EXACTLY));
            mContent.layout(0, mRealScroll, mContent.getMeasuredWidth(), mRealScroll + mContent.getMeasuredHeight());
        }
    }

    @Override
    public boolean onStartNestedScroll(@NonNull View child, @NonNull View target, int axes, int type) {
        if (axes != ViewCompat.SCROLL_AXIS_VERTICAL) {
            return false;
        }

        if (type == ViewCompat.TYPE_NON_TOUCH) {
            return false;
        }

        return false;//TODO
    }

    @Override
    public void onNestedScrollAccepted(@NonNull View child, @NonNull View target, int axes, int type) {

    }

    @Override
    public void onStopNestedScroll(@NonNull View target, int type) {
        if (Math.abs(mRealScroll) > getHeight() / 5) {
            if (mOnDismissListener != null) {
                mOnDismissListener.onDismissed(this);
            }
            else {
                // Fallback. Dismiss listener should never be null.
                mRawScroll = 0;
                mRealScroll = 0;
            }
        }
        else {
            mRawScroll = 0;
            mRealScroll = 0;

            requestLayout();
        }
    }

    private int mConsumedY = 0;

    @Override
    public void onNestedScroll(@NonNull View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int type) {
        mConsumedY = dyUnconsumed;

        mRawScroll += -dyUnconsumed;

        requestLayout();
    }

    @Override
    public void onNestedPreScroll(@NonNull View target, int dx, int dy, @NonNull int[] consumed, int type) {
        consumed[1] = mConsumedY;
    }

}
