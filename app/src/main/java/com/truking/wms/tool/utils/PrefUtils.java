package com.truking.wms.tool.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import java.util.Locale;

/**
 * created by jiangpanming
 * on 2020-06-12
 * desc no
 */
public class PrefUtils {

    private static final String PREF = "spf";
    private static String KEY_LANGUAGE = "app_language";

    /**
     *
     * 获得存入sp的语言
     * */
    public static String getLanguage(Context context) {
        if (TextUtils.isEmpty(getSpf(context, KEY_LANGUAGE))) {
            return Locale.getDefault().getLanguage();//context.getResources().getString(R.string.tv_english);
        }
        return getSpf(context, KEY_LANGUAGE);
    }

    /**
     * 语言存入sp
     * */
    public static void setLanguage(Context context, String value) {
        if (value == null) {
            value = "en";//context.getResources().getString(R.string.tv_english);
        }
        setSpf(context, KEY_LANGUAGE, value);
    }

    private static String getSpf(Context context, String key) {
        return context.getSharedPreferences(PREF, Context.MODE_PRIVATE).getString(key, "");
    }

    private static void setSpf(Context context, String key, String value) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREF, Context.MODE_PRIVATE).edit();
        editor.putString(key, value);
        editor.commit();
    }

}
