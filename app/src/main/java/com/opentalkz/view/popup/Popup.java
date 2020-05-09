package com.opentalkz.view.popup;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import androidx.core.view.ViewCompat;

import com.opentalkz.R;

import java.util.ArrayList;
import java.util.List;

public class Popup {

    private static final int WRAP_MEASURE_SPEC = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);

    public interface OnItemSelectedListener {

        void onItemClicked(int position, PopupItem item);

    }

    private OnItemSelectedListener mListener;

    private View mAnchor;

    private PopupWindow mWindow;

    public Popup(View anchor, PopupItem[] items, OnItemSelectedListener listener) {
        mListener = listener;

        mAnchor = anchor;

        mWindow = new PopupWindow();
        mWindow.setFocusable(true);
        mWindow.setBackgroundDrawable(new ColorDrawable(0));

        final Context context = anchor.getContext();

        FrameLayout view = new FrameLayout(context);
        view.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mWindow.dismiss();
            }

        });

        mWindow.setContentView(view);
        mWindow.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
        mWindow.setHeight(WindowManager.LayoutParams.MATCH_PARENT);

        LinearLayout content = new LinearLayout(context);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setBackgroundResource(R.drawable.popup_background);
        ViewCompat.setElevation(content, content.getResources().getDimension(R.dimen.popup_elevation));

        List<ViewGroup.LayoutParams> allParams = new ArrayList<>(items.length);
        int maxWidth = 0;

        for (int i = 0; i < items.length; i++) {
            final int position = i;
            final PopupItem item = items[i];

            View itemView = item.createView(content);

            ViewGroup.LayoutParams params = itemView.getLayoutParams();
            allParams.add(params);

            itemView.measure(WRAP_MEASURE_SPEC, View.MeasureSpec.makeMeasureSpec(params.height, View.MeasureSpec.EXACTLY));
            params.width = itemView.getMeasuredWidth();
            maxWidth = Math.max(maxWidth, params.width);

            content.addView(itemView);

            itemView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    mWindow.dismiss();

                    mListener.onItemClicked(position, item);
                }

            });
        }

        for (ViewGroup.LayoutParams params: allParams) {
            params.width = maxWidth;
        }

        view.addView(content, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    public void showAtLocation(int x, int y) {
        int[] location = new int[2];
        mAnchor.getLocationInWindow(location);

        View content = ((ViewGroup) mWindow.getContentView()).getChildAt(0);
        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) content.getLayoutParams();

        layoutParams.leftMargin = location[0] + x;
        layoutParams.topMargin = location[1] + y;

        mWindow.showAtLocation(mAnchor, Gravity.NO_GRAVITY, 0, 0);
    }

    public void show() {
        int[] location = new int[2];

        mAnchor.getLocationInWindow(location);

        showAtLocation(location[0], location[1]);
    }

}
