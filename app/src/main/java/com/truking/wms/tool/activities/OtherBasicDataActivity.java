package com.truking.wms.tool.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.truking.wms.tool.R;
import com.truking.wms.tool.utils.ButtonUtils;
import com.truking.wms.tool.utils.LoadingStaticDialog;
import com.truking.wms.tool.utils.NetworkUtil;
import com.truking.wms.tool.utils.PreferenceHelper;
import com.truking.wms.tool.utils.XToastUtil;
import com.yzq.zxinglibrary.android.CaptureActivity;
import com.yzq.zxinglibrary.common.Constant;

import org.angmarch.views.NiceSpinner;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class OtherBasicDataActivity extends AppCompatActivity implements View.OnClickListener{
    private static final int PHOTO_REQUEST_SAOYISAO1 = 1;
    private static final int PHOTO_REQUEST_SAOYISAO2 = 2;

    private static final int REQUEST_CODE_SCAN1= 2;
    private static final int REQUEST_CODE_SCAN2 = 3;
    boolean isInitTab = false;
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;
    private ArrayAdapter<String> adapter;

    @BindView(R.id.pandian)
    public LinearLayout view_pandian;
    @BindView(R.id.wuliaoxingxi)
    public LinearLayout view_wuliaoxingxi;
    @BindView(R.id.huoweiduizhao)
    public LinearLayout view_huoweiduizhao;
    @BindView(R.id.view_bocibiaoqian)
    public LinearLayout view_bocibiaoqian;
    private NiceSpinner niceSpinner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MyApp.getActivities().add(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otherbasicdatas);
        ButterKnife.bind(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

    }

    @OnClick({R.id.pandian,R.id.wuliaoxingxi,R.id.huoweiduizhao,R.id.view_bocibiaoqian})
    public void onViewClicked(View view){
        switch (view.getId()) {
            case R.id.pandian: {
                if(!ButtonUtils.isFastDoubleClick(R.id.pandian)){
                    Intent intent = new Intent(this, PandianActivity.class);
                    startActivity(intent);
                }
                break;
            }
            case R.id.wuliaoxingxi: {
                Intent intent = new Intent(this, WuliaoxingxiActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.huoweiduizhao: {
                Intent intent = new Intent(this, HuoweiduizhaoActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.view_bocibiaoqian: {
                Intent intent = new Intent(this, BocibiaoqianWarehousesActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.back:{
                this.finish();
                break;
            }
        }

    }

    @Override
    public void onClick(View view) {

    }


}
