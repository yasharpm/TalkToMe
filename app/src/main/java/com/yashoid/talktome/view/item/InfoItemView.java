package com.yashoid.talktome.view.item;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.StringRes;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.ViewCompat;

import com.yashoid.talktome.R;
import com.yashoid.talktome.view.SimpleButton;

public class InfoItemView extends ViewGroup {

    public static View infoView(Context context, @StringRes int resId) {
        FrameLayout container = new FrameLayout(context);

        InfoItemView view = new InfoItemView(context);
        view.setIndicatorColor(ResourcesCompat.getColor(context.getResources(), R.color.infoitem_infoindicatorcolor, null));
        view.setText(resId);

        container.addView(view, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

        return container;
    }

    public static View errorView(Context context, @StringRes int resId, OnClickListener onRetryClickListener) {
        LinearLayout container = new LinearLayout(context);
        container.setOrientation(LinearLayout.VERTICAL);

        InfoItemView view = new InfoItemView(context);
        view.setIndicatorColor(ResourcesCompat.getColor(context.getResources(), R.color.infoitem_infoindicatorcolor, null));
        view.setText(resId);

        container.addView(view, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER_HORIZONTAL;
        params.topMargin = context.getResources().getDimensionPixelSize(R.dimen.infoitem_button_margin);

        SimpleButton button = new SimpleButton(context);
        button.setText(R.string.infoitem_retry);
        button.setIconResource(R.drawable.ic_retry);
        button.setOnClickListener(onRetryClickListener);
        container.addView(button, params);

        return container;
    }

    private ItemContentView mContent;
    private ImageView mArrow;

    public InfoItemView(Context context) {
        super(context);
        initialize(context, null, 0, 0);
    }

    public InfoItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context, attrs, 0, 0);
    }

    public InfoItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context, attrs, defStyleAttr, 0);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public InfoItemView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initialize(context, attrs, defStyleAttr, defStyleRes);
    }

    private void initialize(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        mContent = new ItemContentView(context);
        mArrow = new ImageView(context);

        addView(mContent);
        addView(mArrow);

        mContent.setIndicator(AppCompatResources.getDrawable(context, R.drawable.ic_indicator_post));
        mContent.setBackgroundResource(R.drawable.item_background);
        mContent.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.infoitem_textsize));

        mArrow.setBackgroundResource(R.drawable.infoitem_arrow_background);
        mArrow.setImageResource(R.drawable.ic_arrow_bottom);

        Resources res = getResources();

        ViewCompat.setElevation(mArrow, res.getDimension(R.dimen.item_elevation));
        ViewCompat.setElevation(mContent, res.getDimension(R.dimen.item_elevation));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mContent.measure(widthMeasureSpec, heightMeasureSpec);
        mArrow.measure(
                MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
                MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
        );

        int height = mContent.getMeasuredHeight() + mArrow.getDrawable().getIntrinsicHeight() + mArrow.getPaddingBottom() - (int) ViewCompat.getElevation(mContent);

        setMeasuredDimension(
                mContent.getMeasuredWidthAndState(),
                MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY)
        );
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        mContent.layout(0, 0, mContent.getMeasuredWidth(), mContent.getMeasuredHeight());

        int arrowSize = mArrow.getMeasuredWidth();
        int arrowLeft = (getWidth() - arrowSize) / 2;
//        int arrowTop = mContent.getMeasuredHeight() - arrowSize / 2 - (int) ViewCompat.getElevation(mContent);
        int arrowTop = getHeight() - arrowSize - 2 * (int) ViewCompat.getElevation(mContent);
        mArrow.layout(arrowLeft, arrowTop, arrowLeft + arrowSize, arrowTop + arrowSize);
    }

    public void setIndicatorColor(int color) {
        mContent.setIndicatorColor(color);
    }

    public void setText(CharSequence text) {
        mContent.setText(text);
    }

    public void setText(@StringRes int resId) {
        mContent.setText(resId);
    }

}
