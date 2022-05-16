package com.truking.wms.tool.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class GuideUtils {

    public static final String FIRST_OPEN = "first_open";
    public static final String FIRST_USE = "first_use";
    public static final String FIRST_SHOW_ADVICE = "first_show_advice";

    public static final String NEED_UPLOAD_USE_LOG = "need_upload_use_log";
    private static final String spFileName = "app";

    public static final String MOBILE_INFO = "mobile_info";

    public static final String ERROR_INFO = "error_info";

    public static final String LOADED_INFO = "LOADED_INFO";

    public static void putBoolean(Context context, String strKey,
                                  Boolean strData) {
        SharedPreferences activityPreferences = context.getSharedPreferences(
                spFileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = activityPreferences.edit();
        editor.putBoolean(strKey, strData);
        editor.commit();
    }

    public static Boolean getBoolean(Context context, String strKey) {
        SharedPreferences setPreferences = context.getSharedPreferences(
                spFileName, Context.MODE_PRIVATE);
        Boolean result = setPreferences.getBoolean(strKey, false);
        return result;
    }

    public static Boolean getBoolean(Context context, String strKey,
                                     Boolean strDefault) {
        SharedPreferences setPreferences = context.getSharedPreferences(
                spFileName, Context.MODE_PRIVATE);
        Boolean result = setPreferences.getBoolean(strKey, strDefault);
        return result;
    }

    public static String getString(Context context, String strKey) {

        SharedPreferences setPreferences = context.getSharedPreferences(
                spFileName, Context.MODE_PRIVATE);
        String result = setPreferences.getString(strKey, "");
        return result;
    }

    public static void putString(Context context, String strKey, String strData) {
        SharedPreferences activityPreferences = context.getSharedPreferences(
                spFileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = activityPreferences.edit();
        editor.putString(strKey, strData);
        editor.commit();
    }
}
