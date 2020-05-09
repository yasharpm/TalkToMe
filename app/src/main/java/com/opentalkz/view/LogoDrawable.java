package com.opentalkz.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.opentalkz.R;

public class LogoDrawable extends Drawable {

    private static final int INTRINSIC_SIZE = 480;

    private Paint mPaint;

    private RectF mHeadRect = new RectF();
    private RectF mBaseRect = new RectF();
    private Path mCurvePath = new Path();

    private RectF mHelperRect = new RectF();

    public LogoDrawable(Context context) {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);

        mPaint.setColor(ContextCompat.getColor(context, R.color.themeColor));
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

        float unit = Math.min(bounds.width(), bounds.height()) / 10f;

        mHeadRect.set(bounds.left, bounds.top, bounds.left + unit * 10, bounds.top + unit * 2);
        mBaseRect.set(bounds.left + unit * 3, bounds.top + unit * 5, bounds.left + unit * 5, bounds.top + unit * 10);

        float x = bounds.left + unit * 3;
        float y = bounds.top + unit * 5;

        mCurvePath.reset();
        mCurvePath.moveTo(x, y);

        mHelperRect.set(x, y - unit * 5, x + unit * 10, y + unit * 5);
        mCurvePath.arcTo(mHelperRect, 180, 90);

        x += unit * 5;
        y -= unit * 3;

        mCurvePath.lineTo(x, y);

        mHelperRect.set(x - unit * 3, y, x + unit * 3, y + unit * 6);
        mCurvePath.arcTo(mHelperRect, -90, -90);

        mCurvePath.close();
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        canvas.drawRect(mHeadRect, mPaint);
        canvas.drawRect(mBaseRect, mPaint);
        canvas.drawPath(mCurvePath, mPaint);
    }

    @Override
    public void setAlpha(int alpha) {
        mPaint.setAlpha(alpha);

        invalidateSelf();
    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {
        mPaint.setColorFilter(colorFilter);

        invalidateSelf();
    }

    @Override
    public int getOpacity() {
        return PixelFormat.OPAQUE;
    }

}
