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
import com.truking.wms.tool.utils.MyDialog2;
import com.truking.wms.tool.utils.NetworkUtil;
import com.truking.wms.tool.utils.PreferenceHelper;
import com.truking.wms.tool.utils.XToastUtil;
import com.yzq.zxinglibrary.android.CaptureActivity;
import com.yzq.zxinglibrary.common.Constant;

import org.angmarch.views.NiceSpinner;
import org.angmarch.views.OnSpinnerItemSelectedListener;
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
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DafuImportBasicDataActivity1 extends AppCompatActivity implements View.OnClickListener{
    private static final int PHOTO_REQUEST_SAOYISAO = 1;
    private static final int REQUEST_CODE_SCAN = 2;
    boolean isInitTab = false;
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;

    private ArrayAdapter<String> adapter;
    @BindView(R.id.btn_send)
    public Button submit;

    private String wuliaobianma;//物料编码
    private String wuliaomingcheng;//物料名称
    private String guige;//规格
    private String shuliang;//数量
    private String chang;//长
    private String kuan;//宽
    private String gao;//高
    private String zhongliang;//重量
    private String danwei;//单位
    @BindView(R.id.wuliaobianma)
    public TextView ed_wuliaobianma;
    @BindView(R.id.wuliaomingcheng)
    public TextView tv_wuliaomingcheng;
    @BindView(R.id.guige)
    public TextView tv_guige;
    @BindView(R.id.danwei)
    public TextView tv_danwei;
    @BindView(R.id.shuliang)
    public EditText ed_shuliang;
    @BindView(R.id.chang)
    public EditText ed_chang;
    @BindView(R.id.kuan)
    public EditText ed_kuan;
    @BindView(R.id.gao)
    public EditText ed_gao;
    @BindView(R.id.zhongliang)
    public EditText ed_zhongliang;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MyApp.getActivities().add(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dafuimportbasicdata1);
        ButterKnife.bind(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

    }

    @OnClick({R.id.btn_send,R.id.scan,R.id.back})
    public void onViewClicked(View view){
        switch (view.getId()) {
            case R.id.btn_send: {
                if(!ButtonUtils.isFastDoubleClick(R.id.btn_send)){
                    //做校验
                    wuliaobianma = ed_wuliaobianma.getText().toString();
                    chang = ed_chang.getText().toString();
                    kuan = ed_kuan.getText().toString();
                    gao = ed_gao.getText().toString();
                    zhongliang = ed_zhongliang.getText().toString();
                    shuliang = ed_shuliang.getText().toString();
                    if(wuliaobianma == null || wuliaobianma != null && "".equals(wuliaobianma)
                            || shuliang == null || shuliang != null && "".equals(shuliang)
                            || chang == null || chang != null && "".equals(chang)
                            || kuan == null || kuan != null && "".equals(kuan)
                            || gao == null || gao != null && "".equals(gao)
                            || zhongliang == null || zhongliang != null && "".equals(zhongliang)){
                        XToastUtil.showToast(this,"信息未填完整！无法提交！");
                        return;
                    }
                    LoadingStaticDialog.showLoadingDialog(DafuImportBasicDataActivity1.this, "操作中，请稍等！");
                    startTimer();
                    missDialog = true;
                    //网络请求
                    String url="http://172.16.1.131:9090/api/app/dafuwcsbasicdata";
                    JSONObject parameters = new JSONObject();
                    //添加参数
                    try {
                        parameters.put("wuliaobianma", wuliaobianma);
                        parameters.put("wuliaomingcheng", wuliaomingcheng);
                        parameters.put("rongqidalei", "");
                        parameters.put("rongqixiaolei", "");
                        parameters.put("guige", guige);
                        parameters.put("danwei", danwei);
                        parameters.put("chang", chang);
                        parameters.put("kuan", kuan);
                        parameters.put("gao", gao);
                        parameters.put("zhongliang", zhongliang);
                        parameters.put("shuliang", shuliang);
                        parameters.put("mark", 0);
                        parameters.put("userName", PreferenceHelper.getUserId());
                        parameters.put("token", PreferenceHelper.getToken());
                        Log.i("7777777888888",parameters.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    NetworkUtil networkUtil = new NetworkUtil();
                    networkUtil.setCallback(
                            new Callback() {
                                @Override
                                public void onFailure(Call call, IOException e) {
                                    Looper.prepare();
                                    XToastUtil.showToast(DafuImportBasicDataActivity1.this,"失败！"+e.getLocalizedMessage());
                                    missDialog = false;
                                    LoadingStaticDialog.loadDialogDismiss();
                                    stopTimer();
                                    Looper.loop();
                                }

                                @Override
                                public void onResponse(Call call, Response response) throws IOException {
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
                                        XToastUtil.showToast(DafuImportBasicDataActivity1.this,msg);
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
                            LoadingStaticDialog.showLoadingDialog(DafuImportBasicDataActivity1.this, "操作中，请稍等！");
                            startTimer();
                            missDialog = true;
                            OkHttpClient client = new OkHttpClient();
                            String url = "http://172.16.1.131:9090/api/app/dafudata?userName="+ PreferenceHelper.getUserId()+
                                    "&token="+PreferenceHelper.getToken()+"&partNumber="+skucode;
                            Request build = new Request.Builder().url(url).build();
                            Call call = client.newCall(build);
                            call.enqueue(new Callback() {
                                @Override
                                public void onFailure(Call call, IOException e) {
                                    Looper.prepare();
                                    XToastUtil.showToast(DafuImportBasicDataActivity1.this,"失败！"+e.getLocalizedMessage());
                                    missDialog = false;
                                    LoadingStaticDialog.loadDialogDismiss();
                                    stopTimer();
                                    Looper.loop();
                                }

                                @Override
                                public void onResponse(Call call, Response response) throws IOException {
                                    String string = response.body().string();
                                    Log.i("info",string+"");
                                    try {
                                        Looper.prepare();
                                        missDialog = false;
                                        LoadingStaticDialog.loadDialogDismiss();
                                        stopTimer();
                                        JSONObject json = new JSONObject(string);
                                        String data = json.optString("data");
                                        int code = json.optInt("code");
                                        if(code == 555){
                                            JSONObject jsonData = new JSONObject(data);
                                            wuliaomingcheng = jsonData.optString("sapwuliaomiaoshu");//物料名称
                                            danwei = jsonData.optString("jiliangdanwei");//单位
                                            chang = jsonData.optString("chang");
                                            kuan = jsonData.optString("kuan");
                                            gao = jsonData.optString("gao");
                                            guige = jsonData.optString("guige");
                                            zhongliang = jsonData.optString("zhongliang");
                                            shuliang = jsonData.optString("shuliang");
                                            //设置值
                                            tv_danwei.setText(danwei);
                                            ed_shuliang.setText(shuliang);
                                            tv_wuliaomingcheng.setText(wuliaomingcheng);
                                            tv_guige.setText(guige);
                                            ed_chang.setText(chang);
                                            ed_kuan.setText(kuan);
                                            ed_gao.setText(gao);
                                            ed_zhongliang.setText(zhongliang);
                                            //弹窗提示
                                            //创建
                                            MyDialog2 dialog = new MyDialog2(DafuImportBasicDataActivity1.this,new
                                                    MyDialog2.MyOnclickListener() {
                                                        @Override
                                                        public void onYesClick(String message) {
                                                            if("22222".equals(message)){

                                                            }
                                                        }
                                                    });
                                            dialog.show();
                                        }else{
                                            JSONObject jsonData = new JSONObject(data);
                                            danwei = jsonData.optString("jiliangdanwei");//单位
                                            String wuliaomingchengandguigexinghao = jsonData.optString("sapwuliaomiaoshu");//物料名称
                                            String[] arr = wuliaomingchengandguigexinghao.split("\\|\\|");
                                            if(arr.length < 2){
                                                arr = wuliaomingchengandguigexinghao.split("‖") ;
                                            }
                                            wuliaomingcheng = arr[0];
                                            guige = arr[1];
                                            //设置值
                                            tv_danwei.setText(danwei);
                                            tv_guige.setText(guige);
                                            tv_wuliaomingcheng.setText(wuliaomingcheng);
                                        }
                                        Looper.loop();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
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
                    XToastUtil.showToast(DafuImportBasicDataActivity1.this, "请求超时！");
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
