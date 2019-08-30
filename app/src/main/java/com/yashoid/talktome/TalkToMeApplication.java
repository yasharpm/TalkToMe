package com.yashoid.talktome;

import android.app.Application;

import com.yashoid.mmv.Managers;
import com.yashoid.talktome.evaluation.Eval;
import com.yashoid.talktome.model.comment.Comment;
import com.yashoid.talktome.model.comment.CommentList;
import com.yashoid.talktome.model.pendingpost.PendingPost;
import com.yashoid.talktome.model.post.MyPostList;
import com.yashoid.talktome.model.post.Post;
import com.yashoid.talktome.model.post.PostList;

public class TalkToMeApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Managers.enableCache(this, TTMOffice.get());
        Managers.bindLifeCycle(this);

        Managers.addTypeProvider(new PostList.PostListTypeProvider(this));
        Managers.addTypeProvider(new Post.PostTypeProvider(this));
        Managers.addTypeProvider(new CommentList.CommentListTypeProvider(this));
        Managers.addTypeProvider(new Comment.CommentTypeProvider(this));
        Managers.addTypeProvider(new PendingPost.PendingPostTypeProvider(this));
        Managers.addTypeProvider(new MyPostList.MyPostListTypeProvider(this));

        Managers.registerModel(MyPostList.FEATURES);

        Eval.initialize(this);
    }

}
