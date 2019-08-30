package com.yashoid.talktome;

import com.yashoid.network.NetworkOperator;
import com.yashoid.office.task.TaskManager;
import com.yashoid.office.task.TaskManagerBuilder;

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
            mNetworkOperator = new NetworkOperator(mTaskManager);
        }

        return mTaskManager;
    }

    public static NetworkOperator network() {
        get();

        return mNetworkOperator;
    }

}
