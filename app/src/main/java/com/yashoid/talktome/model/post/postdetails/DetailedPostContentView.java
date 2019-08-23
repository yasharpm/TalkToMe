package com.yashoid.talktome.model.post.postdetails;

import android.content.Context;
import android.util.AttributeSet;

import androidx.core.content.ContextCompat;

import com.yashoid.talktome.R;
import com.yashoid.talktome.model.post.PostFullItemView;

public class DetailedPostContentView extends PostFullItemView {

    public DetailedPostContentView(Context context) {
        super(context);
        initialize(context, null, 0);
    }

    public DetailedPostContentView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context, attrs, 0);
    }

    public DetailedPostContentView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context, attrs, defStyleAttr);
    }

    private void initialize(Context context, AttributeSet attrs, int defStyleAttr) {
        setBackground(ContextCompat.getDrawable(context, R.drawable.item_attachedtotop_background));

        getContent().setTextIsSelectable(true);
    }

}
