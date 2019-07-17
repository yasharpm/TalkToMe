package com.yashoid.talktome.network;

import android.os.Build;

public interface ApiConstants {

    String API_URL = "http://www.yashoid.com/projects/talktome/operations/api.php";

    String DEVICE_ID = "device_id";
    String DEVICE_MODEL = "device_model";
    String API_LEVEL = "api_level";
    String APP_VERSION = "app_version";
    String ADU_ID = "adu_id";

    String DEVICE_MODEL_VALUE = Build.MODEL;
    int API_LEVEL_VALUE = Build.VERSION.SDK_INT;
    int APP_VERSION_VALUE = 22;

    String REQUEST = "request";
    String REQUEST_LOGIN = "login";
    String REQUEST_GET_NOTE = "get_note";
    String REQUEST_GET_COMMENTS = "get_comments";
    String REQUEST_ADD_COMMENT = "add_comment";

    String RESULT = "result";
    String RESULT_OK = "ok";

}
