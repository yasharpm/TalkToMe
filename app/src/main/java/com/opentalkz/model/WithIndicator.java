package com.opentalkz.model;

import android.content.Context;

import com.yashoid.mmv.Action;
import com.yashoid.mmv.Model;
import com.yashoid.mmv.ModelFeatures;
import com.opentalkz.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public interface WithIndicator extends Basics {

    String INDICATOR_COLOR = "indicatorColor";

    abstract class WithIndicatorTypeProvider extends BaseTypeProvider {

        private int[] mColors;
        private int mColorIndex = 0;

        public WithIndicatorTypeProvider(Context context, String typeName) {
            super(typeName);

            int[] colors = context.getResources().getIntArray(R.array.indicatorColors);

            List<Integer> tempColors = new ArrayList<>(colors.length);

            for (int color: colors) {
                tempColors.add(color);
            }

            mColors = new int[colors.length];

            Random random = new Random();

            for (int i = 0; i < mColors.length; i++) {
                int index = random.nextInt(tempColors.size());

                mColors[i] = tempColors.remove(index);
            }
        }

        @Override
        public Action getAction(ModelFeatures features, String actionName, Object... params) {
            if (Action.ACTION_MODEL_CREATED.equals(actionName)) {
                return mCreationAction;
            }

            return getAction(features, actionName);
        }

        abstract protected void onModelCreated(Model model);

        abstract protected Action getAction(ModelFeatures features, String actionName);

        private Action mCreationAction = new Action() {

            @Override
            public Object perform(Model model, Object... params) {
                int color = mColors[mColorIndex++];

                if (mColorIndex == mColors.length) {
                    mColorIndex = 0;
                }

                model.set(INDICATOR_COLOR, color);

                onModelCreated(model);

                return null;
            }

        };

    }

}
