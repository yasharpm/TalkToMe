package com.yashoid.talktome.network;

import com.yashoid.yashson.fieldprovider.ParseName;

import java.util.List;

public class PostListResponse {

    private int mTotalCount;

    private int mCount;

    @ParseName(name = "data", exactMatch = true)
    private List<PostResponse> mPosts;

    public List<PostResponse> getPosts() {
        return mPosts;
    }

}
