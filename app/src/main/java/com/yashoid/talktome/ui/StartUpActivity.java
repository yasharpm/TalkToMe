package com.yashoid.talktome.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.yashoid.talktome.R;
import com.yashoid.talktome.evaluation.Eval;
import com.yashoid.talktome.evaluation.Screens;

public class StartUpActivity extends AppCompatActivity implements Screens {

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
