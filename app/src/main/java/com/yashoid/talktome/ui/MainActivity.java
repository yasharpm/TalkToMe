package com.yashoid.talktome.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;

import com.yashoid.mmv.Managers;
import com.yashoid.mmv.Model;
import com.yashoid.mmv.ModelFeatures;
import com.yashoid.mmv.Target;
import com.yashoid.talktome.post.PostList;
import com.yashoid.talktome.post.PostListPagerFragment;
import com.yashoid.talktome.post.PostListViewBunchAdapter;
import com.yashoid.talktome.view.LoadableContentView;
import com.yashoid.talktome.R;
import com.yashoid.talktome.view.viewbunch.ViewBunch;

public class MainActivity extends AppCompatActivity implements Target, PostList, ViewBunch.OnItemClickListener {

    private LoadableContentView mLoadableContent;
    private ViewBunch mViewBunch;

    private ModelFeatures mPostListFeatures;
    private Model mPostListModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLoadableContent = findViewById(R.id.loadableContent);
        mLoadableContent.setOnStateChangedListener(new LoadableContentView.OnStateChangedListener() {
            @Override
            public void onStateChanged(int state) {
                if (state == LoadableContentView.STATE_LOADING) {
                    int modelState = mPostListModel.get(STATE);

                    if (modelState == STATE_LOADING) {
                        return;
                    }

                    mPostListModel.perform(GET_MODELS, mViewBunch.getRequiredItemCount());
                }
            }
        });

        mPostListFeatures = new ModelFeatures.Builder()
                .add(TYPE, TYPE_POST_LIST)
                .build();

        mViewBunch = findViewById(R.id.viewbunch);
        mViewBunch.setAdapter(new PostListViewBunchAdapter(mPostListFeatures));
        mViewBunch.setOnItemClickListener(this);

        Managers.registerTarget(this, mPostListFeatures);
    }

    @Override
    public void setModel(Model model) {
        mPostListModel = model;

        onModelChanged();
    }

    @Override
    public void onFeaturesChanged(String... featureNames) {
        onModelChanged();
    }

    private void onModelChanged() {
        int state = mPostListModel.get(STATE);

        switch (state) {
            case STATE_LOADING:
            case STATE_IDLE:
                mLoadableContent.startLoading();
                break;
            case STATE_SUCCESS:
            case STATE_FAILURE:
                mLoadableContent.stopLoading();
                break;
        }
    }

    @Override
    public void onItemClicked(ViewBunch parent, ViewBunch.ViewBunchItem item, int position) {
        int count = parent.getVisibleItemCount();
        int startPage = count - position - 1;

        Fragment fragment = PostListPagerFragment.newInstance(mPostListFeatures, count, startPage);

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.overlay, fragment)
                .addToBackStack(null)
                .commit();
    }

}
