package com.yashoid.talktome;

import android.app.Application;

import com.yashoid.mmv.Managers;
import com.yashoid.talktome.model.comment.Comment;
import com.yashoid.talktome.model.comment.CommentList;
import com.yashoid.talktome.model.post.Post;
import com.yashoid.talktome.model.post.PostList;

public class TalkToMeApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Managers.bindLifeCycle(this);

        Managers.addTypeProvider(new PostList.PostListTypeProvider(this));
        Managers.addTypeProvider(new Post.PostTypeProvider(this));
        Managers.addTypeProvider(new CommentList.CommentListTypeProvider());
        Managers.addTypeProvider(new Comment.CommentTypeProvider(this));
    }

}
