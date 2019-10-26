package com.yashoid.talktome;

import android.content.Context;

import com.yashoid.network.NetworkOperator;
import com.yashoid.office.task.TaskManager;
import com.yashoid.office.task.TaskManagerBuilder;
import com.yashoid.talktome.network.RequestRunner;

public class TTMOffice {

    private static final int DATABASE_READ_WORKERS = 2;
    private static final int DATABASE_WRITE_WORKERS = 1;

    private static TaskManager mTaskManager = null;
    private static NetworkOperator mNetworkOperator = null;

    public static TaskManager get() {
        if (mTaskManager == null) {
            mTaskManager = new TaskManagerBuilder()
                    .addDatabaseReadSection(DATABASE_READ_WORKERS)
                    .addDatabaseReadWriteSection(DATABASE_WRITE_WORKERS)
                    .build();
        }

        if (mNetworkOperator == null) {
            mNetworkOperator = new NetworkOperator.Builder().taskManager(mTaskManager).lowPriorityWorkerCount(2).build();
        }

        return mTaskManager;
    }

    public static NetworkOperator network() {
        get();

        return mNetworkOperator;
    }

    public static RequestRunner runner(Context context) {
        return RequestRunner.get(context);
    }

    public static Preferences preferences(Context context) {
        return Preferences.get(context);
    }

}
