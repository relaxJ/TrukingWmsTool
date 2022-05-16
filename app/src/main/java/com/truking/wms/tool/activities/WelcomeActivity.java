package com.truking.wms.tool.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import com.truking.wms.tool.utils.GuideUtils;
import com.truking.wms.tool.utils.PreferenceHelper;
import com.truking.wms.tool.utils.StatusBarUtil;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class WelcomeActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MyApp.getActivities().add(this);
        super.onCreate(savedInstanceState);
        StatusBarUtil.StatusBarLightMode(this);
//        StatusBarUtil.StatusBarLightMode(this,StatusBarUtil.StatusBarLightMode(this));
        Observable.just(initAppData())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String result) {
                        gotoMainPage();
                    }
                });
    }

    private void gotoMainPage() {
        // 判断是否是第一次开启应用
        boolean isFirstOpen = GuideUtils.getBoolean(this, GuideUtils.FIRST_OPEN);
        // 如果是第一次启动，则先弹窗提醒用户阅读隐私政策，同意后进入功能引导页
        if (!isFirstOpen) {
            Intent intent = new Intent(WelcomeActivity.this, GuideActivity.class);
            startActivity(intent);
        } else {
            Log.i("WelcomeActivity", " PreferenceHelper.getUserId() -> " + PreferenceHelper.getUserId());
            if (PreferenceHelper.getUserId() == null
                    || TextUtils.isEmpty(PreferenceHelper.getUserId())) {
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
            } else {
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
            }
        }
    }

    /**
     * 初始化应用数据
     */
    private String initAppData() {

        return null;
    }

}

