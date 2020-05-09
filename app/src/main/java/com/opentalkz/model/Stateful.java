package com.opentalkz.model;

public interface Stateful {

    int STATE_IDLE = 0;
    int STATE_LOADING = 1;
    int STATE_SUCCESS = 2;
    int STATE_FAILURE = 3;

    String STATE = "state";

}
