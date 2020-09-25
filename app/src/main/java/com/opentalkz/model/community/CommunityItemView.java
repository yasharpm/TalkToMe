package com.opentalkz.model.community;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.TextView;

import androidx.core.view.ViewCompat;

import com.opentalkz.R;
import com.yashoid.mmv.Model;
import com.yashoid.sequencelayout.SequenceLayout;

public class CommunityItemView extends SequenceLayout implements Community {

    private TextView mTextName;
    private TextView mTextDescription;

    public CommunityItemView(Context context) {
        super(context);
        initialize(context, null, 0);
    }

    public CommunityItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context, attrs, 0);
    }

    public CommunityItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context, attrs, defStyleAttr);
    }

    private void initialize(Context context, AttributeSet attrs, int defStyleAttr) {
        ViewCompat.setElevation(this, getResources().getDimension(R.dimen.item_community_elevation));

        setBackgroundResource(R.drawable.item_background);

        LayoutInflater.from(context).inflate(R.layout.item_community, this, true);

        addSequences(R.xml.sequences_communityitem);

        mTextName = findViewById(R.id.text_name);
        mTextDescription = findViewById(R.id.text_description);
    }

    public void setModel(Model model) {
        String name = model == null ? getResources().getString(R.string.default_community_name) : (String) model.get(NAME);
        String description = model == null ? getResources().getString(R.string.default_community_description) : (String) model.get(DESCRIPTION);

        mTextName.setText(name);
        mTextDescription.setText(description);
    }

}
