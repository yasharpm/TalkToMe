package com.opentalkz;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

import com.opentalkz.ui.SettingsActivity;

import java.util.Locale;

public class Scheme {

    private static final String EXTRA_URI = "uri";

    private static final String SCHEME = "opentalkz";

    private static final String HOST_SETTINGS = "settings";

    public static void extrasToUri(Intent intent) {
        if (intent.getData() != null) {
            return;
        }

        if (intent.hasExtra(EXTRA_URI)) {
            Uri uri = Uri.parse(intent.getStringExtra(EXTRA_URI));

            intent.removeExtra(EXTRA_URI);
            intent.setData(uri);
        }
    }

    public static boolean canHandle(Uri uri) {
        return uri != null && uri.getScheme() != null && SCHEME.equals(uri.getScheme().toLowerCase(Locale.US));
    }

    public static void handleUri(Activity activity, Uri uri) {
        if (!canHandle(uri)) {
            return;
        }

        if (HOST_SETTINGS.equals(uri.getHost())) {
            activity.startActivity(SettingsActivity.getIntent(activity));
            return;
        }
    }

}
