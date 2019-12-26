package com.yashoid.talktome;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class Share {

    private static final String SHARE_URL = "http://opentalkz.com/post/";

    public static Uri getShareUri(String postId) {
        return Uri.parse(SHARE_URL + postId);
    }

    public static void performShareAction(String postId, String content, Context context) {
        Uri shareUri = Share.getShareUri(postId);

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");

        String shareMessage = '"' + content + "\"\n" + context.getString(R.string.share_message) + "\n" + shareUri;

        intent.putExtra(Intent.EXTRA_TEXT, shareMessage);

        intent = Intent.createChooser(intent, context.getString(R.string.share_title));

        context.startActivity(intent);
    }

}
