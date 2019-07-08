package com.yashoid.talktome.post.postdetails;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.yashoid.mmv.ModelFeatures;

public class PostDetailsFragment extends Fragment {

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
        list.setAdapter(new PostDetailsAdapter(mPostFeatures));
    }

}
