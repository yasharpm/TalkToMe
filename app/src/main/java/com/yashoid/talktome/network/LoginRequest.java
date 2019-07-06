package com.yashoid.talktome.network;

import android.content.Context;

import com.yashoid.network.request.JSONObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/*

    POST http://www.yashoid.com/projects/talktome/operations/api.php
    request : login
    {
        device_id : ...
        device_model : Build.MODEL
        api_level : android.os.Build.VERSION.SDK_INT
        app_version : ...
    }
    {"result":"ok","adu_id":"138986","user_id":"29643"}

 */
public class LoginRequest extends JSONObjectRequest implements ApiConstants {

    public LoginRequest(Context context) throws IOException {
        super(API_URL);

        getConnection().addRequestProperty(REQUEST, REQUEST_LOGIN);

        JSONObject body = new JSONObject();

        try {
            body.put(DEVICE_ID, Api.get(context).getDeviceId());
            body.put(DEVICE_MODEL, DEVICE_MODEL_VALUE);
            body.put(API_LEVEL, API_LEVEL_VALUE);
            body.put(APP_VERSION, APP_VERSION_VALUE);
        } catch (JSONException e) { }

        setBody(body);
    }

}
