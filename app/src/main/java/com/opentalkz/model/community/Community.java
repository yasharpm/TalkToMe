package com.opentalkz.model.community;

import com.opentalkz.model.Basics;
import com.yashoid.mmv.Action;
import com.yashoid.mmv.ModelFeatures;

import java.util.List;

public interface Community extends Basics {

    String TYPE_COMMUNITY = "Community";

    String ID = "id";
    String NAME = "name";
    String DESCRIPTION = "description";

    class CommunityTypeProvider extends BaseTypeProvider {

        public CommunityTypeProvider() {
            super(TYPE_COMMUNITY);
        }

        @Override
        public void getIdentifyingFeatures(ModelFeatures features, List<String> identifyingFeatures) {
            super.getIdentifyingFeatures(features, identifyingFeatures);

            identifyingFeatures.add(ID);
        }

        @Override
        public Action getAction(ModelFeatures features, String actionName, Object... params) {
            return null;
        }

    }

}
