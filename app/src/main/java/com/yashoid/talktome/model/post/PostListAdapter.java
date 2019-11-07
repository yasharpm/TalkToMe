package com.yashoid.talktome.model.post;

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
import androidx.recyclerview.widget.RecyclerView;

import com.yashoid.mmv.ModelFeatures;
import com.yashoid.talktome.R;

import java.util.List;

public class PostListAdapter extends RecyclerView.Adapter {

    private static final int POST_MAX_LINES = 4;

    private static final int TYPE_POST = 0;
    private static final int TYPE_NO_POSTS = 1;

    public interface OnItemClickListener {

        void onItemClicked(int position, ModelFeatures modelFeatures);

        void onNewPostClicked();

    }

    private List<ModelFeatures> mPosts = null;

    private OnItemClickListener mOnItemClickListener = null;

    public PostListAdapter() {

    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    public void setPosts(List<ModelFeatures> posts) {
        mPosts = posts;

        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return (mPosts == null || !mPosts.isEmpty()) ? TYPE_POST : TYPE_NO_POSTS;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();

        switch (viewType) {
            case TYPE_POST:
                PostFullItemView view = new PostFullItemView(context);

                view.setMaxLines(POST_MAX_LINES);

                final RecyclerView.ViewHolder viewHolder = new RecyclerView.ViewHolder(view) { };

                view.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        onItemClicked(viewHolder.getAdapterPosition());
                    }

                });

                return viewHolder;
            case TYPE_NO_POSTS:
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

                RecyclerView.ViewHolder emptyHolder = new RecyclerView.ViewHolder(emptyView) { };

                emptyView.findViewById(R.id.button_newpost).setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        onNewPostClicked();
                    }

                });

                return emptyHolder;
        }

        throw new IllegalArgumentException("Unrecognized view type '" + viewType + "'.");
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (mPosts == null || mPosts.isEmpty()) {
            // Bypassing the binding of the empty view.
            return;
        }

        ModelFeatures postFeatures = mPosts.get(position);

        PostFullItemView itemView = (PostFullItemView) holder.itemView;

        itemView.setPost(postFeatures);
    }

    @Override
    public int getItemCount() {
        return mPosts == null ? 0 : (mPosts.isEmpty() ? 1 : mPosts.size());
    }

    private void onItemClicked(int position) {
        if (mOnItemClickListener != null) {
            mOnItemClickListener.onItemClicked(position, mPosts.get(position));
        }
    }

    private void onNewPostClicked() {
        if (mOnItemClickListener != null) {
            mOnItemClickListener.onNewPostClicked();
        }
    }

}
