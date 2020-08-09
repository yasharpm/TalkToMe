package com.opentalkz.model.post.postdetails;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.opentalkz.Preferences;
import com.opentalkz.model.post.PostFullItemView;
import com.opentalkz.network.RequestRunner;
import com.opentalkz.ui.MyPostsActivity;
import com.opentalkz.ui.ProfileActivity;
import com.yashoid.mmv.ModelFeatures;

public class PostDetailsFragment extends Fragment implements PostFullItemView.OnActionListener {

    private static final String KEY_POST = "post";

    public static PostDetailsFragment newInstance(ModelFeatures postFeatures) {
        PostDetailsFragment fragment = new PostDetailsFragment();

        Bundle args = new Bundle();
        args.putParcelable(KEY_POST, postFeatures);

        fragment.setArguments(args);

        return fragment;
    }

    private ModelFeatures mPostFeatures;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();

        mPostFeatures = args.getParcelable(KEY_POST);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return new RecyclerView(inflater.getContext());
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView list = (RecyclerView) view;

        list.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        list.setAdapter(new PostDetailsAdapter(mPostFeatures, this));
    }

    @Override
    public void onProfileButtonClicked(String userId) {
        String selfUserId = Preferences.get(getContext()).readString(RequestRunner.USER_ID);

        if (TextUtils.equals(userId, selfUserId)) {
            startActivity(MyPostsActivity.getIntent(getContext()));
            return;
        }

        startActivity(ProfileActivity.getIntent(getContext(), userId));
    }

}
