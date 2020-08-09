package com.opentalkz.view;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.opentalkz.R;

public class LoadableContentView extends ViewGroup {

    public static final int STATE_IDLE = 0;
    public static final int STATE_SCROLLING = 1;
    public static final int STATE_LOADING = 2;
    public static final int STATE_SETTLING = 3;

    private static final double SCROLL_REDUCTION_RATE = 0.8d;
    private static final long SETTLE_DURATION = 200;

    public interface OnStateChangedListener {

        void onStateChanged(int state);

    }

    private OnStateChangedListener mOnStateChangedListener = null;

    private int mLoadAreaHeight;
    private int mLoadingSize;

    private LoadingDrawable mLoadingDrawable;

    private View mContentView = null;

    private int mState = STATE_IDLE;

    private float mDisplacementProgress = 0;

    private GestureDetector mGestureDetector;

    public LoadableContentView(Context context) {
        super(context);
        initialize(context, null, 0);
    }

    public LoadableContentView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context, attrs, 0);
    }

    public LoadableContentView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context, attrs, defStyleAttr);
    }

    private void initialize(Context context, AttributeSet attrs, int defStyleAttr) {
        setWillNotDraw(false);

        Resources res = getResources();

        mLoadAreaHeight = res.getDimensionPixelSize(R.dimen.loadablecontent_loadareaheight);
        mLoadingSize = res.getDimensionPixelSize(R.dimen.loadablecontent_loadingsize);

        mLoadingDrawable = new LoadingDrawable(context);
        mLoadingDrawable.setRotating(false);
        mLoadingDrawable.setBounds(-mLoadingSize / 2, -mLoadingSize / 2, mLoadingSize / 2, mLoadingSize / 2);
        mLoadingDrawable.setCallback(this);

        mGestureDetector = new GestureDetector(context, mOnGestureListener);
    }

    public void setOnStateChangedListener(OnStateChangedListener listener) {
        mOnStateChangedListener = listener;
    }

    public int getState() {
        return mState;
    }

    @Override
    public void onViewAdded(View child) {
        super.onViewAdded(child);

        if (mContentView != null) {
            throw new RuntimeException("LoadableContentView can not have more than one child.");
        }

        mContentView = child;
    }

    @Override
    public void onViewRemoved(View child) {
        super.onViewRemoved(child);

        mContentView = null;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mContentView == null) {
            return;
        }

        mContentView.measure(widthMeasureSpec, heightMeasureSpec);

        setMeasuredDimension(mContentView.getMeasuredWidthAndState(), mContentView.getMeasuredHeightAndState());
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (mContentView != null) {
            int displacement = (int) (mDisplacementProgress * mLoadAreaHeight);

            mContentView.layout(0, displacement, mContentView.getMeasuredWidth(), displacement + mContentView.getMeasuredHeight());
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int displacement = (int) (mDisplacementProgress * mLoadAreaHeight);

        if (displacement == 0) {
            return;
        }

        canvas.save();

        if (displacement > 0) {
            canvas.translate(getWidth() / 2f, displacement / 2f);
        }
        else {
            canvas.translate(getWidth() / 2f, getHeight() + displacement / 2f);
        }

        mLoadingDrawable.draw(canvas);

        canvas.restore();
    }

    @Override
    protected boolean verifyDrawable(@NonNull Drawable who) {
        return super.verifyDrawable(who) || mLoadingDrawable == who;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        mGestureDetector.onTouchEvent(ev);

        super.dispatchTouchEvent(ev);

        if (ev.getAction() != MotionEvent.ACTION_DOWN && ev.getAction() != MotionEvent.ACTION_MOVE) {
            if (mState != STATE_LOADING && mState != STATE_IDLE) {
                settle();
            }
        }

        return true;
    }

//    @Override
//    public boolean onInterceptTouchEvent(MotionEvent ev) {
//        return mState == STATE_SCROLLING || mState == STATE_SETTLING;
//    }
//
//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        boolean result = mGestureDetector.onTouchEvent(event);
//
//        if (event.getAction() != MotionEvent.ACTION_DOWN && event.getAction() != MotionEvent.ACTION_MOVE) {
//            if (mState != STATE_LOADING && mState != STATE_IDLE) {
//                settle();
//            }
//        }
//
//        return result;
//    }

    private GestureDetector.OnGestureListener mOnGestureListener = new GestureDetector.OnGestureListener() {

        private float mScrollDistance;

        @Override
        public boolean onDown(MotionEvent e) {
            return mState == STATE_IDLE || mState == STATE_SCROLLING;
        }

        @Override
        public void onShowPress(MotionEvent e) { }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            return false;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            if (mState == STATE_LOADING || mState == STATE_SETTLING) {
                return false;
            }

            boolean scrollingDown = distanceY > 0;

            View child = getChildAt(0);

            if (child.canScrollVertically(scrollingDown ? 1 : -1) && mState != STATE_SCROLLING) {
                return true;
            }

            if (mState == STATE_IDLE) {
                mState = STATE_SCROLLING;

                mLoadingDrawable.setRotating(false);

                mScrollDistance = 0;

                onStateChanged();
            }

            float previousScrollDistance = mScrollDistance;

            mScrollDistance += -distanceY;

            if (previousScrollDistance * mScrollDistance < 0) {
                // We have a direction change in scroll.
                mState = STATE_IDLE;

                mScrollDistance = 0;
                mDisplacementProgress = 0;

                onDisplacementProgressChanged();

                return true;
            }

            float adjustedScroll = (float) Math.min(mLoadAreaHeight, Math.pow(Math.abs(mScrollDistance), SCROLL_REDUCTION_RATE));

            mDisplacementProgress = (adjustedScroll * (mScrollDistance > 0 ? 1 : -1)) / (float) mLoadAreaHeight;

            onDisplacementProgressChanged();

            if (adjustedScroll == mLoadAreaHeight) {
                startLoading();
            }

            return true;
        }

        @Override
        public void onLongPress(MotionEvent e) { }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            return false;
        }

    };

    private void onDisplacementProgressChanged() {
        if (!mLoadingDrawable.isRotating()) {
            mLoadingDrawable.setAngle(getAngleForDisplacementProgress(mDisplacementProgress));
        }

        requestLayout();
        invalidate();
    }

    private float getAngleForDisplacementProgress(float progress) {
        return -LoadingDrawable.DEFAULT_ANGLE + progress * LoadingDrawable.DEFAULT_ANGLE;
    }

    private void settle() {
        mState = STATE_SETTLING;

        ValueAnimator animator = new ValueAnimator();
        animator.setFloatValues(mDisplacementProgress, 0);
        animator.setDuration(SETTLE_DURATION);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mDisplacementProgress = (float) animation.getAnimatedValue();

                onDisplacementProgressChanged();
            }

        });
        animator.addListener(new Animator.AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animation) { }

            @Override
            public void onAnimationEnd(Animator animation) {
                mDisplacementProgress = 0;

                onDisplacementProgressChanged();

                mState = STATE_IDLE;

                onStateChanged();
            }

            @Override
            public void onAnimationCancel(Animator animation) { }

            @Override
            public void onAnimationRepeat(Animator animation) { }

        });
        animator.start();

        onStateChanged();
    }

    public void startLoading() {
        if (mState == STATE_LOADING) {
            return;
        }

        mState = STATE_LOADING;

        if (Math.abs(mDisplacementProgress) != 1) {
            mDisplacementProgress = Math.signum(mDisplacementProgress);

            if (mDisplacementProgress == 0) {
                mDisplacementProgress = 1;
            }

            requestLayout();
            invalidate();
        }

        mLoadingDrawable.setRotating(true);

        onStateChanged();
    }

    public void stopLoading() {
        if (mState == STATE_LOADING) {
            settle();
        }
    }

    private void onStateChanged() {
        if (mOnStateChangedListener != null) {
            mOnStateChangedListener.onStateChanged(mState);
        }
    }

}
