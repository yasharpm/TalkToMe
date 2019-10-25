package com.yashoid.talktome.network;

public class RegisterResponse {

    private String mUserId;
    private String mRefreshToken;
    private String mToken;
    private int mTokenLife;

    public String getUserId() {
        return mUserId;
    }

    public String getRefreshToken() {
        return mRefreshToken;
    }

    public String getToken() {
        return mToken;
    }

    public int getTokenLife() {
        return mTokenLife;
    }

}
