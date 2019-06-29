package com.yashoid.talktome;

import android.app.Application;

import com.yashoid.mmv.Managers;
import com.yashoid.talktome.post.PostList;

public class TalkToMeApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Managers.bindLifeCycle(this);

        Managers.addTypeProvider(new PostList.PostListTypeProvider());
    }

}
