package com.yashoid.talktome.post;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.yashoid.mmv.Managers;
import com.yashoid.mmv.Model;
import com.yashoid.mmv.ModelFeatures;
import com.yashoid.mmv.Target;
import com.yashoid.talktome.R;
import com.yashoid.talktome.view.DismissableContent;
import com.yashoid.talktome.view.Toolbar;

public class PostListPagerFragment extends Fragment implements DismissableContent.OnDismissListener,
        View.OnClickListener, ViewPager.OnPageChangeListener, Target, Post {

    private static final String KEY_POST_LIST = "postList";
    private static final String KEY_COUNT = "count";
    private static final String KEY_START_PAGE = "startPage";

    public static PostListPagerFragment newInstance(ModelFeatures postListFeatures, int count, int startPage) {
        PostListPagerFragment fragment = new PostListPagerFragment();

        Bundle args = new Bundle();
        args.putParcelable(KEY_POST_LIST, postListFeatures);
        args.putInt(KEY_COUNT, count);
        args.putInt(KEY_START_PAGE, startPage);

        fragment.setArguments(args);

        return fragment;
    }

    private PostListPagerAdapter mAdapter;

    private ViewPager mContentPager;
    private EditText mEditComment;
    private View mButtonPostComment;

    private Model mModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_postlistpager, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ((Toolbar) view.findViewById(R.id.toolbar)).setActionButtonClickListener(this);

        mButtonPostComment = view.findViewById(R.id.button_postcomment);

        mEditComment = view.findViewById(R.id.edit_comment);
        mEditComment.addTextChangedListener(mEditCommentWatcher);

        Bundle args = getArguments();

        ModelFeatures postListFeatures = args.getParcelable(KEY_POST_LIST);
        int count = args.getInt(KEY_COUNT);

        mContentPager = view.findViewById(R.id.content);

        ((DismissableContent) view).setOnDismissListener(this);

        mAdapter = new PostListPagerAdapter(getChildFragmentManager(), postListFeatures, count);

        mContentPager.setAdapter(mAdapter);

        int startPage = args.getInt(KEY_START_PAGE);

        if (startPage != -1) {
            mContentPager.setCurrentItem(startPage);
        }

        mContentPager.addOnPageChangeListener(this);

        updateModel(mContentPager.getCurrentItem());

        mButtonPostComment.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mModel.perform(POST_COMMENT);
            }

        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        mAdapter = null;

        mContentPager = null;
        mEditComment = null;
        mButtonPostComment = null;

        Managers.unregisterTarget(this);

        mModel = null;
    }

    @Override
    public void onDismissed(DismissableContent view) {
        goBack();
    }

    @Override
    public void onClick(View v) {
        goBack();
    }

    private void goBack() {
        getFragmentManager().popBackStack();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) { }

    @Override
    public void onPageSelected(int position) {
        updateModel(position);
    }

    private void updateModel(int position) {
        if (mModel != null) {
            Managers.unregisterTarget(this);
        }

        Managers.registerTarget(this, mAdapter.getPostFeatures(position));
    }

    @Override
    public void onPageScrollStateChanged(int state) { }

    private TextWatcher mEditCommentWatcher = new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) { }

        @Override
        public void afterTextChanged(final Editable s) {
            mModel.set(PENDING_COMMENT, s.toString());
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

    private void onModelChanged() {
        String pendingComment = mModel.get(PENDING_COMMENT);

        if (!TextUtils.equals(mEditComment.getText().toString(), pendingComment)) {
            mEditComment.setText(pendingComment);

            if (pendingComment != null) {
                mEditComment.setSelection(pendingComment.length());
            }
        }

        int state = mModel.get(POST_COMMENT_STATE);

        switch (state) {
            case STATE_IDLE:
            case STATE_SUCCESS:
            case STATE_FAILURE:
                mEditComment.setEnabled(true);
                mButtonPostComment.setEnabled(true);
                break;
            case STATE_LOADING:
                mEditComment.setEnabled(false);
                mButtonPostComment.setEnabled(false);
                break;
        }
    }

}
