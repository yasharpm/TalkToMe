package com.yashoid.talktome.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.yashoid.mmv.Managers;
import com.yashoid.mmv.Model;
import com.yashoid.mmv.ModelFeatures;
import com.yashoid.mmv.Target;
import com.yashoid.talktome.post.PostList;
import com.yashoid.talktome.post.PostListViewBunchAdapter;
import com.yashoid.talktome.view.LoadableContentView;
import com.yashoid.talktome.R;
import com.yashoid.talktome.view.viewbunch.ViewBunch;

public class MainActivity extends AppCompatActivity implements Target, PostList {

    private LoadableContentView mLoadableContent;
    private ViewBunch mViewBunch;

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

        ModelFeatures postListFeatures = new ModelFeatures.Builder()
                .add(TYPE, TYPE_POST_LIST)
                .build();

        mViewBunch = findViewById(R.id.viewbunch);
        mViewBunch.setAdapter(new PostListViewBunchAdapter(postListFeatures));

        Managers.registerTarget(this, postListFeatures);
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

}
