package com.opentalkz.notification.post;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Looper;

import com.opentalkz.TTMOffice;
import com.opentalkz.network.PostResponse;
import com.opentalkz.network.RandomPostsResponse;
import com.opentalkz.network.Requests;
import com.yashoid.mmv.Model;
import com.yashoid.mmv.ModelFeatures;
import com.yashoid.mmv.SingleShotTarget;
import com.yashoid.network.RequestResponse;
import com.yashoid.network.RequestResponseCallback;

import java.util.ArrayList;
import java.util.List;

public class RandomPostBox {

    private static final int NUMBER_OF_POSTS_TO_GET = 10;
    private static final int MINIMUM_POSTS_TO_REFRESH = 3;

    public interface NextPostCallback {

        void onNextPostResult(Model post, Object... args);

    }

    private static RandomPostBox sInstance = null;

    public static RandomPostBox getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new RandomPostBox(context.getApplicationContext());
        }

        return sInstance;
    }

    private Context mContext;

    private List<ModelFeatures> mPosts = new ArrayList<>();

    private boolean mIsGettingPosts = false;

    private NextPostCallback mLastCallback = null;
    private Object[] mLastArgs = null;

    private RandomPostBox(Context context) {
        mContext = context;
    }

    synchronized public void next(final NextPostCallback callback, final Object... args) {
        if (mPosts.isEmpty()) {
            ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);

            if (cm != null) {
                NetworkInfo ni = cm.getActiveNetworkInfo();

                if (ni == null || !ni.isConnected()) {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {

                        @Override
                        public void run() {
                            callback.onNextPostResult(null, args);
                        }

                    });

                    return;
                }
            }

            mLastCallback = callback;
            mLastArgs = args;

            getPosts();
        }
        else {
            SingleShotTarget.get(mPosts.remove(0), new SingleShotTarget.ModelCallback() {

                @Override
                public void onModelReady(Model model) {
                    callback.onNextPostResult(model, args);
                }

            });

            if (mPosts.size() <= MINIMUM_POSTS_TO_REFRESH) {
                getPosts();
            }
        }
    }

    synchronized private void getPosts() {
        if (mIsGettingPosts) {
            return;
        }

        mIsGettingPosts = true;

        TTMOffice.runner(mContext).runInBackground(
                Requests.randomPosts(NUMBER_OF_POSTS_TO_GET, "fa", "IR", null),
                new RequestResponseCallback<RandomPostsResponse>() {

                    @Override
                    public void onRequestResponse(RequestResponse<RandomPostsResponse> response) {
                        if (response.isSuccessful()) {
                            List<PostResponse> postsResponse = response.getContent().getPosts();

                            for (PostResponse postResponse: postsResponse) {
                                mPosts.add(postResponse.asModelFeatures());
                            }
                        }

                        mIsGettingPosts = false;

                        if (mLastCallback != null) {
                            final NextPostCallback callback = mLastCallback;
                            final Object[] args = mLastArgs;

                            mLastCallback = null;
                            mLastArgs = null;

                            if (mPosts.size() > 0) {
                                SingleShotTarget.get(mPosts.remove(0), new SingleShotTarget.ModelCallback() {

                                    @Override
                                    public void onModelReady(Model model) {
                                        callback.onNextPostResult(model, args);
                                    }

                                });
                            }
                            else {
                                callback.onNextPostResult(null, args);
                            }
                        }
                    }

                });
    }

}
