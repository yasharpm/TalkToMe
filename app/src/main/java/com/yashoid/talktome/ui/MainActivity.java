package com.yashoid.talktome.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;

import com.yashoid.mmv.ModelFeatures;
import com.yashoid.talktome.post.Post;
import com.yashoid.talktome.post.PostItemView;
import com.yashoid.talktome.view.LoadableContentView;
import com.yashoid.talktome.R;
import com.yashoid.talktome.view.ViewBunch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private LoadableContentView mLoadableContent;
    private ViewBunch mViewBunch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLoadableContent = findViewById(R.id.loadableContent);
        mLoadableContent.setOnStateChangedListener(new LoadableContentView.OnStateChangedListener() {
            @Override
            public void onStateChanged(int state) {
                if (state == LoadableContentView.STATE_LOADING) {
                    mViewBunch.load(new ViewBunch.OnLoadCallback() {
                        @Override
                        public void onLoadResult(boolean success) {
                            mLoadableContent.stopLoading();
                        }
                    });
                }
            }
        });

        mViewBunch = findViewById(R.id.viewbunch);

        mViewBunch.setAdapter(new ViewBunch.ViewBunchAdapter() {
            @Override
            public void getBunch(final int count, final ViewBunch.BunchCallback callback) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        List<ModelFeatures> bunch = new ArrayList<>();

                        for (int i = 0; i < count; i++) {
                            ModelFeatures post = new ModelFeatures.Builder()
                                    .add(Post.ID, i)
                                    .add(Post.CONTENT, "این یک آزمایش واقعی به زبان سلیس فارسی میباشد \nکه میتواند خطاهای موجود را \nنمایان سازد!")
                                    .build();

                            bunch.add(post);
                        }

                        callback.onBunchResult(true, bunch);
                    }
                }, 1000);
            }

            @Override
            public ViewBunch.ViewBunchItem createItem() {
                return new PostItemView(MainActivity.this);
            }
        });

        mLoadableContent.startLoading();
    }

}
