package com.opentalkz.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.ViewGroup;

import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.res.ResourcesCompat;

import com.opentalkz.R;

public class SimpleButton extends ViewGroup {

    private static final float TEXT_HORIZONTAL_RATIO = 0.33f;

    private AppCompatTextView mText;
    private AppCompatImageView mIcon;

    private int mIconMarginLeft;

    public SimpleButton(Context context) {
        super(context);
        initialize(context, null, 0, 0);
    }

    public SimpleButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context, attrs, 0, 0);
    }

    public SimpleButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context, attrs, defStyleAttr, 0);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public SimpleButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initialize(context, attrs, defStyleAttr, defStyleRes);
    }

    private void initialize(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        setBackgroundResource(R.drawable.simplebutton_background);

        Resources res = getResources();

        mText = new AppCompatTextView(context);
        mText.setTextSize(TypedValue.COMPLEX_UNIT_PX, res.getDimension(R.dimen.simplebutton_textsize));
        mText.setTextColor(ResourcesCompat.getColor(res, R.color.simplebutton_textcolor, null));
        mText.setTypeface(ResourcesCompat.getFont(context, R.font.dana_regular));
        addView(mText);

        mIcon = new AppCompatImageView(context);
        addView(mIcon);

        mIconMarginLeft = res.getDimensionPixelSize(R.dimen.simplebutton_padding_left);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SimpleButton, defStyleAttr, defStyleRes);

        mText.setText(a.getText(R.styleable.SimpleButton_text));

        int iconResId = a.getResourceId(R.styleable.SimpleButton_icon, 0);

        if (iconResId != 0) {
            mIcon.setImageDrawable(AppCompatResources.getDrawable(context, iconResId));
        }

        a.recycle();
    }

    public void setText(CharSequence text) {
        mText.setText(text);
    }

    public void setText(@StringRes int resId) {
        mText.setText(resId);
    }

    public void setIconDrawable(Drawable drawable) {
        mIcon.setImageDrawable(drawable);
    }

    public void setIconResource(@DrawableRes int resId) {
        mIcon.setImageResource(resId);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        Resources res = getResources();

        if (widthMode == MeasureSpec.AT_MOST) {
            width = Math.min(width, res.getDimensionPixelSize(R.dimen.simplebutton_width));
        }
        else if (widthMode == MeasureSpec.UNSPECIFIED) {
            width = res.getDimensionPixelSize(R.dimen.simplebutton_width);
        }

        if (heightMode == MeasureSpec.AT_MOST) {
            height = Math.min(height, res.getDimensionPixelSize(R.dimen.simplebutton_height));
        }
        else if (heightMode == MeasureSpec.UNSPECIFIED) {
            height = res.getDimensionPixelSize(R.dimen.simplebutton_height);
        }

        mText.measure(
                MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
                MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
        );

        int iconSize = res.getDimensionPixelSize(R.dimen.simplebutton_icon_size);

        mIcon.measure(
                MeasureSpec.makeMeasureSpec(iconSize, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(iconSize, MeasureSpec.EXACTLY)
        );

        setMeasuredDimension(width, height);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int width = getWidth();
        int height = getHeight();

        int iconSize = mIcon.getMeasuredWidth();
        int iconTop = (height - iconSize) / 2;

        int textWidth = mText.getMeasuredWidth();
        int textHeight = mText.getMeasuredHeight();
        int textTop = (height - textHeight) / 2;
        int textLeft = (int) (mIconMarginLeft + iconSize + (width - mIconMarginLeft - iconSize - textWidth) * TEXT_HORIZONTAL_RATIO);

        mIcon.layout(mIconMarginLeft, iconTop, mIconMarginLeft + iconSize, iconTop + iconSize);
        mText.layout(textLeft, textTop, textLeft + textWidth, textTop + textHeight);
    }

}
