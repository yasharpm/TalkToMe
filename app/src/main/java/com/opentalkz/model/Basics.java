package com.opentalkz.model;

import android.text.TextUtils;

import com.yashoid.mmv.ModelFeatures;
import com.yashoid.mmv.TypeProvider;

import java.util.List;

public interface Basics {

    String TYPE = "type";

    abstract class BaseTypeProvider implements TypeProvider {

        private String mType;

        public BaseTypeProvider(String type) {
            mType = type;
        }

        protected String getType() {
            return mType;
        }

        @Override
        public boolean isOfType(ModelFeatures features) {
            return TextUtils.equals(mType, (String) features.get(TYPE));
        }

        @Override
        public void getIdentifyingFeatures(ModelFeatures features, List<String> identifyingFeatures) {
            identifyingFeatures.add(TYPE);
        }

    }

}
