package com.yashoid.talktome.view;

import android.content.Context;
import android.graphics.Typeface;

import java.util.HashMap;

public class Fonts {

    public static final int DEFAULT_REGULAR = 0;

    private static final String FONTS_ADDRESS = "font/";

    private static HashMap<Integer, Typeface> mFontMap = new HashMap<>();

    public static Typeface get(int font, Context context) {
        Typeface typeface = mFontMap.get(font);

        if (typeface == null) {
            typeface = Typeface.createFromAsset(context.getAssets(), getFontPath(font));

            mFontMap.put(font, typeface);
        }

        return typeface;
    }

    private static String getFontPath(int font) {
        return FONTS_ADDRESS + getFontName(font);
    }

    private static String getFontName(int font) {
        switch (font) {
            case DEFAULT_REGULAR:
            default:
                return "dana-regular.ttf";
        }
    }

}
