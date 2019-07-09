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

import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.yashoid.mmv.Model;
import com.yashoid.mmv.Target;
import com.yashoid.talktome.R;

public class PostContentView extends AppCompatTextView implements Post, Target {

    private Model mModel;

    private int mMaxLines;
    private int mLines = -1;

    private Drawable mIndicator;
    private int mIndicatorSize;
    private int mIndicatorMargin;

    private boolean mPaddingAdjusted = false;

    public PostContentView(Context context) {
        super(context);
        initialize(context, null, 0);
    }

    public PostContentView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context, attrs, 0);
    }

    public PostContentView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context, attrs, defStyleAttr);
    }

    private void initialize(final Context context, AttributeSet attrs, int defStyleAttr) {
        Resources res = getResources();

        setTextSize(TypedValue.COMPLEX_UNIT_PX, res.getDimension(R.dimen.postcontent_textsize));
        setLineHeight(res.getDimensionPixelSize(R.dimen.postcontent_lineheight));
        setTextColor(ContextCompat.getColor(context, R.color.postcontent_textcolor));
        setTypeface(ResourcesCompat.getFont(context, R.font.dana_regular));

        setEllipsize(TextUtils.TruncateAt.END);

        mIndicator = ContextCompat.getDrawable(context, R.drawable.ic_indicator_post).mutate();
        mIndicatorSize = res.getDimensionPixelSize(R.dimen.postcontent_indicator_size);
        mIndicatorMargin = res.getDimensionPixelSize(R.dimen.postcontent_indicator_margin);
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
        if (!mPaddingAdjusted) {
            setPadding(getPaddingLeft(), getPaddingTop(), getPaddingRight() + mIndicatorSize + mIndicatorMargin, getPaddingBottom());

            mPaddingAdjusted = true;
        }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        measureMaxLines();
    }

    @Override
    public void layout(int l, int t, int r, int b) {
        super.layout(l, t, r, b);

        Layout layout = getLayout();

        int baseline = (layout.getLineTop(0) + layout.getLineBottom(0)) / 2;
        int cy = baseline + getPaddingTop();

        int left = getWidth() - getPaddingRight() + mIndicatorMargin;

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
