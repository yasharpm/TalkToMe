package com.yashoid.talktome.post;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.yashoid.mmv.Managers;
import com.yashoid.mmv.Model;
import com.yashoid.mmv.ModelFeatures;
import com.yashoid.mmv.PersistentTarget;

import java.util.List;

public class PostListPagerAdapter extends FragmentPagerAdapter implements PostList, PersistentTarget {

    private List<ModelFeatures> mPosts;

    public PostListPagerAdapter(FragmentManager fm, ModelFeatures postListModelFeatures) {
        super(fm);

        Managers.registerTarget(this, postListModelFeatures);
    }

    @Override
    public void setModel(Model model) {
        mPosts = model.get(MODEL_LIST);

        Managers.unregisterTarget(this);
    }

    @Override
    public void onFeaturesChanged(String... featureNames) { }

    @Override
    public Fragment getItem(int position) {
        ModelFeatures post = mPosts.get(mPosts.size() - position - 1);

        return PostDetailsFragment.newInstance(post);
    }

    @Override
    public int getCount() {
        return mPosts.size();
    }

}
