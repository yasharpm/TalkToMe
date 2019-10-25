package com.yashoid.talktome.model.post;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;

import com.yashoid.mmv.Managers;
import com.yashoid.mmv.Model;
import com.yashoid.mmv.ModelFeatures;
import com.yashoid.mmv.PersistentTarget;
import com.yashoid.mmv.Target;
import com.yashoid.network.RequestResponse;
import com.yashoid.network.RequestResponseCallback;
import com.yashoid.sequencelayout.Sequence;
import com.yashoid.sequencelayout.SequenceLayout;
import com.yashoid.sequencelayout.SequenceReader;
import com.yashoid.talktome.R;
import com.yashoid.talktome.TTMOffice;
import com.yashoid.talktome.model.comment.CommentList;
import com.yashoid.talktome.network.ReportResponse;
import com.yashoid.talktome.network.Requests;
import com.yashoid.talktome.util.TimeUtil;
import com.yashoid.talktome.view.popup.Popup;
import com.yashoid.talktome.view.popup.PopupItem;

import java.util.List;

public class PostFullItemView extends SequenceLayout implements Target, Post {

    private static final PopupItem REPORT = new PopupItem(R.string.postfullitem_report, R.drawable.ic_report);

    private static final PopupItem[] MORE_ITEMS = { REPORT };

    private PostContentView mContent;
    private TextView mTextLikes;
    private TextView mTextComments;
    private TextView mTextViews;
    private TextView mTextTime;
    private View mButtonMore;

    private Model mModel;

    public PostFullItemView(Context context) {
        super(context);
        initialize(context, null, 0);
    }

    public PostFullItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context, attrs, 0);
    }

    public PostFullItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context, attrs, defStyleAttr);
    }

    private void initialize(Context context, AttributeSet attrs, int defStyleAttr) {
        setBackground(ContextCompat.getDrawable(context, R.drawable.item_background));

        ViewCompat.setElevation(this, getResources().getDimension(R.dimen.postfullitem_elevation));

        LayoutInflater.from(context).inflate(R.layout.postfullitem, this, true);

        try {
            XmlResourceParser parser = getResources().getXml(R.xml.sequences_postfullitem);
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
        mButtonMore = findViewById(R.id.button_more);

        mButtonMore.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Resources res = getResources();

                int x = res.getDimensionPixelSize(R.dimen.more_popup_x);
                int y = res.getDimensionPixelSize(R.dimen.more_popup_y);

                new Popup(v, MORE_ITEMS, mOnMoreItemSelectedListener).showAtLocation(x, y);
            }

        });
    }

    private Popup.OnItemSelectedListener mOnMoreItemSelectedListener = new Popup.OnItemSelectedListener() {

        @Override
        public void onItemClicked(int position, PopupItem item) {
            if (item == REPORT) {
                Toast.makeText(getContext(), R.string.report_reporting, Toast.LENGTH_SHORT).show();

                String postId = mModel.get(ID);

                TTMOffice.runner(getContext()).runUserAction(Requests.report(postId, -1, null), new RequestResponseCallback<ReportResponse>() {

                    @Override
                    public void onRequestResponse(RequestResponse<ReportResponse> response) {
                        int message = response.isSuccessful() ? R.string.report_reported : R.string.report_notreported;

                        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
                    }

                });
            }
        }

    };

    protected TextView getContent() {
        return mContent;
    }

    public void setMaxLines(int maxLines) {
        mContent.setMaxLines(maxLines);
    }

    public void setPost(ModelFeatures postFeatures) {
        if (mModel != null) {
            Managers.unregisterTarget(mContent);
            Managers.unregisterTarget(this);

            Managers.unregisterTarget(mCommentCountTarget);

            mModel = null;
        }

        Managers.registerTarget(mContent, postFeatures);
        Managers.registerTarget(this, postFeatures);

        ModelFeatures commentListFeatures = new ModelFeatures.Builder()
                .add(TYPE, CommentList.TYPE_COMMENT_LIST)
                .add(CommentList.POST_ID, postFeatures.get(ID))
                .build();
        Managers.registerTarget(mCommentCountTarget, commentListFeatures);
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
        Integer views = mModel.get(VIEWS);
        mTextViews.setText(views == null ? "" : String.valueOf(views));

        mTextLikes.setText("0");

        Long createdTime = mModel.get(CREATED_TIME);
        mTextTime.setText(createdTime == null ? "" : TimeUtil.getRelativeTime((long) mModel.get(CREATED_TIME), getContext()));
    }

    private Target mCommentCountTarget = new PersistentTarget() {

        private Model mCommentList;

        @Override
        public void setModel(Model model) {
            mCommentList = model;

            onModelChanged();
        }

        @Override
        public void onFeaturesChanged(String... featureNames) {
            onModelChanged();
        }

        private void onModelChanged() {
            int state = mCommentList.get(CommentList.STATE);

            if (state != CommentList.STATE_SUCCESS) {
                mTextComments.setText("");
            }
            else {
                List<ModelFeatures> comments = mCommentList.get(CommentList.MODEL_LIST);

                mTextComments.setText(String.valueOf(comments.size()));
            }
        }

    };

}
