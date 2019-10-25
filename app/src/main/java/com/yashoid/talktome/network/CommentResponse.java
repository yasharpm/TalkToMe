package com.yashoid.talktome.network;

import com.yashoid.mmv.ModelFeatures;
import com.yashoid.talktome.model.comment.Comment;

public class CommentResponse implements Comment {

    private String mUserId;
    private String mPostId;
    private String mContent;
    private long mCreatedTime;
    private long mUpdatedTime;
    private String mId;

    public ModelFeatures asModelFeatures() {
        return new ModelFeatures.Builder()
                .add(TYPE, TYPE_COMMENT)
                .add(ID, mId)
                .add(CONTENT, mContent)
                .build();
    }

}
