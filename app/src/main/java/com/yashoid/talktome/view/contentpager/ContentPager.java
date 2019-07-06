package com.yashoid.talktome.view.contentpager;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;

import androidx.viewpager.widget.PagerAdapter;

public class ContentPager {

    private Context mContext;

    private PopupWindow mWindow = null;

    private PagerAdapter mAdapter = null;

    private int mStartPage = -1;

    public ContentPager(Context context) {
        mContext = context;
    }

    public void setAdapter(PagerAdapter adapter) {
        mAdapter = adapter;
    }

    public void setStartPage(int startPage) {
        mStartPage = startPage;
    }

    public void show(View anchor) {
        if (mWindow != null) {
            mWindow.dismiss();
        }

        mWindow = new PopupWindow();

        ContentPagerView view = new ContentPagerView(mContext, mWindow, mAdapter, mStartPage);
        mWindow.setContentView(view);

        mWindow.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
        mWindow.setHeight(WindowManager.LayoutParams.MATCH_PARENT);
        mWindow.setFocusable(true);

        mWindow.showAtLocation(anchor, Gravity.LEFT | Gravity.TOP, 0, 0);
    }

}
