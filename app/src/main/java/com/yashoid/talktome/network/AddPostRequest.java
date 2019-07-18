package com.yashoid.talktome.network;

import android.content.Context;

import com.yashoid.network.request.JSONObjectRequest;

import java.io.IOException;
import java.net.HttpURLConnection;

/*	function:	ADD NOTE
    headers:	request:add_note, adu_id, device_id, app_version
    method:		POST
    body:		note
    response:	JSON: RESULT(RESULT_OK|RESULT_USER_NOT_FOUND), note_id */
public class AddPostRequest extends JSONObjectRequest implements ApiConstants {

    public AddPostRequest(Context context, String content) throws IOException {
        super(API_URL);

        HttpURLConnection connection = getConnection();

        connection.addRequestProperty(REQUEST, REQUEST_ADD_NOTE);
        connection.addRequestProperty(ADU_ID, Api.get(context).getAduId());
        connection.addRequestProperty(DEVICE_ID, Api.get(context).getDeviceId());
        connection.addRequestProperty(APP_VERSION, "" + API_LEVEL_VALUE);

        setBody(content);
    }

}
