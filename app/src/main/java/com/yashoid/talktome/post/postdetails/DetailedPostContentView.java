package com.yashoid.talktome.post.postdetails;

import android.content.Context;
import android.content.res.XmlResourceParser;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;

import com.yashoid.mmv.Managers;
import com.yashoid.mmv.Model;
import com.yashoid.mmv.ModelFeatures;
import com.yashoid.sequencelayout.Sequence;
import com.yashoid.sequencelayout.SequenceLayout;
import com.yashoid.sequencelayout.SequenceReader;
import com.yashoid.talktome.R;
import com.yashoid.talktome.post.PostContentView;

import java.util.List;

public class DetailedPostContentView extends SequenceLayout {

    private PostContentView mContent;

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
    }

    public void setPost(ModelFeatures postFeatures) {
        Managers.registerTarget(mContent, postFeatures);
    }

//    @Override
//    public void setModel(Model model) {
//        mModel = model;
//
//        onModelChanged();
//    }
//
//    @Override
//    public void onFeaturesChanged(String... featureNames) {
//        onModelChanged();
//    }
//
//    private void onModelChanged() {
//        mContent.setModel(mModel);
//    }

}
