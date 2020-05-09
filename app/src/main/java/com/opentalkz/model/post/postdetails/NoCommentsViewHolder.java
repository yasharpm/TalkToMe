package com.opentalkz.model.post.postdetails;

import android.content.Context;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.MetricAffectingSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.opentalkz.R;
import com.opentalkz.view.item.PointedInfoView;

public class NoCommentsViewHolder extends RecyclerView.ViewHolder {

    public static NoCommentsViewHolder newInstance(Context context) {
        PointedInfoView view = new PointedInfoView(context);

        view.setContentView(R.layout.view_nocomments);

        TextView textInfo = view.findViewById(R.id.text_info);

        String text = context.getString(R.string.nocomments);
        String smiley = context.getString(R.string.nocomments_smiley);

        final int smileyColor = ResourcesCompat.getColor(context.getResources(), R.color.nocomments_smiley, null);

        SpannableStringBuilder ssb = new SpannableStringBuilder(text + " " + smiley);
        ssb.setSpan(new MetricAffectingSpan() {

            @Override
            public void updateDrawState(TextPaint tp) {
                tp.setColor(smileyColor);
            }

            @Override
            public void updateMeasureState(@NonNull TextPaint textPaint) {

            }

        }, text.length() + 1, ssb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        textInfo.setText(ssb);

        view.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
                ));

        return new NoCommentsViewHolder(view);
    }

    private NoCommentsViewHolder(@NonNull View itemView) {
        super(itemView);
    }

}
