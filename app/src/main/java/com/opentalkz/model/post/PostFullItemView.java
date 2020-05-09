package com.opentalkz.model.post;

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
import com.yashoid.mmv.Target;
import com.yashoid.network.RequestResponse;
import com.yashoid.network.RequestResponseCallback;
import com.yashoid.sequencelayout.Sequence;
import com.yashoid.sequencelayout.SequenceLayout;
import com.yashoid.sequencelayout.SequenceReader;
import com.opentalkz.R;
import com.opentalkz.Share;
import com.opentalkz.TTMOffice;
import com.opentalkz.network.ReportResponse;
import com.opentalkz.network.Requests;
import com.opentalkz.util.ReportAComment;
import com.opentalkz.util.TimeUtil;
import com.opentalkz.view.popup.Popup;
import com.opentalkz.view.popup.PopupItem;

import java.util.List;

public class PostFullItemView extends SequenceLayout implements Target, Post {

    private static final PopupItem REPORT = new PopupItem(R.string.postfullitem_report, R.drawable.ic_report);
    private static final PopupItem REPORT_A_COMMENT = new PopupItem(R.string.postfullitem_reportacomment, R.drawable.ic_report);

    private static final PopupItem[] MORE_ITEMS = { REPORT, REPORT_A_COMMENT };

    private PostContentView mContent;
    private TextView mTextLikes;
    private TextView mTextComments;
    private TextView mTextViews;
    private TextView mTextTime;
    private View mButtonShare;
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
        mButtonShare = findViewById(R.id.button_share);
        mButtonMore = findViewById(R.id.button_more);

        mButtonShare.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                String postId = mModel.get(ID);
                String content = mModel.get(CONTENT);

                Share.performShareAction(postId, content, getContext());
            }

        });

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
            else if (item == REPORT_A_COMMENT) {
                ReportAComment.reportAComment(getContext(), mModel);
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
        Integer views = mModel.get(VIEWS);
        mTextViews.setText(views == null ? "" : String.valueOf(views));

        mTextLikes.setText("0");

        Long createdTime = mModel.get(CREATED_TIME);
        mTextTime.setText(createdTime == null ? "" : TimeUtil.getRelativeTime((long) mModel.get(CREATED_TIME), getContext()));

        List<ModelFeatures> comments = mModel.get(COMMENTS);

        mTextComments.setText(String.valueOf((comments == null || comments.isEmpty()) ? 0 : comments.size()));
    }

}
