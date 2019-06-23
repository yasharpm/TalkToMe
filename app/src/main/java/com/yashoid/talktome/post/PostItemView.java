package com.yashoid.talktome.post;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Paint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;

import com.yashoid.mmv.Model;
import com.yashoid.talktome.R;
import com.yashoid.talktome.view.Fonts;
import com.yashoid.talktome.view.ViewBunch;

public class PostItemView extends AppCompatTextView implements ViewBunch.ViewBunchItem, Post {

    private Model mModel;

    private int mMaxLines;
    private int mLines = -1;

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

    private void initialize(Context context, AttributeSet attrs, int defStyleAttr) {
        Resources res = getResources();

        setBackgroundResource(R.drawable.postitem_background);
        ViewCompat.setElevation(this, res.getDimension(R.dimen.postitem_elevation));

        setTextSize(TypedValue.COMPLEX_UNIT_PX, res.getDimension(R.dimen.postitem_textsize));
        setLineHeight(res.getDimensionPixelSize(R.dimen.postitem_lineheight));
        setTextColor(ContextCompat.getColor(context, R.color.postitem_textcolor));
        setTypeface(Fonts.get(Fonts.DEFAULT_REGULAR, context));

        setEllipsize(TextUtils.TruncateAt.END);
    }

    @Override
    public void setModel(Model model) {
        mModel = model;

        onModelChanged();
    }

    @Override
    public Model getModel() {
        return mModel;
    }

    @Override
    public void onFeaturesChanged(String... featureNames) {
        onModelChanged();
    }

    private void onModelChanged() {
        setText((String) mModel.get(CONTENT));
    }

    @Override
    public int measureHeight(int width) {
        int widthSpec = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY);
        int heightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);

        measure(widthSpec, heightSpec);

        measureMaxLines();

        return getMeasuredHeight();
    }

    private void measureMaxLines() {
        if (mLines != -1) {
            mMaxLines = mLines;
            return;
        }

        mMaxLines = getLayout().getLineCount();
    }

    @Override
    public int getLines() {
        return mLines == -1 ? mMaxLines : mLines;
    }

    @Override
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
