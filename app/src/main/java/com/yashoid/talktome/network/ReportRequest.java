package com.yashoid.talktome.network;

import android.content.Context;

import com.yashoid.network.request.JSONObjectRequest;

import java.io.IOException;
import java.net.HttpURLConnection;

/*	function	REPORT NOTE
    headers:	request:report_note, adu_id, device_id, app_version
    method:		GET
    params:		note_id
    response:	JSON: RESULT(RESULT_OK|RESULT_USER_NOT_FOUND)
*/
public class ReportRequest extends JSONObjectRequest implements ApiConstants {

    private static final String NOTE_ID = "note_id";

    public ReportRequest(Context context, String noteId) throws IOException {
        super(API_URL + "?" + NOTE_ID + "=" + noteId);

        HttpURLConnection connection = getConnection();

        connection.addRequestProperty(REQUEST, REQUEST_REPORT);
        connection.addRequestProperty(ADU_ID, Api.get(context).getAduId());
        connection.addRequestProperty(DEVICE_ID, Api.get(context).getDeviceId());
        connection.addRequestProperty(APP_VERSION, "" + API_LEVEL_VALUE);
    }

}
