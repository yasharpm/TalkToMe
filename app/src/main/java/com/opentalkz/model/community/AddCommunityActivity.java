package com.opentalkz.model.community;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.opentalkz.R;
import com.opentalkz.view.Toolbar;
import com.yashoid.mmv.Managers;
import com.yashoid.mmv.Model;
import com.yashoid.mmv.ModelFeatures;
import com.yashoid.mmv.SingleShotTarget;
import com.yashoid.mmv.Target;

import java.util.List;

public class AddCommunityActivity extends AppCompatActivity implements Community, Target, View.OnClickListener {

    private static final String EXTRA_COMMUNITY = "community";

    public static Intent getIntent(Context context, ModelFeatures communityFeatures) {
        Intent intent = new Intent(context, AddCommunityActivity.class);

        intent.putExtra(EXTRA_COMMUNITY, communityFeatures);

        return intent;
    }

    private TextView mTextName;
    private TextView mTextDescription;
    private View mButtonAdd;
    private View mTextAlreadyExists;

    private Model mModel;
    private Model mCommunityList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addcommunity);

        ((Toolbar) findViewById(R.id.toolbar)).setActionButtonClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }

        });

        mTextName = findViewById(R.id.text_name);
        mTextDescription = findViewById(R.id.text_description);
        mButtonAdd = findViewById(R.id.button_add);
        mTextAlreadyExists = findViewById(R.id.text_alreadyexists);

        mButtonAdd.setOnClickListener(this);
        mTextAlreadyExists.setVisibility(View.INVISIBLE);

        ModelFeatures communityFeatures = getIntent().getParcelableExtra(EXTRA_COMMUNITY);
        Managers.registerTarget(this, communityFeatures);

        Managers.registerTarget(mCommunityListTarget, CommunityList.FEATURES);
    }

    private Target mCommunityListTarget = new Target() {

        @Override
        public void setModel(Model model) {
            mCommunityList = model;

            onCommunityListChanged();
        }

        @Override
        public void onFeaturesChanged(String... featureNames) {
            onCommunityListChanged();
        }

    };

    @Override
    public void setModel(Model model) {
        mModel = model;

        onModelChanged();
    }

    @Override
    public void onFeaturesChanged(String... featureNames) {
        onModelChanged();
    }

    private void onCommunityListChanged() {
        if (mCommunityList == null || mModel == null) {
            return;
        }

        mButtonAdd.setVisibility(View.VISIBLE);
        mTextAlreadyExists.setVisibility(View.INVISIBLE);

        List<ModelFeatures> communities = mCommunityList.get(CommunityList.MODEL_LIST);

        if (communities == null || communities.isEmpty()) {
            return;
        }

        for (ModelFeatures community: communities) {
            if (community.get(ID).equals(mModel.get(ID))) {
                mButtonAdd.setVisibility(View.INVISIBLE);
                mTextAlreadyExists.setVisibility(View.VISIBLE);
                return;
            }
        }
    }

    private void onModelChanged() {
        if (mModel == null) {
            return;
        }

        mTextName.setText((String) mModel.get(NAME));
        mTextDescription.setText((String) mModel.get(DESCRIPTION));
    }

    @Override
    public void onClick(View v) {
        v.setEnabled(false);

        final String communityId = mModel.get(ID);

        SingleShotTarget.get(CommunityList.FEATURES, new SingleShotTarget.ModelCallback() {

            @Override
            public void onModelReady(Model model) {
                ModelFeatures communityFeatures = new ModelFeatures.Builder()
                        .addAll(mModel.getAllFeatures())
                        .build();

                model.perform(CommunityList.ADD_COMMUNITY, communityFeatures);
                model.set(CommunityList.SELECTED_COMMUNITY_ID, communityId);
                model.cache(true);

                finish();
            }

        });
    }

}
