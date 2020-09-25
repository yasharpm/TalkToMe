package com.opentalkz.model.post;

import android.content.Context;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.MetricAffectingSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;

import com.opentalkz.model.list.ModelListAdapter;
import com.opentalkz.model.list.TargetViewHolder;
import com.yashoid.mmv.Model;
import com.opentalkz.R;

public class PostListAdapter extends ModelListAdapter<PostListAdapter.OnPostItemClickListener> {

    private static final int POST_MAX_LINES = 4;

    public interface OnPostItemClickListener extends ModelListAdapter.OnItemClickListener {

        void onNewPostClicked();

    }

    public PostListAdapter() {

    }

    @Override
    protected TargetViewHolder onCreateItemViewHolder(ViewGroup parent, OnPostItemClickListener itemListener) {
        PostFullItemView view = new PostFullItemView(parent.getContext());

        view.setMaxLines(POST_MAX_LINES);

        final TargetViewHolder viewHolder = new TargetViewHolder(view) {

            @Override
            protected void onModelChanged(Model model) {
                ((PostFullItemView) itemView).setModel(model);
            }

        };

        view.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                onItemClicked(viewHolder.getBindingAdapterPosition());
            }

        });

        return viewHolder;
    }

    @Override
    protected TargetViewHolder onCreateNoItemsViewHolder(ViewGroup parent, OnPostItemClickListener itemListener) {
        Context context = parent.getContext();

        View emptyView = LayoutInflater.from(context).inflate(R.layout.view_mypostsempty, parent, false);

        TextView textInfo = emptyView.findViewById(R.id.text_info);

        String info = context.getString(R.string.mypostsempty_info);
        String suffix = context.getString(R.string.mypostsempty_info_suffix);
        final int suffixColor = ResourcesCompat.getColor(context.getResources(), R.color.mypostsitem_info_suffix_textcolor, null);

        SpannableStringBuilder ssb = new SpannableStringBuilder(info + " " + suffix);
        ssb.setSpan(new MetricAffectingSpan() {

            @Override
            public void updateMeasureState(@NonNull TextPaint textPaint) { }

            @Override
            public void updateDrawState(TextPaint tp) {
                tp.setColor(suffixColor);
            }

        }, info.length() + 1, ssb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        textInfo.setText(ssb);

        TargetViewHolder emptyHolder = new TargetViewHolder(emptyView) {

            @Override
            protected void onModelChanged(Model model) { }

        };

        emptyView.findViewById(R.id.button_newpost).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                onNewPostClicked();
            }

        });

        return emptyHolder;
    }

    protected void onNewPostClicked() {
        if (getOnItemClickListener() != null) {
            getOnItemClickListener().onNewPostClicked();
        }
    }

}
