package com.opentalkz.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.opentalkz.R;
import com.opentalkz.notification.post.PostNotificationService;
import com.opentalkz.notification.post.RandomPostWidgetUpdatingService;
import com.opentalkz.view.Toolbar;

import ir.abmyapp.androidsdk.ABResources;
import ir.abmyapp.androidsdk.IABResources;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int[] PERIOD_NAMES = {
            R.string.settings_period_off, R.string.settings_period_fivemins,
            R.string.settings_period_halfhour, R.string.settings_period_twohours
    };

    private static final long[] PERIODS = {
            0L, 5L * 60L * 1000L, 30L * 60L * 1000L, 2L * 60L * 60L * 1000L
    };

    private static final int[] NOTIFICATION_PERIOD_INDICES = {
            0, 1, 2, 3
    };

    private static final int[] WIDGET_PERIOD_INDICES = {
            1, 2, 3
    };

    public static Intent getIntent(Context context) {
        Intent intent = new Intent(context, SettingsActivity.class);

        return intent;
    }

    private Spinner mSpinnerNotification;
    private Spinner mSpinnerWidget;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        ((Toolbar) findViewById(R.id.toolbar)).setActionButtonClickListener(this);

        mSpinnerNotification = findViewById(R.id.spinner_notification);
        mSpinnerWidget = findViewById(R.id.spinner_widget);

        mSpinnerNotification.setAdapter(createPeriodAdapter(NOTIFICATION_PERIOD_INDICES));
        mSpinnerWidget.setAdapter(createPeriodAdapter(WIDGET_PERIOD_INDICES));

        long notificationRefreshPeriod = PostNotificationService.getRefreshPeriod(this);

        if (!setRefreshPeriod(mSpinnerNotification, NOTIFICATION_PERIOD_INDICES, notificationRefreshPeriod)) {
            PostNotificationService.setRefreshPeriod(this, 0);
        }

        long widgetRefreshPeriod = RandomPostWidgetUpdatingService.getRefreshPeriod(this);

        if (!setRefreshPeriod(mSpinnerWidget, WIDGET_PERIOD_INDICES, widgetRefreshPeriod)) {
            RandomPostWidgetUpdatingService.setRefreshPeriod(this, 0);
        }

        mSpinnerNotification.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                long period = PERIODS[NOTIFICATION_PERIOD_INDICES[position]];

                PostNotificationService.setRefreshPeriod(SettingsActivity.this, period);

                Intent intent;

                if (period == 0) {
                    intent = PostNotificationService.getTurnOffIntent(SettingsActivity.this);
                }
                else {
                    intent = PostNotificationService.getIntent(SettingsActivity.this);
                }

                startService(intent);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }

        });

        mSpinnerWidget.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                long period = PERIODS[WIDGET_PERIOD_INDICES[position]];
                RandomPostWidgetUpdatingService.setRefreshPeriod(SettingsActivity.this, period);

                if (period == 0) {
                    // Please don't!
                }
                else {
                    RandomPostWidgetUpdatingService.enqueueWork(SettingsActivity.this);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }

        });
    }

    private ArrayAdapter<String> createPeriodAdapter(int[] periodIndices) {
        IABResources res = ABResources.get(this);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.item_settingsoption);

        for (int index: periodIndices) {
            adapter.add(res.getString(PERIOD_NAMES[index]));
        }

        return adapter;
    }

    private boolean setRefreshPeriod(Spinner spinner, int[] periodIndices, long period) {
        int index = -1;

        for (int i = 0; i < periodIndices.length; i++) {
            if (PERIODS[periodIndices[i]] == period) {
                index = i;
                break;
            }
        }

        if (index == -1) {
            return false;
        }

        spinner.setSelection(index);

        return true;
    }

    @Override
    public void onClick(View v) {
        finish();
    }

}
