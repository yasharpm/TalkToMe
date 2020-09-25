package com.opentalkz.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.yashoid.mmv.Managers;
import com.yashoid.mmv.Model;
import com.yashoid.mmv.ModelFeatures;
import com.yashoid.mmv.Target;
import com.opentalkz.R;
import com.opentalkz.evaluation.Eval;
import com.opentalkz.evaluation.Events;
import com.opentalkz.evaluation.Screens;
import com.opentalkz.model.post.MyPostList;
import com.opentalkz.model.post.PostListAdapter;
import com.opentalkz.model.post.PostListPagerFragment;
import com.opentalkz.notification.ChangeTracker;
import com.opentalkz.view.LoadableContentView;
import com.opentalkz.view.Toolbar;

import java.util.List;

public class MyPostsActivity extends AppCompatActivity implements MyPostList, Target,
        PostListAdapter.OnPostItemClickListener, Screens, Events {

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

        Eval.trackEvent(EVENT_VISITED_MY_POSTS);

        mHolderPosts = findViewById(R.id.holder_posts);
        mHolderPosts.setOnStateChangedListener(new LoadableContentView.OnStateChangedListener() {

            @Override
            public void onStateChanged(int state) {
                if (state == STATE_LOADING) {
                    mMyPostsModel.perform(GET_MODELS);
                }
            }

        });

        mListPosts = findViewById(R.id.list_posts);
        mListPosts.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));

        mPostListAdapter = new PostListAdapter();
        mPostListAdapter.setOnItemClickListener(this);

        mListPosts.setAdapter(mPostListAdapter);

        Managers.registerTarget(this, FEATURES);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
            Eval.setCurrentScreen(this, SCREEN_MY_POSTS);
        }
    }

    @Override
    public void setModel(Model model) {
        mMyPostsModel = model;

        mMyPostsModel.perform(GET_MODELS);

        onModelChanged();
    }

    @Override
    public void onFeaturesChanged(String... featureNames) {
        onModelChanged();
    }

    private void onModelChanged() {
        int state = mMyPostsModel.get(STATE);

        if (state == STATE_SUCCESS) {
            ChangeTracker.get(this).reset();
        }

        mHolderPosts.stopLoading();

        List<ModelFeatures> posts = mMyPostsModel.get(MODEL_LIST);

        mPostListAdapter.setModels(posts);
    }

    @Override
    public void onItemClicked(int position, ModelFeatures modelFeatures) {
        List<ModelFeatures> posts = mMyPostsModel.get(MODEL_LIST);

        int count = posts.size();
        int startPage = count - position - 1;

        Fragment fragment = PostListPagerFragment.newInstance(MyPostList.FEATURES, count, startPage);

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.overlay, fragment)
                .addToBackStack(null)
                .commit();

        Eval.setCurrentScreen(this, SCREEN_MY_POSTS_PAGER);
    }

    @Override
    public void onNewPostClicked() {
        startActivity(NewPostActivity.getIntent(this));
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if (!isFinishing() && getSupportFragmentManager().getBackStackEntryCount() == 0) {
            Eval.setCurrentScreen(this, SCREEN_MY_POSTS);
        }
    }

}
