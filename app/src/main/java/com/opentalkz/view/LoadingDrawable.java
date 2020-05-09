package com.opentalkz.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.opentalkz.R;

public class LoadingDrawable extends Drawable {

    public static final float DEFAULT_ANGLE = 45;

    private static final long TURN_DURATION = 2000;
    private static final float TURN_VELOCITY = 360f / TURN_DURATION;

    private static final int INTRINSIC_SIZE = 480;

    private LogoDrawable mLogoDrawable;

    private int mCenterX;
    private int mCenterY;

    private long mLastDrawTime = 0;
    private float mAngle = 0;

    private boolean mRotating = false;

    public LoadingDrawable(Context context) {
        mLogoDrawable = new LogoDrawable(context);

        setColorFilter(ContextCompat.getColor(context, R.color.loadingdrawable_color), PorterDuff.Mode.SRC_IN);
    }

    public LoadingDrawable(Context context, boolean rotating) {
        this(context);

        setRotating(rotating);
    }

    public void setAngle(float angle) {
        mAngle = angle;

        invalidateSelf();
    }

    public void setRotating(boolean rotating) {
        mRotating = rotating;

        if (!mRotating) {
            mLastDrawTime = 0;
        }

        invalidateSelf();
    }

    public boolean isRotating() {
        return mRotating;
    }

    @Override
    public int getIntrinsicWidth() {
        return INTRINSIC_SIZE;
    }

    @Override
    public int getIntrinsicHeight() {
        return INTRINSIC_SIZE;
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);

        int size = Math.min(bounds.width(), bounds.height());

        mCenterX = bounds.centerX();
        mCenterY = bounds.centerY();

        int unit = size / 24;

        mLogoDrawable.setBounds(-unit * 11, -unit * 3, -unit, unit * 7);
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        canvas.translate(mCenterX, mCenterY);

        canvas.save();
        canvas.rotate(DEFAULT_ANGLE + mAngle, 0, 0);

        mLogoDrawable.draw(canvas);

        canvas.save();

        canvas.rotate(90, 0, 0);
        mLogoDrawable.draw(canvas);

        canvas.rotate(90, 0, 0);
        mLogoDrawable.draw(canvas);

        canvas.rotate(90, 0, 0);
        mLogoDrawable.draw(canvas);

        canvas.restore();

        canvas.restore();

        if (!mRotating) {
            return;
        }

        long nanoTime = System.nanoTime();

        if (mLastDrawTime != 0) {
            float angleChange = TURN_VELOCITY * ((nanoTime - mLastDrawTime) / 1_000_000f);

            mAngle = (mAngle + angleChange) % 360;
        }

        mLastDrawTime = nanoTime;

        invalidateSelf();
    }

    @Override
    public void setAlpha(int alpha) {
        mLogoDrawable.setAlpha(alpha);

        invalidateSelf();
    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {
        mLogoDrawable.setColorFilter(colorFilter);

        invalidateSelf();
    }

    @Override
    public int getOpacity() {
        return PixelFormat.OPAQUE;
    }

}
