package com.yashoid.talktome.model.list;

import com.yashoid.mmv.Action;
import com.yashoid.mmv.Model;
import com.yashoid.mmv.ModelFeatures;
import com.yashoid.talktome.model.Basics;
import com.yashoid.talktome.model.Stateful;

public interface ModelList extends Basics, Stateful {

    String MODEL_LIST = "modelList";

    String GET_MODELS = "getModels";

    abstract class ModelListTypeProvider extends BaseTypeProvider {

        protected ModelListTypeProvider(String typeName) {
            super(typeName);
        }

        @Override
        public Action getAction(ModelFeatures features, String actionName, Object... params) {
            if (Action.ACTION_MODEL_CREATED.equals(actionName)) {
                return mInitializationAction;
            }
            else if (GET_MODELS.equals(actionName)) {
                return getModelsAction;
            }
            else {
                return getAction(features, actionName);
            }
        }

        abstract protected Action getAction(ModelFeatures features, String actionName);

        private Action mInitializationAction = new Action() {

            @Override
            public Object perform(Model model, Object... params) {
                model.set(STATE, STATE_IDLE);
                return null;
            }

        };

        abstract protected void getModels(Model model, Object... params);

        private Action getModelsAction = new Action() {

            @Override
            public Object perform(Model model, Object... params) {
                getModels(model, params);

                return null;
            }

        };

    }

}
