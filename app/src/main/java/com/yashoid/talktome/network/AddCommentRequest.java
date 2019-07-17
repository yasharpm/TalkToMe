package com.yashoid.talktome.network;

import android.content.Context;

import com.yashoid.network.request.JSONObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;

/*	function	ADD COMMENT
		headers:	request:add_comment, adu_id, device_id, app_version
		method:		POST
		body:		JSON: note_id, comment
		response:	JSON: RESULT(RESULT_OK|RESULT_USER_NOT_FOUND) */
public class AddCommentRequest extends JSONObjectRequest implements ApiConstants {

    private static final String NOTE_ID = "note_id";
    private static final String COMMENT = "comment";

    public AddCommentRequest(String noteId, String comment, Context context) throws IOException {
        super(API_URL);

        HttpURLConnection connection = getConnection();

        connection.addRequestProperty(REQUEST, REQUEST_ADD_COMMENT);
        connection.addRequestProperty(ADU_ID, Api.get(context).getAduId());
        connection.addRequestProperty(DEVICE_ID, Api.get(context).getDeviceId());
        connection.addRequestProperty(APP_VERSION, "" + API_LEVEL_VALUE);

        JSONObject body = new JSONObject();

        try {
            body.put(NOTE_ID, noteId);
            body.put(COMMENT, comment);
        } catch (JSONException e) { }

        setBody(body);
    }

}
