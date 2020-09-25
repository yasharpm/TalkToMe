package com.opentalkz.ui;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.opentalkz.Preferences;
import com.opentalkz.Scheme;
import com.opentalkz.network.SyncResponse;
import com.opentalkz.notification.PushService;
import com.opentalkz.notification.post.PostNotificationService;
import com.opentalkz.notification.post.RandomPostWidgetUpdatingService;
import com.opentalkz.R;
import com.opentalkz.TTMOffice;
import com.opentalkz.evaluation.Eval;
import com.opentalkz.evaluation.Screens;

import java.io.IOException;

public class StartUpActivity extends AppCompatActivity implements Screens {

    private static final String TAG = "StartUpActivity";

    private static final long FIRST_START_DELAY = 6_000;
    private static final long START_DELAY = 2_000;

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

        final Uri data = intent.getData();

        if (data != null) {
            if (Scheme.canHandle(data)) {
                Scheme.prepareForUri(this, data, new Runnable() {

                    @Override
                    public void run() {
                        moveOn(data);
                    }

                });
            }
            else {
                moveOn(data);
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
                    final Uri uri = Scheme.makePostUri(postId);

                    Scheme.prepareForUri(this, uri, new Runnable() {

                        @Override
                        public void run() {
                            moveOn(uri);
                        }

                    });
                    return;
                }
            } catch (IOException e) {
                Log.e(TAG, "Failed to read sync object from json.");
            }
        }
        else if (intent.hasExtra(PushService.POST_ID)) {
            String postId = intent.getStringExtra(PushService.POST_ID);
            final Uri uri = Scheme.makePostUri(postId);

            Scheme.prepareForUri(this, uri, new Runnable() {

                @Override
                public void run() {
                    moveOn(uri);
                }

            });
            return;
        }

        moveOn(null);
    }

    private void moveOn(final Uri data) {
        final boolean firstRun =
                Preferences.get(this).readBoolean(MainActivity.PREF_FIRST_RUN, true);

        long delay = firstRun ? FIRST_START_DELAY : START_DELAY;

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                if (!isFinishing()) {
                    // TODO If first run, go to intro screen and community selection.

                    Intent intent = getMainIntent();

                    if (data != null) {
                        intent.setData(data);
                    }

                    startActivity(intent);
                    finish();
                }
            }

        }, delay);
    }

    private Intent getMainIntent() {
        return MainActivity.getIntent(this);
    }

}
