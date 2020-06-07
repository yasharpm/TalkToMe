package com.opentalkz.ui;

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

import com.opentalkz.Scheme;
import com.opentalkz.network.SyncResponse;
import com.opentalkz.notification.PushService;
import com.opentalkz.notification.post.PostNotificationService;
import com.opentalkz.notification.post.RandomPostWidgetUpdatingService;
import com.yashoid.mmv.Managers;
import com.yashoid.mmv.ModelFeatures;
import com.yashoid.network.RequestResponse;
import com.yashoid.network.RequestResponseCallback;
import com.opentalkz.R;
import com.opentalkz.TTMOffice;
import com.opentalkz.evaluation.Eval;
import com.opentalkz.evaluation.Screens;
import com.opentalkz.network.PostResponse;
import com.opentalkz.network.Requests;

import java.io.IOException;

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

        startService(PostNotificationService.getIntent(this));

        if (RandomPostWidgetUpdatingService.getRefreshPeriod(this) == 0) {
            RandomPostWidgetUpdatingService.setDefaultRefreshPeriod(this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        Eval.setCurrentScreen(this, SCREEN_STARTUP);

        Intent intent = getIntent();

        Scheme.extrasToUri(intent);

        Uri data = intent.getData();

        if (data!= null) {
            if (Scheme.canHandle(data)) {
                moveOn(data);
            }
            else {
                String postId = data.getQueryParameter("id");
                moveOnWithPostId(postId);
            }

            return;
        }

        if (intent.hasExtra(PushService.SYNC)) {
            try {
                String sync = intent.getStringExtra(PushService.SYNC);

                SyncResponse.Change change = TTMOffice.network().getYashson().parse(sync, SyncResponse.Change.class);

                String postId = null;

                switch (change.getEventType()) {
                    case SyncResponse.Change.EVENT_NEW_COMMENT:
                        postId = change.getContent().postId;
                        break;
                }

                if (postId != null) {
                    moveOnWithPostId(postId);
                    return;
                }
            } catch (IOException e) {
                Log.e(TAG, "Failed to read sync object from json.");
            }
        }
        else if (intent.hasExtra(PushService.POST_ID)) {
            String postId = intent.getStringExtra(PushService.POST_ID);

            moveOnWithPostId(postId);
            return;
        }

        moveOn(null);
    }

    private void moveOnWithPostId(final String postId) {
        if (TextUtils.isEmpty(postId)) {
            moveOn(null);
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

                moveOn(null);
            }

        });
    }

    private void moveOn(final Uri data) {
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                if (!isFinishing()) {
                    Intent intent = MainActivity.getIntent(StartUpActivity.this);

                    if (data != null) {
                        intent.setData(data);
                    }

                    startActivity(intent);
                    finish();
                }
            }

        }, START_DELAY);
    }

}
