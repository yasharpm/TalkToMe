package com.yashoid.talktome.network.postupdate;

public abstract class RemovedPostUpdate implements PostUpdate {

    @Override
    public int getType() {
        return TYPE_REMOVE;
    }

    abstract public String getPostId();

}
