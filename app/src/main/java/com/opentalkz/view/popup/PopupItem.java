package com.opentalkz.view.popup;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;

import com.opentalkz.R;

public class PopupItem {

    private int mTitleResId;
    private int mIconResId;

    public PopupItem(int titleResId, int iconResId) {
        mTitleResId = titleResId;
        mIconResId = iconResId;
    }

    public View createView(ViewGroup parent) {
        final Context context = parent.getContext();

        String title = context.getString(mTitleResId);
        Drawable icon = ResourcesCompat.getDrawable(context.getResources(), mIconResId, null);

        View view = LayoutInflater.from(context).inflate(R.layout.popupitem, parent, false);

        ((TextView) view.findViewById(R.id.text_title)).setText(title);
        ((ImageView) view.findViewById(R.id.image_icon)).setImageDrawable(icon);

        return view;
    }

}
