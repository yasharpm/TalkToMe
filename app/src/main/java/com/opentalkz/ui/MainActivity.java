package com.opentalkz.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.opentalkz.Scheme;
import com.opentalkz.model.community.Community;
import com.opentalkz.model.community.CommunityList;
import com.opentalkz.model.community.CommunityListActivity;
import com.yashoid.mmv.Managers;
import com.yashoid.mmv.Model;
import com.yashoid.mmv.ModelFeatures;
import com.yashoid.mmv.PersistentTarget;
import com.yashoid.mmv.SingleShotTarget;
import com.yashoid.mmv.Target;
import com.opentalkz.TTMOffice;
import com.opentalkz.evaluation.Eval;
import com.opentalkz.evaluation.Events;
import com.opentalkz.evaluation.Screens;
import com.opentalkz.model.post.PostListPagerFragment;
import com.opentalkz.model.post.PostListViewBunchAdapter;
import com.opentalkz.model.post.RandomPostList;
import com.opentalkz.model.post.SeenPostsTracker;
import com.opentalkz.notification.ChangeTracker;
import com.opentalkz.view.LoadableContentView;
import com.opentalkz.R;
import com.opentalkz.view.Toolbar;
import com.opentalkz.view.item.InfoItemView;
import com.opentalkz.view.popup.Popup;
import com.opentalkz.view.popup.PopupItem;
import com.opentalkz.view.viewbunch.ViewBunch;

import java.util.List;

public class MainActivity extends AppCompatActivity implements Target, RandomPostList,
        ViewBunch.OnItemClickListener, View.OnClickListener, Screens, Events {

    public static final String PREF_FIRST_RUN = "first_run";

    private static final PopupItem MY_POSTS = new PopupItem(R.string.main_more_myposts, R.drawable.ic_post);
    private static final PopupItem COMMUNITIES = new PopupItem(R.string.main_more_communities, R.drawable.ic_communities);
    private static final PopupItem SETTINGS = new PopupItem(R.string.main_more_settings, R.drawable.ic_settings);
    private static final PopupItem ABOUT_US = new PopupItem(R.string.main_more_aboutus, R.drawable.ic_about);
    private static final PopupItem GUIDE = new PopupItem(R.string.main_more_guide, R.drawable.ic_guide);

    private static final PopupItem[] MORE_ITEMS = {
            MY_POSTS,
            COMMUNITIES,
            SETTINGS,
            ABOUT_US,
//            GUIDE_ME,
    };

    public static Intent getIntent(Context context) {
        Intent intent = new Intent(context, MainActivity.class);

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        return intent;
    }

    private Toolbar mToolbar;

    private LoadableContentView mLoadableContent;
    private ViewBunch mViewBunch;
    private View mButtonNewPost;
    private View mTextNewPost;

    private Model mPostListModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SeenPostsTracker.get(this).checkToSend();

        mToolbar = findViewById(R.id.toolbar);
        mToolbar.setActionButtonClickListener(mOnMoreClickListener);

        Eval.trackEvent(EVENT_VISITED);

        mLoadableContent = findViewById(R.id.loadableContent);
        mLoadableContent.setOnStateChangedListener(new LoadableContentView.OnStateChangedListener() {
            @Override
            public void onStateChanged(int state) {
                if (state == LoadableContentView.STATE_LOADING) {
                    int modelState = mPostListModel.get(STATE);

                    if (modelState == STATE_LOADING) {
                        return;
                    }

                    Eval.trackEvent(EVENT_REFRESHED_POSTS);

                    mPostListModel.perform(GET_MODELS, mViewBunch.getRequiredItemCount());
                }
            }
        });

        mViewBunch = findViewById(R.id.viewbunch);
        mViewBunch.setAdapter(new PostListViewBunchAdapter(FEATURES));
        mViewBunch.setOnItemClickListener(this);

        mButtonNewPost = findViewById(R.id.button_newpost);
        mTextNewPost = findViewById(R.id.text_newpost);
        mButtonNewPost.setOnClickListener(this);
        mTextNewPost.setOnClickListener(this);

        Managers.registerTarget(this, FEATURES);
        Managers.registerTarget(mCommunityListTarget, CommunityList.FEATURES);

        if (isFirstRun()) {
            mLoadableContent.removeView(mViewBunch);
            mLoadableContent.addView(InfoItemView.infoView(this, R.string.main_firsttimeinfo));
        }

        ChangeTracker.ChangeCountObserver changeCountObserver = new ChangeTracker.ChangeCountObserver() {

            @Override
            public void onChangeCountUpdated(int changeCount) {
                mToolbar.setNotifierCount(changeCount);
            }

        };

        mToolbar.setTag(changeCountObserver);

        ChangeTracker.get(this).registerChangeCountObserver(changeCountObserver);

        handleIntentData(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        handleIntentData(intent);
    }

    private void handleIntentData(Intent intent) {
        Uri data = intent.getData();

        if (data != null) {
            Scheme.handleUri(this, data);

            intent.setData(null);
        }
    }

    private boolean isFirstRun() {
        return TTMOffice.preferences(this).readBoolean(PREF_FIRST_RUN, true);
    }

    private void setFirstRun(boolean firstRun) {
        TTMOffice.preferences(this).write(PREF_FIRST_RUN, firstRun);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
            Eval.setCurrentScreen(this, SCREEN_POSTS);
        }

        ChangeTracker.get(this).refresh();
    }

    private View.OnClickListener mOnMoreClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            Resources res = getResources();

            int x = res.getDimensionPixelSize(R.dimen.more_popup_x);
            int y = res.getDimensionPixelSize(R.dimen.more_popup_y);

            new Popup(v, MORE_ITEMS, mOnMoreItemSelectedListener).showAtLocation(x, y);
        }

    };

    private Popup.OnItemSelectedListener mOnMoreItemSelectedListener = new Popup.OnItemSelectedListener() {

        @Override
        public void onItemClicked(int position, PopupItem item) {
            if (item == MY_POSTS) {
                startActivity(MyPostsActivity.getIntent(MainActivity.this));
            }
            else if (item == COMMUNITIES) {
                startActivity(CommunityListActivity.getIntent(MainActivity.this));
            }
            else if (item == SETTINGS) {
                startActivity(SettingsActivity.getIntent(MainActivity.this));
            }
            else if (item == ABOUT_US) {
                startActivity(AboutUsActivity.getIntent(MainActivity.this));
            }
        }

    };

    @Override
    public void onClick(View v) {
        startActivity(NewPostActivity.getIntent(this));
    }

    private Target mCommunityListTarget = new PersistentTarget() {

        private Model mCommunityList;

        @Override
        public void setModel(Model model) {
            mCommunityList = model;

            updateTitle();
        }

        @Override
        public void onFeaturesChanged(String... featureNames) {
            updateTitle();
        }

        private void updateTitle() {
            String selectedCommunityId = mCommunityList.get(CommunityList.SELECTED_COMMUNITY_ID);

            if (selectedCommunityId == null) {
                mToolbar.setTitle("");
                return;
            }

            List<ModelFeatures> communities = mCommunityList.get(CommunityList.MODEL_LIST);

            if (communities != null) {
                for (ModelFeatures community : communities) {
                    if (TextUtils.equals(selectedCommunityId, (String) community.get(Community.ID))) {
                        SingleShotTarget.get(community, new SingleShotTarget.ModelCallback() {

                            @Override
                            public void onModelReady(Model model) {
                                String communityName = model.get(Community.NAME);

                                String title = "- " + (communityName == null ? getString(R.string.main_unnamed_community) : communityName);

                                mToolbar.setTitle(title);
                            }

                        });
                        return;
                    }
                }
            }

            mToolbar.setTitle("- " + getString(R.string.main_unnamed_community));
        }

    };

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
            case STATE_IDLE:
                if (!isFirstRun()) {
                    mLoadableContent.startLoading();
                }
                break;
            case STATE_LOADING:
                mLoadableContent.startLoading();
                break;
            case STATE_SUCCESS:
                mLoadableContent.stopLoading();

                if (isFirstRun()) {
                    mLoadableContent.removeAllViews();
                    mLoadableContent.addView(mViewBunch);

                    setFirstRun(false);
                }

                if (mViewBunch.getParent() == null) {
                    mLoadableContent.removeAllViews();
                    mLoadableContent.addView(mViewBunch);
                }

                break;
            case STATE_FAILURE:
                mLoadableContent.stopLoading();

                if (isFirstRun()) {
                    setFirstRun(false);
                }

                mLoadableContent.removeAllViews();
                mLoadableContent.addView(InfoItemView.errorView(this, R.string.main_loaderror, mOnRetryClickListener));
                break;
        }
    }

    private View.OnClickListener mOnRetryClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            mLoadableContent.startLoading();
        }

    };

    @Override
    public void onItemClicked(ViewBunch parent, ViewBunch.ViewBunchItem item, int position) {
        int count = parent.getVisibleItemCount();
        int startPage = count - position - 1;

        Fragment fragment = PostListPagerFragment.newInstance(FEATURES, count, startPage);

        addOnOverlay(fragment);

        Eval.setCurrentScreen(this, SCREEN_POSTS_PAGER);
    }

    public void addOnOverlay(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.overlay, fragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if (!isFinishing() && getSupportFragmentManager().getBackStackEntryCount() == 0) {
            Eval.setCurrentScreen(this, SCREEN_POSTS);
        }
    }

}
