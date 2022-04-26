package com.truking.wms.tool.activities;


import android.app.Activity;
import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.multidex.MultiDex;
import android.util.Log;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.inuker.bluetooth.library.BluetoothContext;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.truking.wms.tool.R;
import com.truking.wms.tool.utils.LanguageUtil;
import com.truking.wms.tool.utils.PrefUtils;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *Application里面attachBaseContext和onCreate函数调用顺序
 * Application-> attachBaseContext ();
 * ContentProvider:onCreate()
 * Application:onCreate()
 */
public class MyApp extends Application {
    //private static MainActivity mainActivity = null;
    private static MyApp mApp ;
    private static List<Activity> activities = new ArrayList<>();

    public static MyApp getInstance() {
        return mApp;
    }

    private static Context mContext;
    public int count = 0;//app栈中有多少个activity，用来判断是否在前台还是后台
    public static long timeStart = 0;//记录切换到后台的毫秒值

    private Activity lastActivity;

    public static Context getContext() {
        return mContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        //根据之前设置的语言设置app应用内语言
        LanguageUtil.changeAppLanguage(this, PrefUtils.getLanguage(this));

        Fresco.initialize(this);//初始化 图片加载

//        Locale mSystemLanguageList[]= Locale.getAvailableLocales();
//        for(Locale locale:mSystemLanguageList){
//            Log.i("AIRBNK","language:"+locale.getLanguage());
//        }
//        String language = Locale.getDefault().getLanguage();
//        Log.i("AIRBNK","language11111:"+language);
        mApp = this;
        BluetoothContext.set(this);
        localizeString();

        //
        //application上下文
        mContext = getApplicationContext();

        //应用前后台切换的判断
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityStopped(Activity activity) {
                Log.v("App", activity + "onActivityStopped");
                lastActivity = activity;
                count--;
                if (count == 0) {
                    timeStart = new Date().getTime();
                    Log.d("App","切到后台-->" + timeStart);
                }
            }

            @Override
            public void onActivityStarted(Activity activity) {
                Log.d("App", activity + "onActivityStarted");
//                if (count == 0) {
//                    Log.d("App","切到前台");
//                    long timeEnd = new Date().getTime();
//                    Log.d("App","时间差" + (timeEnd - timeStart));
//                    //切换到前台和切换到后台的时间差大于等于10s
//                    if (timeStart != 0 && timeEnd - timeStart >= 1000 * 30) {
//                        //如果设置了手势密码
//                        DBOpenHelper helper=new DBOpenHelper(getContext());
//                        SQLiteDatabase db=helper.getWritableDatabase();
//                        Cursor cursor = db.rawQuery("select * from patternLock", new String[]{});
//                        if(cursor.moveToNext()) {
//                            Intent receiver = new Intent();
//                            receiver.setAction("GO_GET_LOCK");
//                            receiver.putExtra("activity",lastActivity.getLocalClassName());
//                            sendBroadcast(receiver);
//                        }
//                    }
//                }
//                count++;
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
                Log.d("App","onActivitySaveInstanceState");
            }

            @Override
            public void onActivityResumed(Activity activity) {
                Log.d("App","onActivityResumed");
            }

            @Override
            public void onActivityPaused(Activity activity) {
                Log.d("App","onActivityPaused");
            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                Log.d("App","onActivityDestroyed");
            }

            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                Log.d("App","onActivityCreated");
            }
        });
    }

//    //public static void setMainActivity(MainActivity activity) {
//        mainActivity = activity;
//    }

    private void localizeString() {
        //设置 Header 为 Material风格
        ClassicsHeader.REFRESH_HEADER_PULLDOWN = getResources().getString(R.string.header_pull_down);
        ClassicsHeader.REFRESH_HEADER_REFRESHING = getResources().getString(R.string.header_refreshing);
        ClassicsHeader.REFRESH_HEADER_LOADING = getResources().getString(R.string.header_loading);
        ClassicsHeader.REFRESH_HEADER_RELEASE = getResources().getString(R.string.header_release);
        ClassicsHeader.REFRESH_HEADER_FINISH = getResources().getString(R.string.header_finished);
        ClassicsHeader.REFRESH_HEADER_FAILED = getResources().getString(R.string.header_failed);
        ClassicsHeader.REFRESH_HEADER_LASTTIME="";
//      ClassicsHeader.REFRESH_HEADER_TIME = getResources().getString(R.string.header_time);
        ClassicsFooter.REFRESH_FOOTER_PULLUP = getResources().getString(R.string.footer_pull_up);
        ClassicsFooter.REFRESH_FOOTER_RELEASE = getResources().getString(R.string.footer_release);
        ClassicsFooter.REFRESH_FOOTER_LOADING = getResources().getString(R.string.footer_loading);
        ClassicsFooter.REFRESH_FOOTER_REFRESHING = getResources().getString(R.string.footer_refreshing);
        ClassicsFooter.REFRESH_FOOTER_FINISH = getResources().getString(R.string.footer_finished);
        ClassicsFooter.REFRESH_FOOTER_FAILED = getResources().getString(R.string.footer_failed);
        ClassicsFooter.REFRESH_FOOTER_ALLLOADED = getResources().getString(R.string.footer_allloaded);
    }

    public static List<Activity> getActivities() {
        return activities;
    }

    /**
     * 获取自身App安装包信息
     *
     * @return
     */
    public PackageInfo getLocalPackageInfo() {
        return getPackageInfo(getPackageName());
    }

    /**
     * 获取App安装包信息
     *
     * @return
     */
    public PackageInfo getPackageInfo(String packageName) {
        PackageInfo info = null;
        try {
            info = getPackageManager().getPackageInfo(packageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return info;
    }


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LanguageUtil.attachBaseContext(base, PrefUtils.getLanguage(base)));
        MultiDex.install(this);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        LanguageUtil.changeAppLanguage(this, PrefUtils.getLanguage(this)); // onCreate 之前调用 否则不起作用
    }


    @Override
    public void onTerminate() {
        super.onTerminate();
    }


}
