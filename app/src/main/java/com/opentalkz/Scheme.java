package com.opentalkz;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.opentalkz.model.community.AddCommunityActivity;
import com.opentalkz.model.community.Community;
import com.opentalkz.model.post.Post;
import com.opentalkz.model.post.PostList;
import com.opentalkz.model.post.PostListPagerFragment;
import com.opentalkz.network.CommunityResponse;
import com.opentalkz.network.PostResponse;
import com.opentalkz.network.Requests;
import com.opentalkz.ui.MainActivity;
import com.opentalkz.ui.SettingsActivity;
import com.yashoid.mmv.Managers;
import com.yashoid.mmv.Model;
import com.yashoid.mmv.ModelFeatures;
import com.yashoid.mmv.SingleShotTarget;
import com.yashoid.network.RequestResponse;
import com.yashoid.network.RequestResponseCallback;

import java.util.ArrayList;
import java.util.List;

public class Scheme {

    private static final String TAG = "Scheme";

    private static final String EXTRA_URI = "uri";

    private static final String SCHEME = "opentalkz";

    private static final String HOST_COMMUNITY = "community";
    private static final String HOST_SETTINGS = "settings";

    private static final int TYPE_UNIDENTIFIED = -1;
    private static final int TYPE_SETTINGS = 0;
    private static final int TYPE_POST = 1;
    private static final int TYPE_COMMUNITY = 2;

    private static boolean sLastRequestSuccessful = false;

    public static void extrasToUri(Intent intent) {
        if (intent.getData() != null) {
            return;
        }

        if (intent.hasExtra(EXTRA_URI)) {
            Uri uri = Uri.parse(intent.getStringExtra(EXTRA_URI));

            intent.removeExtra(EXTRA_URI);
            intent.setData(uri);
        }
    }

    public static Uri makePostUri(String postId) {
        return Uri.parse("https://opentalkz.com/post.html?id=" + postId);
    }

    private static int getType(Uri uri) {
        if (uri == null || TextUtils.isEmpty(uri.getScheme())) {
            return TYPE_UNIDENTIFIED;
        }

        if (SCHEME.equalsIgnoreCase(uri.getScheme())) {
            if (HOST_SETTINGS.equalsIgnoreCase(uri.getHost())) {
                return TYPE_SETTINGS;
            }
            else if (HOST_COMMUNITY.equalsIgnoreCase(uri.getHost())) {
                return TYPE_COMMUNITY;
            }

            return TYPE_UNIDENTIFIED;
        }

        String query = uri.getQueryParameter("id");

        return TextUtils.isEmpty(query) ? TYPE_UNIDENTIFIED : TYPE_POST;
    }

    public static boolean canHandle(Uri uri) {
        return getType(uri) != TYPE_UNIDENTIFIED;
    }

    public static void prepareForUri(final Activity activity, Uri uri, final Runnable action) {
        int type = getType(uri);

        switch (type) {
            case TYPE_POST:
                sLastRequestSuccessful = false;

                String postId = uri.getQueryParameter("id");

                TTMOffice.runner(activity).runForUI(Requests.getPost(postId), new RequestResponseCallback<PostResponse>() {

                    @Override
                    public void onRequestResponse(RequestResponse<PostResponse> response) {
                        if (response.isSuccessful()) {
                            sLastRequestSuccessful = true;

                            ModelFeatures postFeatures = response.getContent().asModelFeatures();

                            Managers.registerModel(postFeatures);
                        }
                        else {
                            sLastRequestSuccessful = false;

                            Log.i(TAG, "Failed to get post from server.");

                            Toast.makeText(activity, response.getResponseCode() == 404 ? R.string.scheme_postnotfound : R.string.scheme_gettingpostfailed, Toast.LENGTH_LONG).show();
                        }

                        activity.runOnUiThread(action);
                    }

                });
                return;
            case TYPE_COMMUNITY:
                sLastRequestSuccessful = false;

                String communityId = uri.getQueryParameter("id");

                TTMOffice.runner(activity).runForUI(Requests.getCommunity(communityId), new RequestResponseCallback<CommunityResponse>() {

                    @Override
                    public void onRequestResponse(RequestResponse<CommunityResponse> response) {
                        if (response.isSuccessful()) {
                            sLastRequestSuccessful = true;

                            ModelFeatures communityFeatures = response.getContent().asModelFeatures();

                            Managers.registerModel(communityFeatures);
                        }
                        else {
                            sLastRequestSuccessful = false;

                            Log.i(TAG, "Failed to get community from server.");

                            Toast.makeText(activity, response.getResponseCode() == 404 ? R.string.scheme_communitynotfound : R.string.scheme_gettingcommunityfailed, Toast.LENGTH_LONG).show();
                        }

                        activity.runOnUiThread(action);
                    }

                });
                return;
            case TYPE_SETTINGS:
            case TYPE_UNIDENTIFIED:
                activity.runOnUiThread(action);
                return;
        }


    }

    public static void handleUri(final MainActivity activity, Uri uri) {
        int type = getType(uri);

        switch (type) {
            case TYPE_UNIDENTIFIED:
                return;
            case TYPE_SETTINGS:
                activity.startActivity(SettingsActivity.getIntent(activity));
                return;
            case TYPE_POST:
                if (!sLastRequestSuccessful) {
                    return;
                }

                final String postId = uri.getQueryParameter("id");

                final ModelFeatures postsFeatures = new ModelFeatures.Builder()
                        .add(PostList.TYPE, PostList.TYPE_POST_LIST)
                        .build();

                SingleShotTarget.get(postsFeatures, new SingleShotTarget.ModelCallback() {

                    @Override
                    public void onModelReady(Model model) {
                        ModelFeatures postFeatures = new ModelFeatures.Builder()
                                .add(Post.TYPE, Post.TYPE_POST)
                                .add(Post.ID, postId)
                                .build();

                        List<ModelFeatures> posts = new ArrayList<>();
                        posts.add(postFeatures);

                        model.set(PostList.MODEL_LIST, posts);

                        Fragment fragment = PostListPagerFragment.newInstance(postsFeatures, 1, 0);

                        activity.addOnOverlay(fragment);
                    }

                });
                return;
            case TYPE_COMMUNITY:
                if (!sLastRequestSuccessful) {
                    return;
                }

                final String communityId = uri.getQueryParameter("id");

                ModelFeatures communityFeatures = new ModelFeatures.Builder()
                        .add(Community.TYPE, Community.TYPE_COMMUNITY)
                        .add(Community.ID, communityId)
                        .build();

                activity.startActivity(AddCommunityActivity.getIntent(activity, communityFeatures));
                return;
        }
    }

}
