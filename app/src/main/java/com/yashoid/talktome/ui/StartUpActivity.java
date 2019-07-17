package com.yashoid.talktome.ui;

import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.yashoid.talktome.R;

public class StartUpActivity extends AppCompatActivity {

    private static final long START_DELAY = 2000;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startup);
    }

    @Override
    protected void onResume() {
        super.onResume();

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
