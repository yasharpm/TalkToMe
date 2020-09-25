package com.opentalkz.model.community;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.opentalkz.R;
import com.opentalkz.model.list.ModelListAdapter;
import com.opentalkz.view.Toolbar;
import com.yashoid.mmv.Managers;
import com.yashoid.mmv.Model;
import com.yashoid.mmv.ModelFeatures;
import com.yashoid.mmv.Target;

import java.util.List;

public class CommunityListActivity extends AppCompatActivity implements Target, CommunityList,
        ModelListAdapter.OnItemClickListener {

    public static Intent getIntent(Context context) {
        Intent intent = new Intent(context, CommunityListActivity.class);

        return intent;
    }

    private RecyclerView mListCommunity;

    private CommunityListAdapter mAdapter;

    private Model mModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_communitylist);

        ((Toolbar) findViewById(R.id.toolbar)).setActionButtonClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }

        });

        mListCommunity = findViewById(R.id.list_community);
        mListCommunity.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));

        mAdapter = new CommunityListAdapter();
        mAdapter.setOnItemClickListener(this);

        mListCommunity.setAdapter(mAdapter);

        Managers.registerTarget(this, FEATURES);
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
        List<ModelFeatures> communityList = mModel.get(MODEL_LIST);

        mAdapter.setModels(communityList);
    }

    @Override
    public void onItemClicked(int position, ModelFeatures modelFeatures) {
        String id = modelFeatures == null ? null : (String) modelFeatures.get(Community.ID);

        mModel.set(SELECTED_COMMUNITY_ID, id);
        mModel.cache(true);

        finish();
    }

}
