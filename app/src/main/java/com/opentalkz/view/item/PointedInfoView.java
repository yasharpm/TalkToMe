package com.opentalkz.view.item;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.PointF;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.LayoutRes;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.ViewCompat;

import com.opentalkz.R;

public class PointedInfoView extends ViewGroup {

    private float mPointBase;
    private float mPointHeight;
    private float mEdge;
    private int mSquareBias;
    private int mPointOffset;

    private View mPoint;
    private ViewGroup mContent;

    public PointedInfoView(Context context) {
        super(context);
        initialize(context, null, 0, 0);
    }

    public PointedInfoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context, attrs, 0, 0);
    }

    public PointedInfoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context, attrs, defStyleAttr, 0);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public PointedInfoView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initialize(context, attrs, defStyleAttr, defStyleRes);
    }

    private void initialize(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        setClipChildren(false);
        setClipToPadding(false);

        Resources res = getResources();

        float elevation = res.getDimension(R.dimen.pointedinfoview_elevation);

        mPointBase = res.getDimension(R.dimen.pointedinfoview_pointbase);
        mPointHeight = res.getDimension(R.dimen.pointedinfoview_pointheight);

        mEdge = PointF.length(mPointBase, mPointHeight);

        float targetDiameter2 = 2 * mEdge * (mEdge - mPointHeight);
        float diameter2 = 2 * mEdge * (mEdge + mPointHeight);
        int squareEdge = (int) Math.sqrt(diameter2 / 2);

        float scale = (float) Math.sqrt(targetDiameter2 / diameter2);
        float rotation = (float) Math.toDegrees(Math.atan((mEdge - mPointHeight) / mPointBase));

        mSquareBias = (int) (0.5f * (Math.sqrt(diameter2) - Math.sqrt(diameter2 / 2)));

        mPointOffset = (int) (res.getDimension(R.dimen.pointedinfoview_pointoffset) - mPointBase);

        View square = new View(context);
        ViewCompat.setElevation(square, elevation);
        square.setBackgroundColor(ResourcesCompat.getColor(res, R.color.pointedinfoview_background, null));
        square.setRotation(45);
        square.setLayoutParams(new LayoutParams(squareEdge, squareEdge));

        FrameLayout scaleLayer = new FrameLayout(context);
        scaleLayer.setClipChildren(false);
        scaleLayer.setClipToPadding(false);
        scaleLayer.setScaleX(scale);
        scaleLayer.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        scaleLayer.addView(square);

        FrameLayout rotationLayer = new FrameLayout(context);
        ViewCompat.setElevation(rotationLayer, elevation);
        rotationLayer.setClipChildren(false);
        rotationLayer.setClipToPadding(false);
        rotationLayer.setRotation(rotation);
        rotationLayer.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        rotationLayer.addView(scaleLayer);

        mPoint = rotationLayer;
        addView(mPoint);

        mContent = new FrameLayout(context);
        ViewCompat.setElevation(mContent, elevation);
        mContent.setBackground(AppCompatResources.getDrawable(context, R.drawable.item_background));
        addView(mContent);
    }

    public void setContentView(View view) {
        mContent.removeAllViews();
        mContent.addView(view);
    }

    public void setContentView(@LayoutRes int resId) {
        setContentView(LayoutInflater.from(getContext()).inflate(resId, mContent, false));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mContent.measure(
                widthMeasureSpec,
                MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
        );

        mPoint.measure(
                MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
                MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
        );

        int height = (int) (mContent.getMeasuredHeight() + mPoint.getMeasuredHeight() - mPointHeight + mSquareBias);

        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        mContent.layout(0, 0, mContent.getMeasuredWidth(), mContent.getMeasuredHeight());

        int pointTop = (int) (mContent.getMeasuredHeight() - mPointHeight);

        mPoint.layout(mPointOffset, pointTop, mPointOffset + mPoint.getMeasuredWidth(), pointTop + mPoint.getMeasuredHeight());
    }

}
