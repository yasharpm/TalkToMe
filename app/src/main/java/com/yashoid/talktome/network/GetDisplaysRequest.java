package com.yashoid.talktome.network;

import android.content.Context;

import com.yashoid.network.request.JSONObjectRequest;

import org.json.JSONArray;

import java.io.IOException;
import java.net.HttpURLConnection;

/*	function	GET DISPLAYS
    headers:	request:get_displays, adu_id, device_id, app_version
    method:		POST
    body:		JSON: [note_id]
    reponse:	JSON: RESLUT(RESULT_OK|RESULT_USER_NOT_FOUND), displays JSON: [note_id, note_displays] */
public class GetDisplaysRequest extends JSONObjectRequest implements ApiConstants {

    public GetDisplaysRequest(Context context, String[] postIds) throws IOException {
        super(API_URL);

        HttpURLConnection connection = getConnection();

        connection.addRequestProperty(REQUEST, REQUEST_GET_DISPLAYS);
        connection.addRequestProperty(ADU_ID, Api.get(context).getAduId());
        connection.addRequestProperty(DEVICE_ID, Api.get(context).getDeviceId());
        connection.addRequestProperty(APP_VERSION, "" + API_LEVEL_VALUE);

        JSONArray body = new JSONArray();

        for (String postId: postIds) {
            body.put(postId);
        }

        setBody(body);
    }

}
