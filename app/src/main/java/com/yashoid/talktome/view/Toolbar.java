package com.yashoid.talktome.view;

import android.content.Context;
import android.content.res.XmlResourceParser;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;

import com.yashoid.sequencelayout.Sequence;
import com.yashoid.sequencelayout.SequenceLayout;
import com.yashoid.sequencelayout.SequenceReader;
import com.yashoid.talktome.R;

import java.util.List;

public class Toolbar extends SequenceLayout {

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

        try {
            XmlResourceParser parser = getResources().getXml(R.xml.sequences_toolbar);
            List<Sequence> sequences = new SequenceReader(context).readSequences(parser);

            for (Sequence sequence: sequences) {
                addSequence(sequence);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int height = getResources().getDimensionPixelSize(R.dimen.toolbar_height);
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

}
