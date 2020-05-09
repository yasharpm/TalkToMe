package com.opentalkz.network;

import android.util.SparseArray;

import com.yashoid.network.NetworkRequest;
import com.yashoid.network.PreparedRequest;
import com.opentalkz.TTMOffice;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.UUID;

public class Requests {

    private static final String BASE_URL = "http://opentalkz.com/api/v1/";

    private static final int REGISTER = 0;
    private static final int GET_TOKEN = 1;
    private static final int NEW_POST = 2;
    private static final int RANDOM_POSTS = 3;
    private static final int GET_POST = 4;
    private static final int MY_POSTS = 5;
    private static final int SEEN_POSTS = 6;
    private static final int NEW_COMMENT = 7;
    private static final int LIKE_OR_DISLIKE = 8;
    private static final int REPORT = 9;
    private static final int SYNC = 10;
    private static final int UPDATE_FCM_TOKEN = 11;

    private static SparseArray<NetworkRequest> sRequests = new SparseArray<>();

    public static PreparedRequest<RegisterResponse> register() {
        NetworkRequest request = sRequests.get(REGISTER);

        if (request == null) {
            String password = UUID.randomUUID().toString();

            JSONObject body = new JSONObject();

            try {
                body.put("password", password);
            } catch (JSONException e) { }

            request = TTMOffice.network().getRequest(
                    RegisterResponse.class, "POST", BASE_URL + "user/register", body);

            sRequests.put(REGISTER, request);
        }

        return (PreparedRequest<RegisterResponse>) request.prepare();
    }

    public static PreparedRequest<GetTokenResponse> getToken(String userId, String refreshToken) {
        NetworkRequest request = sRequests.get(GET_TOKEN);

        if (request == null) {
            request = TTMOffice.network().getRequest(
                    GetTokenResponse.class, "POST", BASE_URL + "user/refreshToken");

            sRequests.put(GET_TOKEN, request);
        }

        JSONObject body = new JSONObject();

        try {
            body.put("userId", userId);
            body.put("refreshToken", refreshToken);
        } catch (JSONException e) { }

        return (PreparedRequest<GetTokenResponse>) request.prepare(body);
    }

    public static PreparedRequest<PostResponse> newPost(String content, String language, String country) {
        NetworkRequest request = sRequests.get(NEW_POST);

        if (request == null) {
            request = TTMOffice.network().getRequest(
                    PostResponse.class, "POST", BASE_URL + "post");

            sRequests.put(NEW_POST, request);
        }

        JSONObject body = new JSONObject();

        try {
            body.put("post", content);
            body.put("language", language);
            body.put("country", country);
        } catch (JSONException e) { }

        return (PreparedRequest<PostResponse>) request.prepare(body);
    }

    public static PreparedRequest<RandomPostsResponse> randomPosts(int count, String language, String country) {
        NetworkRequest request = sRequests.get(RANDOM_POSTS);

        if (request == null) {
            request = TTMOffice.network().getRequest(
                    RandomPostsResponse.class,
                    "GET",
                    BASE_URL + "post/random?count={count}&language={lang}&country={country}"
            );

            sRequests.put(RANDOM_POSTS, request);
        }

        return (PreparedRequest<RandomPostsResponse>) request.prepare("count", count, "lang", language, "country", country);
    }

    public static PreparedRequest<PostResponse> getPost(String postId) {
        NetworkRequest request = sRequests.get(GET_POST);

        if (request == null) {
            request = TTMOffice.network().getRequest(
                    PostResponse.class,
                    "GET",
                    BASE_URL + "post?id={id}"
            );

            sRequests.put(GET_POST, request);
        }

        return (PreparedRequest<PostResponse>) request.prepare("id", postId);
    }

    public static PreparedRequest<PostListResponse> myPosts(int count, int offset) {
        NetworkRequest request = sRequests.get(MY_POSTS);

        if (request == null) {
            request = TTMOffice.network().getRequest(
                    PostListResponse.class,
                    "GET",
                    BASE_URL + "post/myPosts?count={count}&offset={offset}"
            );

            sRequests.put(MY_POSTS, request);
        }

        return (PreparedRequest<PostListResponse>) request.prepare("count", count, "offset", offset);
    }

    public static PreparedRequest<SeenPostsResponse> seenPosts(List<String> postIds) {
        NetworkRequest request = sRequests.get(SEEN_POSTS);

        if (request == null) {
            request = TTMOffice.network().getRequest(
                    SeenPostsResponse.class,
                    "POST",
                    BASE_URL + "post/seen"
            );

            sRequests.put(SEEN_POSTS, request);
        }

        JSONObject body = new JSONObject();

        try {
            JSONArray jIds = new JSONArray();

            for (String id: postIds) {
                jIds.put(id);
            }

            body.put("postIds", jIds);
        } catch (JSONException e) { }

        return (PreparedRequest<SeenPostsResponse>) request.prepare(body);
    }

    public static PreparedRequest<CommentResponse> newComment(String postId, String content) {
        NetworkRequest request = sRequests.get(NEW_COMMENT);

        if (request == null) {
            request = TTMOffice.network().getRequest(
                    CommentResponse.class,
                    "POST",
                    BASE_URL + "comment"
            );

            sRequests.put(NEW_COMMENT, request);
        }

        JSONObject body = new JSONObject();

        try {
            body.put("postId", postId);
            body.put("comment", content);
        } catch (JSONException e) { }

        return (PreparedRequest<CommentResponse>) request.prepare(body);
    }

    public static PreparedRequest<LikeResponse> likeOrDislike(String postId, boolean like) {
        NetworkRequest request = sRequests.get(LIKE_OR_DISLIKE);

        if (request == null) {
            request = TTMOffice.network().getRequest(
                    LikeResponse.class,
                    "POST",
                    BASE_URL + "like"
            );

            sRequests.put(LIKE_OR_DISLIKE, request);
        }

        JSONObject body = new JSONObject();

        try {
            body.put("postId", postId);
            body.put("like", like);
        } catch (JSONException e) { }

        return (PreparedRequest<LikeResponse>) request.prepare(body);
    }

    public static PreparedRequest<ReportResponse> report(String postId, int reason, String description) {
        return report(postId, null, reason, description);
    }

    public static PreparedRequest<ReportResponse> report(String postId, String commentId, int reason, String description) {
        NetworkRequest request = sRequests.get(REPORT);

        if (request == null) {
            request = TTMOffice.network().getRequest(
                    ReportResponse.class,
                    "POST",
                    BASE_URL + "report"
            );

            sRequests.put(REPORT, request);
        }

        JSONObject body = new JSONObject();

        try {
            body.put("postId", postId);
            body.put("commentId", commentId);
            body.put("reason", reason);
            body.put("description", description);
        } catch (JSONException e) { }

        return (PreparedRequest<ReportResponse>) request.prepare(body);
    }

    public static PreparedRequest<SyncResponse> sync(String updateToken) {
        NetworkRequest request = sRequests.get(SYNC);

        if (request == null) {
            request = TTMOffice.network().getRequest(
                    SyncResponse.class,
                    "GET",
                    BASE_URL + "sync"
            );

            sRequests.put(SYNC, request);
        }
        return (PreparedRequest<SyncResponse>) request.prepare("updateToken", updateToken);
    }

    public static PreparedRequest<BaseResponse> updateFCMToken(String token) {
        NetworkRequest request = sRequests.get(UPDATE_FCM_TOKEN);

        if (request == null) {
            request = TTMOffice.network().getRequest(
                    BaseResponse.class,
                    "POST",
                    BASE_URL + "user/updateFCMToken"
            );

            sRequests.put(UPDATE_FCM_TOKEN, request);
        }

        JSONObject body = new JSONObject();

        try {
            body.put("token", token);
        } catch (JSONException e) { }

        return (PreparedRequest<BaseResponse>) request.prepare(body);
    }


}
