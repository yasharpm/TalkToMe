package com.yashoid.talktome.view.popup;

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

import com.yashoid.talktome.R;

public class Popup {

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

        for (int i = 0; i < items.length; i++) {
            final int position = i;
            final PopupItem item = items[i];

            View itemView = item.createView(content);

            content.addView(itemView);

            itemView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    mWindow.dismiss();

                    mListener.onItemClicked(position, item);
                }

            });
        }

        view.addView(content, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    public void showAtLocation(int x, int y) {
        View content = ((ViewGroup) mWindow.getContentView()).getChildAt(0);
        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) content.getLayoutParams();
        layoutParams.leftMargin = x;
        layoutParams.topMargin = y;

        mWindow.showAtLocation(mAnchor, Gravity.NO_GRAVITY, 0, 0);
    }

    public void show() {
        int[] location = new int[2];

        mAnchor.getLocationInWindow(location);

        showAtLocation(location[0], location[1]);
    }

}
