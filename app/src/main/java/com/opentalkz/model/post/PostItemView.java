package com.opentalkz.model.post;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;

import androidx.core.view.ViewCompat;

import com.opentalkz.R;

public class PostItemView extends PostContentView {

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

        setBackgroundResource(R.drawable.item_background);
        ViewCompat.setElevation(this, res.getDimension(R.dimen.item_elevation));
    }

}
