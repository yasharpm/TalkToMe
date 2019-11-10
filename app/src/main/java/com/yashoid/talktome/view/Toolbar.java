package com.yashoid.talktome.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;

import com.yashoid.sequencelayout.SequenceLayout;
import com.yashoid.talktome.R;

public class Toolbar extends SequenceLayout {

    private TextView mTextTitle;
    private View mImageNotifier;
    private TextView mTextNotifier;
    private View mImageAppName;
    private ImageView mButtonAction;

    public Toolbar(Context context) {
        super(context);
        initialize(context, null, 0);
    }

    public Toolbar(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context, attrs, 0);
    }

    public Toolbar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context, attrs, defStyleAttr);
    }

    private void initialize(Context context, AttributeSet attrs, int defStyleAttr) {
        setBackgroundColor(ContextCompat.getColor(context, R.color.toolbar_background));
        ViewCompat.setElevation(this, getResources().getDimension(R.dimen.toolbar_elevation));

        LayoutInflater.from(context).inflate(R.layout.toolbar, this, true);

        addSequences(R.xml.sequences_toolbar);

        mButtonAction = findViewById(R.id.button_action);
        mImageNotifier = findViewById(R.id.image_notifier);
        mTextNotifier = findViewById(R.id.text_notifier);
        mTextTitle = findViewById(R.id.text_title);
        mImageAppName = findViewById(R.id.image_appname);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.Toolbar, defStyleAttr, 0);

        int actionIconResId = a.getResourceId(R.styleable.Toolbar_actionIcon, 0);
        String title = a.getString(R.styleable.Toolbar_title);

        a.recycle();

        if (actionIconResId != 0) {
            mButtonAction.setImageDrawable(ContextCompat.getDrawable(context, actionIconResId));
        }

        setTitle(title);
        setNotifierCount(0);
    }

    public void setNotifierCount(int count) {
        if (count <= 0) {
            mImageNotifier.setVisibility(INVISIBLE);
            mTextNotifier.setVisibility(INVISIBLE);
            return;
        }

        mImageNotifier.setVisibility(VISIBLE);
        mTextNotifier.setVisibility(VISIBLE);

        mTextNotifier.setText("+" + count);
    }

    public void setTitle(CharSequence title) {
        if (TextUtils.isEmpty(title)) {
            mTextTitle.setVisibility(INVISIBLE);
            mImageAppName.setVisibility(VISIBLE);
        }
        else {
            mTextTitle.setVisibility(VISIBLE);
            mImageAppName.setVisibility(INVISIBLE);

            mTextTitle.setText(title);
        }
    }

    public void setActionButtonClickListener(OnClickListener onClickListener) {
        mButtonAction.setOnClickListener(onClickListener);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int height = getResources().getDimensionPixelSize(R.dimen.toolbar_height);
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

}
