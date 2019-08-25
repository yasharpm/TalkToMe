package com.yashoid.talktome.network.postupdate;

import com.yashoid.mmv.ModelFeatures;

public abstract class NewPostUpdate implements PostUpdate {

    @Override
    public int getType() {
        return TYPE_NEW;
    }

    abstract public ModelFeatures getPost();

}
