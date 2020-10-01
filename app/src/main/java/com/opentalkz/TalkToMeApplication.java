package com.opentalkz;

import android.app.Application;

import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.opentalkz.model.community.Community;
import com.opentalkz.model.community.CommunityList;
import com.opentalkz.model.post.UserPostList;
import com.yashoid.mmv.Managers;
import com.opentalkz.evaluation.Eval;
import com.opentalkz.evaluation.Events;
import com.opentalkz.model.comment.Comment;
import com.opentalkz.model.comment.CommentList;
import com.opentalkz.model.pendingpost.PendingPost;
import com.opentalkz.model.post.MyPostList;
import com.opentalkz.model.post.Post;
import com.opentalkz.model.post.PostList;
import com.opentalkz.model.post.RandomPostList;
import com.opentalkz.notification.Notifier;
import com.opentalkz.notification.PushUtils;

public class TalkToMeApplication extends Application implements Events {

    private static final String TAG = "TalkToMeApplication";

    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(!BuildConfig.DEBUG);

        Managers.enableCache(this, TTMOffice.get());
        Managers.bindLifeCycle(this);

        Managers.addTypeProvider(new Community.CommunityTypeProvider());
        Managers.addTypeProvider(new CommunityList.CommunityListTypeProvider());
        Managers.addTypeProvider(new PostList.PostListTypeProvider());
        Managers.addTypeProvider(new RandomPostList.RandomPostListTypeProvider(this));
        Managers.addTypeProvider(new Post.PostTypeProvider(this));
        Managers.addTypeProvider(new CommentList.CommentListTypeProvider(this));
        Managers.addTypeProvider(new Comment.CommentTypeProvider(this));
        Managers.addTypeProvider(new PendingPost.PendingPostTypeProvider(this));
        Managers.addTypeProvider(new MyPostList.MyPostListTypeProvider(this));
        Managers.addTypeProvider(new UserPostList.UserPostListTypeProvider(this));

        Managers.registerModel(MyPostList.FEATURES);

        Eval.initialize(this);

        setupUncaughtExceptionTracker();

        PushUtils.verifyFCMToken(this);

        Notifier.init(this);
    }

    private void setupUncaughtExceptionTracker() {
        Thread.UncaughtExceptionHandler defaultHandler = Thread.getDefaultUncaughtExceptionHandler();

        if (!(defaultHandler instanceof TTMUncaughtExceptionHandler)) {
            Thread.setDefaultUncaughtExceptionHandler(new TTMUncaughtExceptionHandler(defaultHandler));
        }
    }

    private static class TTMUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {

        private Thread.UncaughtExceptionHandler mDefaultHandler;

        TTMUncaughtExceptionHandler(Thread.UncaughtExceptionHandler defaultHandler) {
            mDefaultHandler = defaultHandler;
        }

        @Override
        public void uncaughtException(Thread t, Throwable e) {
            try {
                Eval.trackEvent(EVENT_CRASHED);
            } catch (Throwable throwable) { }

            mDefaultHandler.uncaughtException(t, e);
        }

    }


}
