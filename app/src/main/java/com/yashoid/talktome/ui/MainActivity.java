package com.yashoid.talktome.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;

import com.yashoid.mmv.Managers;
import com.yashoid.mmv.Model;
import com.yashoid.mmv.ModelFeatures;
import com.yashoid.mmv.Target;
import com.yashoid.talktome.model.post.PostList;
import com.yashoid.talktome.model.post.PostListPagerFragment;
import com.yashoid.talktome.model.post.PostListViewBunchAdapter;
import com.yashoid.talktome.view.LoadableContentView;
import com.yashoid.talktome.R;
import com.yashoid.talktome.view.Toolbar;
import com.yashoid.talktome.view.popup.Popup;
import com.yashoid.talktome.view.popup.PopupItem;
import com.yashoid.talktome.view.viewbunch.ViewBunch;

public class MainActivity extends AppCompatActivity implements Target, PostList,
        ViewBunch.OnItemClickListener, View.OnClickListener {

    private static final PopupItem[] MORE_ITEMS = {
            new PopupItem(R.string.main_more_myposts, R.drawable.ic_post),
            new PopupItem(R.string.main_more_settings, R.drawable.ic_share),
            new PopupItem(R.string.main_more_aboutus, R.drawable.ic_settings),
            new PopupItem(R.string.main_more_guide, R.drawable.ic_guide),
    };

    public static Intent getIntent(Context context) {
        Intent intent = new Intent(context, MainActivity.class);

        return intent;
    }

    private LoadableContentView mLoadableContent;
    private ViewBunch mViewBunch;
    private View mButtonNewPost;

    private ModelFeatures mPostListFeatures;
    private Model mPostListModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ((Toolbar) findViewById(R.id.toolbar)).setActionButtonClickListener(mOnMoreClickListener);

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

        mButtonNewPost = findViewById(R.id.button_newpost);
        mButtonNewPost.setOnClickListener(this);

        Managers.registerTarget(this, mPostListFeatures);
    }

    private View.OnClickListener mOnMoreClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            Resources res = getResources();

            int x = res.getDimensionPixelSize(R.dimen.main_morepopup_x);
            int y = res.getDimensionPixelSize(R.dimen.main_morepopup_y);

            new Popup(v, MORE_ITEMS, mOnMoreItemSelectedListener).showAtLocation(x, y);
        }

    };

    private Popup.OnItemSelectedListener mOnMoreItemSelectedListener = new Popup.OnItemSelectedListener() {

        @Override
        public void onItemClicked(int position, PopupItem item) {
            // TODO
        }

    };

    @Override
    public void onClick(View v) {
        startActivity(NewPostActivity.getIntent(this));
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
