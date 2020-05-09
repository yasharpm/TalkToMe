package com.opentalkz.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.MetricAffectingSpan;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.opentalkz.R;
import com.opentalkz.evaluation.Eval;
import com.opentalkz.evaluation.Screens;
import com.opentalkz.view.Toolbar;

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

        String warningAttention = getString(R.string.aboutus_warning_attention);
        String warning = getString(R.string.aboutus_warning);

        final int attentionTextColor = ContextCompat.getColor(this, R.color.aboutus_warning_attention_textcolor);

        SpannableStringBuilder ssb = new SpannableStringBuilder(warningAttention + " " + warning);

        ssb.setSpan(new MetricAffectingSpan() {

            @Override
            public void updateMeasureState(@NonNull TextPaint textPaint) { }

            @Override
            public void updateDrawState(TextPaint tp) {
                tp.setColor(attentionTextColor);
            }

        }, 0, warningAttention.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        ((TextView) findViewById(R.id.text_warning)).setText(ssb);
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
