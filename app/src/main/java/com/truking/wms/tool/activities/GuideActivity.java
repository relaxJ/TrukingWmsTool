package com.truking.wms.tool.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.URLSpan;
import android.view.View;

import com.truking.wms.tool.R;
import com.truking.wms.tool.utils.GuideUtils;

public class GuideActivity extends AppCompatActivity implements Runnable{

//    private Button start;
//    private CheckBox check;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MyApp.getActivities().add(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        /*//透明状态栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);*/
        //设置状态栏的字体为黑色
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        new Thread(GuideActivity.this).start();
    }

    @Override
    public void run() {
        try{
            //延迟1秒时间
            Thread.sleep(1000);
            //通过SharedPerferences 来获取程序的启动次数
            SharedPreferences preferences= getSharedPreferences("count", 0); // 存在则打开它，否则创建新的Preferences
            int count = preferences.getInt("count", 0); // 取出数据
            /**
             *如果用户不是第一次使用则直接调转到显示界面,否则调转到引导界面
             *intent.setClass(目前的acitivy.this, 目标activity.class);
             */
            Intent intent1 = new Intent();
            intent1.setClass(GuideActivity.this, LoginActivity.class);
            startActivity(intent1);

            GuideUtils.putBoolean(GuideActivity.this, GuideUtils.FIRST_OPEN, true);

            finish();
            //实例化Editor对象
            SharedPreferences.Editor editor = preferences.edit();
            //存入数据
            editor.putInt("count", 1); // 存入数据
            //提交修改
            editor.commit();
        } catch (InterruptedException e) {

        }
    }
}
