package com.truking.wms.tool.utils;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.truking.wms.tool.activities.MyApp;


//用于存储一些本地变量
public class PreferenceHelper {

    public static String getToken() {
        SharedPreferences usershared = MyApp.getInstance().getSharedPreferences("BaseInfo", Application.MODE_PRIVATE);
        return usershared.getString("TOKEN", "");
    }

    public static void saveToken(String token) {
        SharedPreferences preferences = MyApp.getInstance().getSharedPreferences("BaseInfo",
                Application.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("TOKEN", token);
        editor.commit();
    }

    public static void saveVersion(String version) {
        SharedPreferences preferences = MyApp.getInstance().getSharedPreferences("BaseInfo",
                Application.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("VERSION", version);
        editor.commit();
    }
    public static String getVersion() {
        SharedPreferences usershared = MyApp.getInstance().getSharedPreferences("BaseInfo", Application.MODE_PRIVATE);
        return usershared.getString("VERSION", "");
    }

    public static String getUserId() {
        SharedPreferences usershared = MyApp.getInstance().getSharedPreferences("BaseInfo", Application.MODE_PRIVATE);
        return usershared.getString("USER_ID", "");
    }

    public static void saveUserId(String userId) {
        SharedPreferences preferences = MyApp.getInstance().getSharedPreferences("BaseInfo",
                Application.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("USER_ID", userId);
        editor.commit();
    }

    public static void saveLoginAcct(String loginAcct){
        SharedPreferences preferences = MyApp.getInstance().getSharedPreferences("BaseInfo",
                Application.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("LOGIN_ACCT", loginAcct);
        editor.commit();
    }

    public static String getLoginAcct(){
        SharedPreferences usershared = MyApp.getInstance().getSharedPreferences("BaseInfo", Application.MODE_PRIVATE);
        return usershared.getString("LOGIN_ACCT", "");
    }


    public static String getPassword() {
        SharedPreferences usershared = MyApp.getInstance().getSharedPreferences("BaseInfo", Application.MODE_PRIVATE);
        return usershared.getString("USER_PASSWORD", "");
    }

    public static void savePassword(String pwd) {
        SharedPreferences preferences = MyApp.getInstance().getSharedPreferences("BaseInfo",
                Application.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("USER_PASSWORD", pwd);
        editor.commit();
    }

    public static String getKeystr(String keyname) {
        SharedPreferences usershared = MyApp.getInstance().getSharedPreferences("newBaseInfo", Application.MODE_PRIVATE);
        return usershared.getString(keyname, "");
    }

    public static void saveKeystr(String keyname,String keyvalue) {
        SharedPreferences preferences = MyApp.getInstance().getSharedPreferences("newBaseInfo",
                Application.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(keyname, keyvalue);
        editor.commit();
    }

    //1、手机或者邮箱登录，2、游客登录 3、微信登录  4、facebook登录  5 QQ登录
    public static void saveLoginMode(int loginMode) {
        SharedPreferences preferences = MyApp.getInstance().getSharedPreferences("BaseInfo",
                Application.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("LOGIN_MODE", loginMode);
        editor.commit();
    }

    public static int getLoginMode(){
        SharedPreferences usershared = MyApp.getInstance().getSharedPreferences("BaseInfo", Application.MODE_PRIVATE);
        return usershared.getInt("LOGIN_MODE", 0);
    }

    @RequiresApi(api = Build.VERSION_CODES.FROYO)
    public static void logOff(Context context){
        saveToken("");
        saveUserId("");
        saveUnionId("");
    }

    public static void saveAskStatus(boolean isNeverAsk){
        SharedPreferences preferences = MyApp.getInstance().getSharedPreferences("BaseInfo",
                Application.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("IS_NEVER_ASK", isNeverAsk);
        editor.commit();
    }

    public static boolean isNeverAsk(){
        SharedPreferences usershared = MyApp.getInstance().getSharedPreferences("BaseInfo", Application.MODE_PRIVATE);
        return usershared.getBoolean("IS_NEVER_ASK", false);
    }

    public static String getUnionId(){
        SharedPreferences usershared = MyApp.getInstance().getSharedPreferences("BaseInfo", Application.MODE_PRIVATE);
        return usershared.getString("UNION_ID", "");
    }

    public static void saveUnionId(String unionId){
        SharedPreferences preferences = MyApp.getInstance().getSharedPreferences("BaseInfo",
                Application.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("UNION_ID", unionId);
        editor.commit();
    }

}
