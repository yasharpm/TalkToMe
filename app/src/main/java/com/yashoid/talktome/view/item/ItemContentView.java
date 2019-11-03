package com.yashoid.talktome.view.item;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.text.Layout;
import android.text.TextUtils;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.yashoid.talktome.R;

public class ItemContentView extends AppCompatTextView {

    private int mMaxLines;
    private int mLines = -1;

    private Drawable mIndicator;
    private int mIndicatorWidth;
    private int mIndicatorMargin;

    private boolean mPaddingAdjusted = false;

    public ItemContentView(Context context) {
        super(context);
        initialize(context, null, 0);
    }

    public ItemContentView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context, attrs, 0);
    }

    public ItemContentView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context, attrs, defStyleAttr);
    }

    private void initialize(final Context context, AttributeSet attrs, int defStyleAttr) {
        Resources res = getResources();

        setLineHeight(res.getDimensionPixelSize(R.dimen.itemcontent_lineheight));
        setTextColor(ContextCompat.getColor(context, R.color.itemcontent_textcolor));
        setTypeface(ResourcesCompat.getFont(context, R.font.dana_regular));
        setHighlightColor(ContextCompat.getColor(context, R.color.itemcontent_highlightcolor));

        setEllipsize(TextUtils.TruncateAt.END);

        mIndicatorWidth = res.getDimensionPixelSize(R.dimen.itemcontent_indicator_width);
        mIndicatorMargin = res.getDimensionPixelSize(R.dimen.itemcontent_indicator_margin);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ItemContentView, defStyleAttr, 0);

        int indicatorResId = a.getResourceId(R.styleable.ItemContentView_indicator, 0);
        int indicatorTint = a.getColor(R.styleable.ItemContentView_indicatorTint, 0);

        a.recycle();

        if (indicatorResId != 0) {
            Drawable indicator = ResourcesCompat.getDrawable(res, indicatorResId, null);

            if (indicator != null) {
                if (indicatorTint != 0) {
                    indicator = indicator.mutate();
                    indicator.setColorFilter(indicatorTint, PorterDuff.Mode.SRC_IN);
                }

                setIndicator(indicator);
            }
        }
    }

    public void setIndicator(Drawable indicator) {
        mIndicator = indicator;

        measureIndicator();

        invalidate();
    }

    public void setIndicatorColor(int color) {
        mIndicator.setColorFilter(color, PorterDuff.Mode.SRC_IN);

        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (!mPaddingAdjusted) {
            setPadding(getPaddingLeft(), getPaddingTop(), getPaddingRight() + mIndicatorWidth + mIndicatorMargin, getPaddingBottom());

            mPaddingAdjusted = true;
        }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        measureMaxLines();
    }

    @Override
    public void layout(int l, int t, int r, int b) {
        super.layout(l, t, r, b);

        measureIndicator();
    }

    private void measureIndicator() {
        if (mIndicator == null) {
            return;
        }

        Layout layout = getLayout();

        if (layout == null) {
            return;
        }

        int baseline = (layout.getLineTop(0) + layout.getLineBottom(0)) / 2;
        int cy = baseline + getPaddingTop();

        int left = getWidth() - getPaddingRight() + mIndicatorMargin;

        int indicatorHeight = mIndicatorWidth * mIndicator.getIntrinsicHeight() / mIndicator.getIntrinsicWidth();

        mIndicator.setBounds(
                left,
                cy - indicatorHeight / 2,
                left + mIndicatorWidth,
                cy + indicatorHeight / 2);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mIndicator != null) {
            mIndicator.draw(canvas);
        }
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
