package com.yashoid.talktome.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.yashoid.mmv.Managers;
import com.yashoid.mmv.Model;
import com.yashoid.mmv.ModelFeatures;
import com.yashoid.mmv.Target;
import com.yashoid.talktome.R;
import com.yashoid.talktome.model.post.MyPostList;
import com.yashoid.talktome.model.post.PostListAdapter;
import com.yashoid.talktome.view.LoadableContentView;
import com.yashoid.talktome.view.Toolbar;

import java.util.List;

public class MyPostsActivity extends AppCompatActivity implements MyPostList, Target {

    public static Intent getIntent(Context context) {
        Intent intent = new Intent(context, MyPostsActivity.class);

        return intent;
    }

    private LoadableContentView mHolderPosts;
    private RecyclerView mListPosts;

    private PostListAdapter mPostListAdapter;

    private Model mMyPostsModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myposts);

        ((Toolbar) findViewById(R.id.toolbar)).setActionButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mHolderPosts = findViewById(R.id.holder_posts);

        mListPosts = findViewById(R.id.list_posts);
        mListPosts.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));

        mPostListAdapter = new PostListAdapter();
        mListPosts.setAdapter(mPostListAdapter);

        Managers.registerTarget(this, FEATURES);
    }

    @Override
    public void setModel(Model model) {
        mMyPostsModel = model;

        onModelChanged();
    }

    @Override
    public void onFeaturesChanged(String... featureNames) {
        onModelChanged();
    }

    private void onModelChanged() {
        int state = mMyPostsModel.get(STATE);

        if (state == STATE_IDLE) {
            mMyPostsModel.perform(GET_MODELS);
        }

        List<ModelFeatures> posts = mMyPostsModel.get(MODEL_LIST);

        mPostListAdapter.setPosts(posts);
    }

}
