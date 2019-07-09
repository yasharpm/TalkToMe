package com.yashoid.talktome.post.postdetails;

import android.content.Context;
import android.content.res.XmlResourceParser;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;

import com.yashoid.mmv.Managers;
import com.yashoid.mmv.Model;
import com.yashoid.mmv.ModelFeatures;
import com.yashoid.mmv.Target;
import com.yashoid.sequencelayout.Sequence;
import com.yashoid.sequencelayout.SequenceLayout;
import com.yashoid.sequencelayout.SequenceReader;
import com.yashoid.talktome.R;
import com.yashoid.talktome.post.Post;
import com.yashoid.talktome.post.PostContentView;
import com.yashoid.talktome.util.TimeUtil;

import java.util.List;

public class DetailedPostContentView extends SequenceLayout implements Target, Post {

    private PostContentView mContent;
    private TextView mTextLikes;
    private TextView mTextComments;
    private TextView mTextViews;
    private TextView mTextTime;

    private Model mModel;

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
        setBackground(ContextCompat.getDrawable(context, R.drawable.detailedpostcontent_background));

        ViewCompat.setElevation(this, getResources().getDimension(R.dimen.detailedpostcontent_elevation));

        LayoutInflater.from(context).inflate(R.layout.detailedpostcontent, this, true);

        try {
            XmlResourceParser parser = getResources().getXml(R.xml.sequences_detailedpostcontent);
            List<Sequence> sequences = new SequenceReader(context).readSequences(parser);

            for (Sequence sequence: sequences) {
                addSequence(sequence);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        mContent = findViewById(R.id.content);
        mTextComments = findViewById(R.id.text_comments);
        mTextLikes = findViewById(R.id.text_likes);
        mTextViews = findViewById(R.id.text_views);
        mTextTime = findViewById(R.id.text_time);

        mContent.setTextIsSelectable(true); // TODO
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
    }

    public void setPost(ModelFeatures postFeatures) {
        if (mModel != null) {
            Managers.unregisterTarget(mContent);
            Managers.unregisterTarget(this);

            mModel = null;
        }

        Managers.registerTarget(mContent, postFeatures);
        Managers.registerTarget(this, postFeatures);
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
        mTextViews.setText(String.valueOf((int) mModel.get(VIEWS)));
        mTextLikes.setText("0");
        mTextComments.setText("?"); // TODO
        mTextTime.setText(TimeUtil.getRelativeTime((long) mModel.get(CREATED_TIME), getContext()));
    }

}
