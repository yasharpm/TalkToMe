package com.yashoid.talktome.ui;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.yashoid.mmv.Managers;
import com.yashoid.mmv.ModelFeatures;
import com.yashoid.network.RequestResponse;
import com.yashoid.network.RequestResponseCallback;
import com.yashoid.talktome.R;
import com.yashoid.talktome.TTMOffice;
import com.yashoid.talktome.evaluation.Eval;
import com.yashoid.talktome.evaluation.Screens;
import com.yashoid.talktome.network.PostResponse;
import com.yashoid.talktome.network.Requests;

public class StartUpActivity extends AppCompatActivity implements Screens {

    private static final String TAG = "StartUpActivity";

    private static final long START_DELAY = 1000;

    public static Intent newIntent(Context context) {
        Intent intent = new Intent(context, StartUpActivity.class);

        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startup);
    }

    @Override
    protected void onResume() {
        super.onResume();

        Eval.setCurrentScreen(this, SCREEN_STARTUP);

        Intent intent = getIntent();

        Uri data = intent.getData();

        if (data == null) {
            moveOn();
            return;
        }

        final String postId = data.getQueryParameter("id");

        if (TextUtils.isEmpty(postId)) {
            moveOn();
            return;
        }

        TTMOffice.runner(this).runForUI(Requests.getPost(postId), new RequestResponseCallback<PostResponse>() {

            @Override
            public void onRequestResponse(RequestResponse<PostResponse> response) {
                if (response.isSuccessful()) {
                    ModelFeatures postFeatures = response.getContent().asModelFeatures();

                    Managers.registerModel(postFeatures);

                    startActivity(MainActivity.getIntent(StartUpActivity.this, postId));

                    finish();

                    return;
                }

                Log.i(TAG, "Failed to get post from server.");

                Toast.makeText(StartUpActivity.this, response.getResponseCode() == 404 ? R.string.startup_postnotfound : R.string.startup_gettingpostfailed, Toast.LENGTH_LONG).show();

                moveOn();
            }

        });
    }

    private void moveOn() {
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                if (!isFinishing()) {
                    startActivity(MainActivity.getIntent(StartUpActivity.this));
                    finish();
                }
            }

        }, START_DELAY);
    }

}
