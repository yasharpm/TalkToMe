package com.yashoid.talktome.network;

import com.yashoid.mmv.ModelFeatures;
import com.yashoid.talktome.model.post.Post;

import java.util.ArrayList;
import java.util.List;

public class PostResponse implements Post {

    private String mUserId;
    private String mContent;
    private String mLanguage;
    private String mCountry;
    private long mCreatedTime;
    private long mUpdatedTime;
    private String mId;
    private int mCommentCount;
    private int mLikeCount;
    private int mViewCount;
    private int mReportCount;
    private List<CommentResponse> mComments;
    private List<LikeResponse> mLikes;

    public ModelFeatures asModelFeatures() {
        ModelFeatures.Builder builder = new ModelFeatures.Builder();

        builder.add(TYPE, TYPE_POST);

        builder.add(ID, mId);
        builder.add(CONTENT, mContent);
        builder.add(CREATED_TIME, mCreatedTime);
        builder.add(VIEWS, mViewCount);
        builder.add(LIKES, mLikeCount);
        builder.add(COMMENTS, commentsToModelFeatures());

        return builder.build();
    }

    private List<ModelFeatures> commentsToModelFeatures() {
        List<ModelFeatures> comments = new ArrayList<>(mComments == null ? 0 : mComments.size());

        if (mComments != null) {
            for (CommentResponse comment: mComments) {
                comments.add(comment.asModelFeatures());
            }
        }

        return comments;
    }

}
