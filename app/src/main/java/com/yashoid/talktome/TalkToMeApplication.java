package com.yashoid.talktome;

import android.app.Application;

import com.yashoid.mmv.Managers;

public class TalkToMeApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Managers.bindLifeCycle(this);
    }

}
