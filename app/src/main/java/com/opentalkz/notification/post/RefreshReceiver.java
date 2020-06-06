package com.opentalkz.notification.post;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import java.lang.reflect.Method;

public class RefreshReceiver extends BroadcastReceiver {

    private static final String TAG = "RefreshReceiver";

    private static final String CALL_INTENT = "call_intent";

    public static Intent getIntent(Context context, String className) {
        Intent intent = new Intent(context, RefreshReceiver.class);

        intent.setData(Uri.parse("opentalkz://refresh/" + className));

        return intent;
    }

    public static Intent getIntent(Context context, Intent callIntent) {
        Intent intent = new Intent(context, RefreshReceiver.class);

        intent.setData(Uri.parse("opentalkz://refresh/" + CALL_INTENT + callIntent.hashCode()));

        intent.putExtra(CALL_INTENT, callIntent);

        return intent;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Uri data = intent.getData();
        String lastPathSegment = data.getLastPathSegment();

        if (lastPathSegment.startsWith(CALL_INTENT)) {
            Intent callIntent = intent.getParcelableExtra(CALL_INTENT);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(callIntent);
            }
            else {
                context.startService(callIntent);
            }

            return;
        }

        try {
            String className = lastPathSegment;

            Class clazz = RefreshReceiver.class.getClassLoader().loadClass(className);

            Method enqueueMethod = clazz.getMethod("enqueueWork", Context.class);

            enqueueMethod.invoke(null, context);
        } catch (Throwable t) {
            Log.e(TAG, "Failed to call refresh.", t);
        }
    }

}
