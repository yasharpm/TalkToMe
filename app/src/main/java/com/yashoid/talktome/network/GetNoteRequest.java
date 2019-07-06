package com.yashoid.talktome.network;

import android.content.Context;

import com.yashoid.network.request.JSONObjectRequest;

import java.io.IOException;
import java.net.HttpURLConnection;

/*
    GET http://www.yashoid.com/projects/talktome/operations/api.php
    request : get_note
    adu_id :
    device_id : Build.FINGERPRINT + " id:" + Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
    app_version : 22 + alpha

    {"result":"ok","type":"note","note_id":"62060","note_user":"15287","note_note":"STUFF","note_time":"2015-01-13 23:24:32","note_displays":118}
 */
public class GetNoteRequest extends JSONObjectRequest implements ApiConstants {

    public GetNoteRequest(Context context) throws IOException {
        super(API_URL);

        HttpURLConnection connection = getConnection();

        connection.addRequestProperty(REQUEST, REQUEST_GET_NOTE);
        connection.addRequestProperty(ADU_ID, Api.get(context).getAduId());
        connection.addRequestProperty(DEVICE_ID, Api.get(context).getDeviceId());
        connection.addRequestProperty(APP_VERSION, "" + API_LEVEL_VALUE);
    }

}
