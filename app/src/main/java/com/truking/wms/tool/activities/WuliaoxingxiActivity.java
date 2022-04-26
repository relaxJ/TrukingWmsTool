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

public class WuliaoxingxiActivity extends AppCompatActivity implements View.OnClickListener{
    private static final int PHOTO_REQUEST_SAOYISAO = 1;
    private static final int REQUEST_CODE_SCAN = 2;
    boolean isInitTab = false;
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;
    private static final String[] m={"1","2","3","4","其他"};
    private ArrayAdapter<String> adapter;
    @BindView(R.id.btn_send)
    public Button submit;

    private String wuliaobianma;//物料编码
    private String gongchang;//工厂
    @BindView(R.id.wuliaobianma)
    public EditText ed_wuliaobianma;
    private NiceSpinner niceSpinner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MyApp.getActivities().add(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wuliaoxingxi);
        ButterKnife.bind(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        niceSpinner = (NiceSpinner) findViewById(R.id.nice_spinner);
        List<String> dataset = new LinkedList<>(Arrays.asList(
                "1010 制造交付中心工厂",
                "1011 无菌装备制造工厂",
                "1012 包装装备制造工厂",
                "1013 工艺装备制造工厂",
                "1014 特种分装及检测装备制造工厂",
                "1015 零部件制造工厂",
                "1019 无价值工厂",
                "2021 飞云生产交付工厂",
                "2029 飞云无价值工厂",
                "2031 源创生产交付工厂",
                "2039 源创无价值工厂"));
        niceSpinner.attachDataSource(dataset);
        niceSpinner.setOnSpinnerItemSelectedListener(new OnSpinnerItemSelectedListener() {
            @Override
            public void onItemSelected(NiceSpinner parent, View view, int position, long id) {
                // This example uses String, but your type can be any

            }
        });
    }

    @OnClick({R.id.btn_send,R.id.scan,R.id.back})
    public void onViewClicked(View view){
        switch (view.getId()) {
            case R.id.btn_send: {
                if(!ButtonUtils.isFastDoubleClick(R.id.btn_send)){
                    //做校验
                    wuliaobianma = ed_wuliaobianma.getText().toString();
                    if(wuliaobianma == null || wuliaobianma != null && "".equals(wuliaobianma)
                           ){
                        XToastUtil.showToast(this,"信息未填完整！无法提交！");
                        return;
                    }
                    if(gongchang == null){
                        gongchang = niceSpinner.getSelectedItem().toString();
                    }
                    //格⼝数量*⽑重<=格⼝载重
                    LoadingStaticDialog.showLoadingDialog(WuliaoxingxiActivity.this, "操作中，请稍等！");
                    startTimer();
                    missDialog = true;
                    //网络请求
                    String url="http://172.16.1.131:9090/api/app/wuliaoxingxi";
                    JSONObject parameters = new JSONObject();
                    //添加参数
                    try {
                        parameters.put("wuliaohao", wuliaobianma);
                        parameters.put("gongchang", gongchang);
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
                                    XToastUtil.showToast(WuliaoxingxiActivity.this,"失败！"+e.getLocalizedMessage());
                                    missDialog = false;
                                    LoadingStaticDialog.loadDialogDismiss();
                                    stopTimer();
                                    Looper.loop();
                                }

                                @Override
                                public void onResponse(Call call, okhttp3.Response response) throws IOException {
                                    Looper.prepare();
                                    String result = response.body().string();
                                    Log.i("8888888888",result);
                                    missDialog = false;
                                    LoadingStaticDialog.loadDialogDismiss();
                                    stopTimer();
                                    JSONObject jsonObject = null;
                                    try {
                                        jsonObject = new JSONObject(result);
                                        int code = jsonObject.getInt("code");
                                        String msg = jsonObject.getString("info");
                                        XToastUtil.showToast(WuliaoxingxiActivity.this,msg);
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
            case R.id.scan: {
                // 取得相机权限
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, PHOTO_REQUEST_SAOYISAO);
                }else{
                    // 调用扫一扫
                    Intent intent = new Intent(this,
                            CaptureActivity.class);
                    startActivityForResult(intent, REQUEST_CODE_SCAN);
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
            case PHOTO_REQUEST_SAOYISAO:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 权限申请成功，扫一扫
                    // 调用扫一扫
                    Intent intent = new Intent(this,
                            CaptureActivity.class);
                    startActivityForResult(intent, REQUEST_CODE_SCAN);
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
            case REQUEST_CODE_SCAN:
                if(intent != null){
                    if(resultCode == RESULT_OK) {
                        String content = intent.getStringExtra(Constant.CODED_CONTENT);
                        String[] arrays = content.split(",",-1);
                        if(arrays.length == 6){
                            String skucode = arrays[3];
                            ed_wuliaobianma.setText(arrays[3]);
                        }
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
                    XToastUtil.showToast(WuliaoxingxiActivity.this, "请求超时！");
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
