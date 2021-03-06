package com.opentalkz.network;

import java.util.List;

public class SyncResponse {

    private String mUpdateToken;
    private boolean mSyncCompleted;
    private List<Change> mChanges;

    public String getUpdateToken() {
        return mUpdateToken;
    }

    public boolean isSyncCompleted() {
        return mSyncCompleted;
    }

    public List<Change> getChanges() {
        return mChanges;
    }

    public static class Change {

        public static final String EVENT_NEW_POST = "new_post";
        public static final String EVENT_NEW_VIEWS = "new_views";
        public static final String EVENT_NEW_COMMENT = "new_comment";
        public static final String EVENT_NEW_LIKE = "new_like";
        public static final String EVENT_REMOVED_LIKE = "removed_like";

        private String mEventType;
        private long mCreatedTime;
        private CombinedObject mContent;

        public String getEventType() {
            return mEventType;
        }

        public CombinedObject getContent() {
            return mContent;
        }

    }

    public static class CombinedObject {

        String id;
        long createdTime;
        long updatedTime;

        String userId;
        public String content;
        String language;
        String country;
        int commentCount;
        int likeCount;
        int viewCount;
        int reportCount;
        List<CommentResponse> comments;
        List<LikeResponse> likes;

        public String postId;
        int views;

    }

}
