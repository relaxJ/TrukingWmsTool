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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ReplenishmentActivity extends AppCompatActivity implements View.OnClickListener{
    private static final int PHOTO_REQUEST_SAOYISAO = 1;
    private static final int REQUEST_CODE_SCAN = 2;
    boolean isInitTab = false;
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;

    private ArrayAdapter<String> adapter;
    @BindView(R.id.btn_send)
    public Button submit;

    private String workCenterId;//工作中心id
    private String workCenter;//工作中心
    private String costCenter;//成本中心
    private String factoryCode;//工厂码
    private String factoryName;//工厂名称
    private String shortDesc;//短描述（班组）
    private String itemCode;//物料码
    private String itemName;//物料名称
    private double replenishmentNum;//补货量
    private double minNumber;//最小补货量
    private String unit;//单位
    private long intervalTime;//最短补货周期（毫秒）
    private String address;//地址
    private String remark;//备注

    @BindView(R.id.weiyima)
    public TextView tv_weiyima;
    @BindView(R.id.workCenter)
    public TextView tv_workCenter;
    @BindView(R.id.costCenter)
    public TextView tv_costCenter;
    @BindView(R.id.factoryCode)
    public TextView tv_factoryCode;
    @BindView(R.id.factoryName)
    public TextView tv_factoryName;
    @BindView(R.id.address)
    public TextView tv_address;
    @BindView(R.id.shortDesc)
    public TextView tv_shortDesc;
    @BindView(R.id.itemCode)
    public TextView tv_itemCode;
    @BindView(R.id.itemName)
    public TextView tv_itemName;
    @BindView(R.id.minNumber)
    public TextView tv_minNumber;
    @BindView(R.id.unit)
    public TextView tv_unit;
    @BindView(R.id.intervalTime)
    public TextView tv_intervalTime;
    @BindView(R.id.replenishmentNum)
    public EditText ed_replenishmentNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MyApp.getActivities().add(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_replenishment);
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
                    String weiyima = tv_weiyima.getText().toString();
                    if(weiyima == null || weiyima != null && "".equals(weiyima)){
                        XToastUtil.showToast(this,"信息未填完整！无法提交！");
                        return;
                    }
                    workCenterId = weiyima.split("&")[0];
                    itemCode = weiyima.split("&")[1];
                    String replenishmentNumTemp = ed_replenishmentNum.getText().toString();
                    if(replenishmentNumTemp == null || replenishmentNumTemp != null && "".equals(replenishmentNumTemp)){
                        XToastUtil.showToast(this,"信息未填完整！无法提交！");
                        return;
                    }
                    replenishmentNum = Double.parseDouble(replenishmentNumTemp);
                    LoadingStaticDialog.showLoadingDialog(ReplenishmentActivity.this, "操作中，请稍等！");
                    startTimer();
                    missDialog = true;
                    //网络请求
                    String url="http://172.16.1.131:9090/api/app/replenishment";
                    JSONObject parameters = new JSONObject();
                    //添加参数
                    try {
                        parameters.put("workCenterId", workCenterId);
                        parameters.put("itemCode", itemCode);
                        parameters.put("replenishmentNum", replenishmentNum);
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
                                    XToastUtil.showToast(ReplenishmentActivity.this,"失败！"+e.getLocalizedMessage());
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
                                        XToastUtil.showToast(ReplenishmentActivity.this,msg);
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
                        String[] arrays = content.split("&",-1);
                        tv_weiyima.setText(content);
                        workCenterId = arrays[0];
                        itemCode = arrays[1];
                        LoadingStaticDialog.showLoadingDialog(ReplenishmentActivity.this, "操作中，请稍等！");
                        startTimer();
                        missDialog = true;
                        OkHttpClient client = new OkHttpClient();
                        String url = "http://172.16.1.131:9090/api/app/workcenter?userName="+ PreferenceHelper.getUserId()+
                                "&token="+PreferenceHelper.getToken()+"&workCenterId="+workCenterId;
                        Request build = new Request.Builder().url(url).build();
                        Call call = client.newCall(build);
                        call.enqueue(new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                Looper.prepare();
                                XToastUtil.showToast(ReplenishmentActivity.this,"失败！"+e.getLocalizedMessage());
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
                                    JSONObject json = new JSONObject(string);
                                    String data = json.optString("data");
                                    int code = json.optInt("code");
                                    if(code == 200){
                                        JSONObject jsonData = new JSONObject(data);
                                        workCenter = jsonData.optString("workCenter");//
                                        costCenter = jsonData.optString("costCenter");//
                                        factoryCode = jsonData.optString("factoryCode");
                                        factoryName = jsonData.optString("factoryName");
                                        shortDesc = jsonData.optString("shortDesc");
                                        address = jsonData.optString("address");
                                        remark = jsonData.optString("remark");
                                        //设置值
                                        tv_workCenter.setText(workCenter);
                                        tv_costCenter.setText(costCenter);
                                        tv_factoryCode.setText(factoryCode);
                                        tv_factoryName.setText(factoryName);
                                        tv_shortDesc.setText(shortDesc);
                                        tv_address.setText(address);


                                        String url1 = "http://172.16.1.131:9090/api/app/replenishmentUnit?userName="+ PreferenceHelper.getUserId()+
                                                "&token="+PreferenceHelper.getToken()+"&itemCode="+itemCode;
                                        Request build1 = new Request.Builder().url(url1).build();
                                        Call call1 = client.newCall(build1);
                                        call1.enqueue(new Callback() {
                                            @Override
                                            public void onFailure(Call call, IOException e) {
                                                Looper.prepare();
                                                XToastUtil.showToast(ReplenishmentActivity.this,"失败！"+e.getLocalizedMessage());
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
                                                    if(code == 200){
                                                        JSONObject jsonData = new JSONObject(data);
                                                        itemCode = jsonData.optString("itemCode");//
                                                        itemName = jsonData.optString("itemName");//
                                                        unit = jsonData.optString("unit");
                                                        minNumber = Double.parseDouble(jsonData.optString("minNumber"));
                                                        intervalTime = Long.parseLong(jsonData.optString("intervalTime"));
                                                        //设置值
                                                        tv_itemCode.setText(itemCode);
                                                        tv_itemName.setText(itemName);
                                                        tv_unit.setText(unit);
                                                        tv_minNumber.setText(String.valueOf(minNumber));
                                                        tv_intervalTime.setText(String.valueOf(intervalTime));
                                                    }else{

                                                    }
                                                    Looper.loop();
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        });
                                    }else{

                                    }
                                    Looper.loop();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });

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
                    XToastUtil.showToast(ReplenishmentActivity.this, "请求超时！");
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
