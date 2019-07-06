package com.yashoid.talktome.post;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.text.Layout;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;

import com.yashoid.mmv.Model;
import com.yashoid.mmv.Target;
import com.yashoid.talktome.R;
import com.yashoid.talktome.view.Fonts;

public class PostItemView extends AppCompatTextView implements Post, Target {

    private Model mModel;

    private int mMaxLines;
    private int mLines = -1;

    private Drawable mIndicator;
    private int mIndicatorSize;
    private int mIndicatorMargin;

    public PostItemView(Context context) {
        super(context);
        initialize(context, null, 0);
    }

    public PostItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context, attrs, 0);
    }

    public PostItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context, attrs, defStyleAttr);
    }

    private void initialize(final Context context, AttributeSet attrs, int defStyleAttr) {
        Resources res = getResources();

        setBackgroundResource(R.drawable.postitem_background);
        ViewCompat.setElevation(this, res.getDimension(R.dimen.postitem_elevation));

        setGravity(Gravity.TOP);
        setTextSize(TypedValue.COMPLEX_UNIT_PX, res.getDimension(R.dimen.postitem_textsize));
        setLineHeight(res.getDimensionPixelSize(R.dimen.postitem_lineheight));
        setTextColor(ContextCompat.getColor(context, R.color.postitem_textcolor));
        setTypeface(Fonts.get(Fonts.DEFAULT_REGULAR, context));

        setEllipsize(TextUtils.TruncateAt.END);

        mIndicator = ContextCompat.getDrawable(context, R.drawable.ic_indicator_post).mutate();
        mIndicatorSize = res.getDimensionPixelSize(R.dimen.postitem_indicator_size);
        mIndicatorMargin = res.getDimensionPixelSize(R.dimen.postitem_indicator_margin);
    }

    @Override
    public void setModel(Model model) {
        mModel = model;

        onModelChanged();
    }

    @Override
    public void onFeaturesChanged(String... featureNames) {
        onModelChanged();
    }

    private void onModelChanged() {
        setText((String) mModel.get(CONTENT));

        mIndicator.setColorFilter((int) mModel.get(INDICATOR_COLOR), PorterDuff.Mode.SRC_IN);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);

        widthMeasureSpec = MeasureSpec.makeMeasureSpec(widthSize - mIndicatorSize - mIndicatorMargin, MeasureSpec.EXACTLY);

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        measureMaxLines();
    }

    @Override
    public void layout(int l, int t, int r, int b) {
        super.layout(l, t, r, b);

        Layout layout = getLayout();

        int baseline = (layout.getLineTop(0) + layout.getLineBottom(0)) / 2;
        int cy = baseline + getPaddingTop();

        int left = getWidth() - getPaddingRight() - mIndicatorSize;

        mIndicator.setBounds(left, cy - mIndicatorSize / 2, left + mIndicatorSize, cy + mIndicatorSize / 2);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        mIndicator.draw(canvas);
    }

    private void measureMaxLines() {
        if (mLines != -1) {
            mMaxLines = mLines;
            return;
        }

        mMaxLines = getLayout().getLineCount();
    }

    public int getLines() {
        return mLines == -1 ? mMaxLines : mLines;
    }

    public void setMaximumAllowedLines(int maxLines) {
        mLines = maxLines;

        if (mLines >= 0) {
            setMaxLines(maxLines);
        }
        else {
            setMaxLines(200);
        }
    }

}
