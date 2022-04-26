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
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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
import org.angmarch.views.OnSpinnerItemSelectedListener;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HuoweiduizhaoActivity extends AppCompatActivity implements View.OnClickListener{
    private static final int PHOTO_REQUEST_SAOYISAO1 = 1;
    private static final int PHOTO_REQUEST_SAOYISAO2 = 2;

    private static final int REQUEST_CODE_SCAN1= 2;
    private static final int REQUEST_CODE_SCAN2 = 3;
    boolean isInitTab = false;
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;
    private ArrayAdapter<String> adapter;
    @BindView(R.id.btn_send)
    public Button submit;

    private String jiuhuowei;//旧货位
    private String xinhuowei;//新货位

    @BindView(R.id.jiuhuowei)
    public EditText ed_jiuhuowei;
    @BindView(R.id.xinhuowei)
    public EditText ed_xinhuowei;
    @BindView(R.id.lastjiuhuowei)
    public TextView ed_lastjiuhuowei;
    @BindView(R.id.lastxinhuowei)
    public TextView ed_lastxinhuowei;

    private NiceSpinner niceSpinner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MyApp.getActivities().add(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_huoweiduizhao);
        ButterKnife.bind(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

    }

    @OnClick({R.id.btn_send,R.id.scan1,R.id.scan2,R.id.back})
    public void onViewClicked(View view){
        switch (view.getId()) {
            case R.id.btn_send: {
                if(!ButtonUtils.isFastDoubleClick(R.id.btn_send)){
                    //做校验
                    jiuhuowei = ed_jiuhuowei.getText().toString();
                    xinhuowei = ed_xinhuowei.getText().toString();
                    if(xinhuowei == null || xinhuowei != null && "".equals(xinhuowei)
                            || jiuhuowei == null || jiuhuowei != null && "".equals(jiuhuowei)
                            ){
                        XToastUtil.showToast(this,"信息未填完整！无法提交！");
                        return;
                    }
                    //新旧货位不能一样
                    if(jiuhuowei.equals(xinhuowei)){
                        XToastUtil.showToast(this,"新货位不能和旧货位一样！");
                        return;
                    }
                    LoadingStaticDialog.showLoadingDialog(HuoweiduizhaoActivity.this, "操作中，请稍等！");
                    startTimer();
                    missDialog = true;
                    //网络请求
                    String url="http://172.16.1.131:9090/api/app/huoweiduizhao";
                    JSONObject parameters = new JSONObject();
                    //添加参数
                    try {
                        parameters.put("oldHuowei", jiuhuowei);
                        parameters.put("newHuowei", xinhuowei);
                        parameters.put("userName", PreferenceHelper.getUserId());
                        parameters.put("token", PreferenceHelper.getToken());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    NetworkUtil networkUtil = new NetworkUtil();
                    networkUtil.setCallback(
                            new okhttp3.Callback() {
                                @Override
                                public void onFailure(Call call, IOException e) {
                                    Looper.prepare();
                                    XToastUtil.showToast(HuoweiduizhaoActivity.this,"失败！"+e.getLocalizedMessage());
                                    missDialog = false;
                                    LoadingStaticDialog.loadDialogDismiss();
                                    stopTimer();
                                    Looper.loop();
                                }

                                @Override
                                public void onResponse(Call call, okhttp3.Response response) throws IOException {
                                    Looper.prepare();
                                    String result = response.body().string();
                                    missDialog = false;
                                    LoadingStaticDialog.loadDialogDismiss();
                                    stopTimer();
                                    JSONObject jsonObject = null;
                                    try {
                                        jsonObject = new JSONObject(result);
                                        int code = jsonObject.getInt("code");
                                        String msg = jsonObject.getString("info");
                                        XToastUtil.showToast(HuoweiduizhaoActivity.this,msg);
                                        if(code == 200){
                                            ed_jiuhuowei.setText("");
                                            ed_xinhuowei.setText("");
                                            ed_lastjiuhuowei.setText(jiuhuowei);
                                            ed_lastxinhuowei.setText(xinhuowei);
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    Looper.loop();
                                }
                            });
                    networkUtil.post(url,parameters);
                }
                break;
            }
            case R.id.scan1: {
                // 取得相机权限
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, PHOTO_REQUEST_SAOYISAO1);
                }else{
                    // 调用扫一扫
                    Intent intent = new Intent(this,
                            CaptureActivity.class);
                    startActivityForResult(intent, REQUEST_CODE_SCAN1);
                }
                break;
            }
            case R.id.scan2: {
                // 取得相机权限
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, PHOTO_REQUEST_SAOYISAO2);
                }else{
                    // 调用扫一扫
                    Intent intent = new Intent(this,
                            CaptureActivity.class);
                    startActivityForResult(intent, REQUEST_CODE_SCAN2);
                }
                break;
            }
            case R.id.back:{
                this.finish();
                break;
            }
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case PHOTO_REQUEST_SAOYISAO1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 权限申请成功，扫一扫
                    // 调用扫一扫
                    Intent intent = new Intent(this,
                            CaptureActivity.class);
                    startActivityForResult(intent, REQUEST_CODE_SCAN1);
                }
                break;
            }
            case PHOTO_REQUEST_SAOYISAO2: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 权限申请成功，扫一扫
                    // 调用扫一扫
                    Intent intent = new Intent(this,
                            CaptureActivity.class);
                    startActivityForResult(intent, REQUEST_CODE_SCAN2);
                }
                break;
            }
        }
    }

    /**
     * 重写取得活动返回值的方法
     **/
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        switch (requestCode){
            // 扫一扫返回值
            case REQUEST_CODE_SCAN1:
                if(intent != null) {
                    if (resultCode == RESULT_OK) {
                        String content = intent.getStringExtra(Constant.CODED_CONTENT);
                        ed_jiuhuowei.setText(content);
                    }
                }
                break;
            case REQUEST_CODE_SCAN2:
                if(intent != null) {
                    if (resultCode == RESULT_OK) {
                        String content = intent.getStringExtra(Constant.CODED_CONTENT);
                        ed_xinhuowei.setText(content);
                    }
                }
                break;
            default:
                if(intent != null && resultCode != RESULT_OK){
                    XToastUtil.showToast(this, "二维码识别失败！");
                }
                break;
        }
    }

    private  boolean missDialog;
    private CountDownTimer countDownTimer;
    public  void startTimer() {
        stopTimer();
        countDownTimer = new CountDownTimer(30000L, 1000L) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                if(missDialog == true){
                    LoadingStaticDialog.loadDialogDismiss();
                    XToastUtil.showToast(HuoweiduizhaoActivity.this, "请求超时！");
                    stopTimer();
                }
            }
        }.start();
    }

    public void stopTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    @Override
    public void onClick(View view) {

    }


}
