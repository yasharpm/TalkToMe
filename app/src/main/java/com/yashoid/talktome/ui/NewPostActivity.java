package com.yashoid.talktome.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.yashoid.mmv.Managers;
import com.yashoid.mmv.Model;
import com.yashoid.mmv.ModelFeatures;
import com.yashoid.mmv.Target;
import com.yashoid.talktome.R;
import com.yashoid.talktome.evaluation.Eval;
import com.yashoid.talktome.evaluation.Events;
import com.yashoid.talktome.evaluation.Screens;
import com.yashoid.talktome.model.pendingpost.PendingPost;

public class NewPostActivity extends AppCompatActivity implements View.OnClickListener,
        PendingPost, Target, Screens, Events {

    public static Intent getIntent(Context context) {
        Intent intent = new Intent(context, NewPostActivity.class);

        return intent;
    }

    private Model mModel;

    private View mButtonClose;
    private EditText mEditPost;
    private TextView mTextWords;
    private View mButtonPost;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newpost);

        mButtonClose = findViewById(R.id.button_close);
        mButtonPost = findViewById(R.id.button_post);

        mButtonClose.setOnClickListener(this);
        mButtonPost.setOnClickListener(this);

        mEditPost = findViewById(R.id.edit_post);
        mTextWords = findViewById(R.id.text_words);

        mEditPost.addTextChangedListener(new TextWatcher() {

            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                mModel.perform(ON_CONTENT_CHANGED, s.toString());
            }

        });

        mEditPost.requestFocusFromTouch();

        ModelFeatures pendingPostFeatures = new ModelFeatures.Builder()
                .add(TYPE, TYPE_PENDING_POST)
                .build();

        Managers.registerTarget(this, pendingPostFeatures);
    }

    @Override
    protected void onResume() {
        super.onResume();

        Eval.setCurrentScreen(this, SCREEN_NEW_POST);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_close:
                finish();
                return;
            case R.id.button_post:
                mModel.perform(SEND_POST);
                return;
        }
    }

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
        Integer state = mModel.get(STATE);

        if (state == null) {
            return;
        }

        switch (state) {
            case STATE_IDLE:
            case STATE_FAILURE:
                mButtonClose.setEnabled(true);
                mEditPost.setEnabled(true);
                mButtonPost.setEnabled(true);
                break;
            case STATE_LOADING:
                mButtonClose.setEnabled(false);
                mEditPost.setEnabled(false);
                mButtonPost.setEnabled(false);
                break;
            case STATE_SUCCESS:
                Eval.trackEvent(EVENT_POSTED);

                Managers.unregisterTarget(this);

                mModel.set(CONTENT, null);
                mModel.set(STATE, STATE_IDLE);

                mModel.cache(true);

                finish();

                break;
        }

        String content = mModel.get(CONTENT);

        String currentContent = mEditPost.getText().toString();

        if (!TextUtils.equals(content, currentContent)) {
            mEditPost.setText(content);

            if (!TextUtils.isEmpty(content)) {
                mEditPost.setSelection(content.length());
            }
        }

        if (!TextUtils.isEmpty(content)) {
            int wordCount = content.split("([\\W\\s]+)").length;

            mTextWords.setText(String.valueOf(wordCount));
        }
        else {
            mTextWords.setText("0");
        }
    }

    @Override
    public void finish() {
        String content = mModel.get(CONTENT);

        if (content != null && content.trim().length() > 0) {
            Eval.trackEvent(EVENT_DIDNT_POST);
        }

        super.finish();
    }

}
