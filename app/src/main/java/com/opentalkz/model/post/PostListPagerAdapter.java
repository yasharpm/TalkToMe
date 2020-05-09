package com.opentalkz.model.post;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.yashoid.mmv.Managers;
import com.yashoid.mmv.Model;
import com.yashoid.mmv.ModelFeatures;
import com.yashoid.mmv.PersistentTarget;
import com.opentalkz.model.list.ModelList;
import com.opentalkz.model.post.postdetails.PostDetailsFragment;

import java.util.List;

public class PostListPagerAdapter extends FragmentPagerAdapter implements ModelList, PersistentTarget {

    private List<ModelFeatures> mPosts;

    private int mCount;

    public PostListPagerAdapter(FragmentManager fm, ModelFeatures postListModelFeatures, int count) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);

        mCount = count;

        Managers.registerTarget(this, postListModelFeatures);
    }

    @Override
    public void setModel(Model model) {
        mPosts = ((List<ModelFeatures>) model.get(MODEL_LIST)).subList(0, mCount);

        Managers.unregisterTarget(this);
    }

    @Override
    public void onFeaturesChanged(String... featureNames) { }

    @Override
    public Fragment getItem(int position) {
        return PostDetailsFragment.newInstance(getPostFeatures(position));
    }

    public ModelFeatures getPostFeatures(int position) {
        return mPosts.get(mPosts.size() - position - 1);
    }

    @Override
    public int getCount() {
        return mPosts.size();
    }

}
