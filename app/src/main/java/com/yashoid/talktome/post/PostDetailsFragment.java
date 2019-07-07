package com.yashoid.talktome.post;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.yashoid.mmv.Managers;
import com.yashoid.mmv.Model;
import com.yashoid.mmv.ModelFeatures;
import com.yashoid.mmv.Target;

public class PostDetailsFragment extends Fragment implements Post, Target {

    private static final String KEY_POST = "post";

    public static PostDetailsFragment newInstance(ModelFeatures postFeatures) {
        PostDetailsFragment fragment = new PostDetailsFragment();

        Bundle args = new Bundle();
        args.putParcelable(KEY_POST, postFeatures);

        fragment.setArguments(args);

        return fragment;
    }

    private Model mPost;

    private TextView mTextContent;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        ModelFeatures postFeatures = args.getParcelable(KEY_POST);

        Managers.registerTarget(this, postFeatures);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return new RecyclerView(inflater.getContext());
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

//        mTextContent = view.findViewById(R.id.text_content);

        updateView();
    }

    @Override
    public void setModel(Model model) {
        mPost = model;

        onModelChanged();
    }

    @Override
    public void onFeaturesChanged(String... featureNames) {
        onModelChanged();
    }

    private void onModelChanged() {
        if (getView() != null) {
            updateView();
        }
    }

    private void updateView() {
//        mTextContent.setText((String) mPost.get(CONTENT));
//        mTextContent.setBackgroundColor((int) mPost.get(INDICATOR_COLOR));
    }

}
