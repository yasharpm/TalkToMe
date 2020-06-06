package com.opentalkz.notification.post;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.RemoteViews;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;

import com.opentalkz.R;
import com.opentalkz.Share;
import com.opentalkz.model.post.Post;
import com.opentalkz.model.post.PostContentView;
import com.opentalkz.ui.StartUpActivity;
import com.yashoid.mmv.Model;

import java.util.concurrent.Semaphore;

public class RandomPostWidgetUpdatingService extends JobIntentService {

    private static final String TAG = "RandomPostWidgetUpdatingService";

    private static final long DEFAULT_REFRESH_PERIOD = 5L * 60L * 1000L;

    public static void enqueueWork(Context context) {
        Intent intent = new Intent(context, RandomPostWidgetUpdatingService.class);

        enqueueWork(context, RandomPostWidgetUpdatingService.class, TAG.hashCode(), intent);
    }

    public static long getRefreshPeriod(Context context) {
        return RefreshHandler.getRefreshPeriod(context, TAG);
    }

    public static void setRefreshPeriod(Context context, long period) {
        RefreshHandler.setRefreshPeriod(context, TAG, period);
    }

    public static void setDefaultRefreshPeriod(Context context) {
        setRefreshPeriod(context, DEFAULT_REFRESH_PERIOD);
    }

    private RefreshHandler mRefreshHandler;

    private Model mPost = null;

    @Override
    public void onCreate() {
        super.onCreate();

        mRefreshHandler = new RefreshHandler(this, TAG, RandomPostWidgetUpdatingService.class);
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        if (!mRefreshHandler.shouldRefresh()) {
            // We are not doing this because in case the user has resized the widget, we need
            // to update the bitmap we had created.
            // return;
        }

        AppWidgetManager widgetManager = AppWidgetManager.getInstance(this);

        int[] widgetIds = widgetManager.getAppWidgetIds(new ComponentName(this, RandomPostWidgetProvider.class));

        if (widgetIds == null || widgetIds.length == 0) {
            return;
        }

        mRefreshHandler.scheduleNextRefresh();

        RandomPostBox postBox = RandomPostBox.getInstance(this);

        final Semaphore locker = new Semaphore(0);

        postBox.next(new RandomPostBox.NextPostCallback() {

            @Override
            public void onNextPostResult(Model post, Object... args) {
                mPost = post;

                locker.release();
            }

        });

        try {
            locker.acquire();
        } catch (InterruptedException e) { }

        if (mPost == null) {
            return;
        }

        mRefreshHandler.onRefreshed();

        for (int widgetId: widgetIds) {
            updateWidget(widgetId, widgetManager);
        }

        mPost = null;
    }

    private void updateWidget(int widgetId, AppWidgetManager widgetManager) {
        Bundle options = widgetManager.getAppWidgetOptions(widgetId);

        int widgetWidthDp = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH);
        int widgetHeightDp = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT);

        DisplayMetrics metrics = getResources().getDisplayMetrics();

        int widgetWidth = (int) (metrics.density * widgetWidthDp);
        int widgetHeight = (int) (metrics.density * widgetHeightDp);

        PostContentView view = new PostContentView(this);

        Paint.FontMetrics fm = view.getPaint().getFontMetrics();
        int lineHeight = (int) (fm.bottom - fm.top + fm.leading);

        int lineCount = widgetHeight / lineHeight;
        view.setMaxLines(lineCount);

        int width = widgetWidth;
        int height = lineCount * lineHeight;

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);

        view.setModel(mPost);
        view.measure(
                View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY)
        );
        view.layout(0, 0, width, height);
        view.draw(canvas);

        RemoteViews rv = new RemoteViews(getPackageName(), R.layout.widget_post);
        rv.setImageViewBitmap(R.id.image_content, bitmap);

        String postId = mPost.get(Post.ID);
        Uri postUri = Share.getShareUri(postId);
        Intent openIntent = StartUpActivity.newIntent(this);
        openIntent.setData(postUri);
        PendingIntent pendingOpenIntent = PendingIntent.getActivity(this, 0, openIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        rv.setOnClickPendingIntent(R.id.container_post, pendingOpenIntent);

        widgetManager.updateAppWidget(widgetId, rv);
    }

}
