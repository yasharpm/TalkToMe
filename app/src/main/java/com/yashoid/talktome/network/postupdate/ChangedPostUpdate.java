package com.yashoid.talktome.network.postupdate;

import com.yashoid.mmv.ModelFeatures;

public abstract class ChangedPostUpdate implements PostUpdate {

    @Override
    public int getType() {
        return TYPE_CHANGE;
    }

    abstract public ModelFeatures getChanges();

}
