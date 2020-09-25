package com.opentalkz.model.community;

import com.opentalkz.model.list.ModelList;
import com.yashoid.mmv.Action;
import com.yashoid.mmv.Model;
import com.yashoid.mmv.ModelFeatures;

import java.util.ArrayList;
import java.util.List;

public interface CommunityList extends ModelList {

    String TYPE_COMMUNITY_LIST = "CommunityList";

    ModelFeatures FEATURES = new ModelFeatures.Builder().add(TYPE, TYPE_COMMUNITY_LIST).build();

    String SELECTED_COMMUNITY_ID = "selectedCommunityId";

    String ADD_COMMUNITY = "addCommunity";

    class CommunityListTypeProvider extends ModelListTypeProvider {

        public CommunityListTypeProvider() {
            super(TYPE_COMMUNITY_LIST);
        }

        @Override
        protected Action getAction(ModelFeatures features, String actionName) {
            return null;
        }

        @Override
        protected void getModels(Model model, Object... params) {

        }

        public Object addCommunity(Model model, Object... params) {
            List<ModelFeatures> communities = model.get(MODEL_LIST);

            if (communities == null) {
                communities = new ArrayList<>();
            }

            communities.add((ModelFeatures) params[0]);

            model.set(MODEL_LIST, communities);

            model.cache(true);

            return null;
        }

    }

}
