package com.yashoid.talktome.network;

import java.util.List;
import java.util.Map;

public class SyncResponse {

    private String mUpdateToken;
    private boolean mSyncCompleted;
    private List<Change> mChanges;

    private static class Change {

        private static final String EVENT_NEW_POST = "new_post";
        private static final String EVENT_NEW_VIEWS = "new_views";
        private static final String EVENT_NEW_COMMENT = "new_comment";
        private static final String EVENT_NEW_LIKE = "new_like";
        private static final String EVENT_REMOVED_LIKE = "removed_like";

        private String mEventType;
        private long mCreatedTime;
        private Map<String, Object> mContent;

    }

}
