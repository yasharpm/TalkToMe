package com.yashoid.talktome.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.yashoid.talktome.R;
import com.yashoid.talktome.evaluation.Eval;
import com.yashoid.talktome.evaluation.Screens;
import com.yashoid.talktome.view.Toolbar;

public class AboutUsActivity extends AppCompatActivity implements View.OnClickListener, Screens {

    public static Intent getIntent(Context context) {
        Intent intent = new Intent(context, AboutUsActivity.class);

        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aboutus);

        ((Toolbar) findViewById(R.id.toolbar)).setActionButtonClickListener(this);

        ((TextView) findViewById(R.id.text_about)).setText(Html.fromHtml(getString(R.string.aboutus_about)));
    }

    @Override
    protected void onResume() {
        super.onResume();

        Eval.setCurrentScreen(this, SCREEN_ABOUT_US);
    }

    @Override
    public void onClick(View v) {
        finish();
    }

}
