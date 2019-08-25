package com.yashoid.talktome.network.postupdate;

public interface PostUpdate {

    int TYPE_NEW = 0;
    int TYPE_CHANGE = 1;
    int TYPE_REMOVE = 2;

    int getType();

}
