package com.yashoid.talktome.network;

import android.content.Context;

import com.yashoid.network.request.JSONObjectRequest;

import java.io.IOException;
import java.net.HttpURLConnection;

/*	function	GET COMMENTS
    headers:	request:get_comments, adu_id, device_id, app_version
    method:		GET
    params:		note_id
    response:	JSON: RESULT(RESULT_OK|RESULT_USER_NOT_FOUND), comments JSON: comment_id, comment_user, comment_comment, comment_time */
public class GetCommentsRequest extends JSONObjectRequest implements ApiConstants {

    private static final String NOTE_ID = "note_id";

    public GetCommentsRequest(Context context, String noteId) throws IOException {
        super(API_URL + "?" + NOTE_ID + "=" + noteId);

        HttpURLConnection connection = getConnection();

        connection.addRequestProperty(REQUEST, REQUEST_GET_COMMENTS);
        connection.addRequestProperty(ADU_ID, Api.get(context).getAduId());
        connection.addRequestProperty(DEVICE_ID, Api.get(context).getDeviceId());
        connection.addRequestProperty(APP_VERSION, "" + API_LEVEL_VALUE);
    }

}
