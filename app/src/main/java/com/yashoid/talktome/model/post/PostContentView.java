package com.yashoid.talktome.model.post;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.util.TypedValue;

import androidx.core.content.ContextCompat;

import com.yashoid.mmv.Model;
import com.yashoid.mmv.Target;
import com.yashoid.talktome.R;
import com.yashoid.talktome.view.item.ItemContentView;

public class PostContentView extends ItemContentView implements Post, Target {

    private Model mModel;

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

        setIndicator(ContextCompat.getDrawable(context, R.drawable.ic_indicator_post).mutate());
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

        setIndicatorColor((int) mModel.get(INDICATOR_COLOR));
    }

}
