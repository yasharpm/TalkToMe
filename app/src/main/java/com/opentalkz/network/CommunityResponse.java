package com.opentalkz.network;

import com.opentalkz.model.community.Community;
import com.yashoid.mmv.ModelFeatures;

public class CommunityResponse implements Community {

    private String mName;
    private String mDescription;
    private long mCreatedTime;
    private long mUpdatedTime;
    private String mId;

    public ModelFeatures asModelFeatures() {
        ModelFeatures.Builder builder = new ModelFeatures.Builder();

        builder.add(TYPE, TYPE_COMMUNITY);

        builder.add(ID, mId);
        builder.add(NAME, mName);
        builder.add(DESCRIPTION, mDescription);

        return builder.build();
    }

}
