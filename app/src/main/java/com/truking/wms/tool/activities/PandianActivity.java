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

public class PandianActivity extends AppCompatActivity implements View.OnClickListener{
    private static final int PHOTO_REQUEST_SAOYISAO = 1;
    private static final int REQUEST_CODE_SCAN = 2;
    private static final int PHOTO_REQUEST_SAOYISAO1 = 11;
    private static final int REQUEST_CODE_SCAN1= 22;
    boolean isInitTab = false;
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;
    private static final String[] m={"1","2","3","4","其他"};
    private ArrayAdapter<String> adapter;
    @BindView(R.id.btn_save)
    public Button submit;

    private String wuliaobianma;//物料编码
    private String jiuwuliaobianma;//旧物料编码
    private String wuliaomingcheng;//物料名称
    private String tuhaoguige;//图号规格
    private String danwei;//单位
    private String huowei;
    private String shuliang;

    @BindView(R.id.wuliaobianma)
    public TextView tv_wuliaobianma;
    @BindView(R.id.ed_shuliang)
    public EditText ed_shuliang;
    private NiceSpinner niceSpinner;
    @BindView(R.id.huowei)
    public EditText ed_huowei;
    @BindView(R.id.jiuwuliaobianma)
    public TextView tv_jiuwuliaobianma;
    @BindView(R.id.wuliaomingcheng)
    public TextView tv_wuliaomingcheng;
    @BindView(R.id.tuhaoguige)
    public TextView tv_tuhaoguige;
    @BindView(R.id.danwei)
    public TextView tv_danwei;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MyApp.getActivities().add(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pandian);
        ButterKnife.bind(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        niceSpinner = (NiceSpinner) findViewById(R.id.cangku_spinner);
        List<String> dataset = new LinkedList<>(Arrays.asList(
                "0001 费用仓库",
                "1001 中央零部件仓库",
                "1002 后包零部件仓库",
                "1003 TOP零部件仓库",
                "1004 冻干原材料仓库",
                "1005 冻干零部件仓库",
                "1006 生物及中药原材料仓库",
                "1007 生物及中药零部件仓库",
                "1008 压力容器原材料仓库",
                "1009 压力容器零部件仓库",
                "1010 智能制造零部件仓库",
                "1011 寄售VMI仓库",
                "1012 工具辅料仓库",
                "1013 诺脉科零部件仓库",
                "1014 保税仓库",
                "1015 成品仓库",
                "1016 配件服务仓库",
                "1017 华兴外租仓",
                "1018 现场大件仓",
                "2101 飞云原材料仓库",
                "2102 飞云零部件仓库",
                "2103 飞云成品仓库",
                "3101 源创原材料仓库",
                "3102 源创零部件仓库",
                "3103 源创成品仓库",
                "3104 源创原材料仓库2",
                "9000 生产疵品仓库",
                "9001 供应商退货库",
                "9002 待维修仓库",
                "9003 待换货仓库",
                "9004 试制材料库"));
        niceSpinner.attachDataSource(dataset);
        niceSpinner.setOnSpinnerItemSelectedListener(new OnSpinnerItemSelectedListener() {
            @Override
            public void onItemSelected(NiceSpinner parent, View view, int position, long id) {
                // This example uses String, but your type can be any

            }
        });
    }

    @OnClick({R.id.btn_save,R.id.scan_huowei,R.id.scan_wuliaobianma,R.id.back})
    public void onViewClicked(View view){
        switch (view.getId()) {
            case R.id.btn_save: {
                if(!ButtonUtils.isFastDoubleClick(R.id.btn_save)){
                    //做校验
                    wuliaobianma = tv_wuliaobianma.getText().toString();
                    huowei = ed_huowei.getText().toString();
                    shuliang = ed_shuliang.getText().toString();
                    if(wuliaobianma == null || wuliaobianma != null && "".equals(wuliaobianma) ||
                            huowei == null || huowei != null && "".equals(huowei) ||
                            shuliang == null || shuliang != null && "".equals(shuliang)){
                        XToastUtil.showToast(this,"信息未填完整！无法提交！");
                        return;
                    }
                    LoadingStaticDialog.showLoadingDialog(PandianActivity.this, "操作中，请稍等！");
                    startTimer();
                    missDialog = true;
                    //网络请求
                    String url="http://172.16.1.131:9090/api/app/pandian";
                    JSONObject parameters = new JSONObject();
                    //添加参数
                    try {
                        parameters.put("itemcode", wuliaobianma);
                        parameters.put("warehousecode", niceSpinner.getSelectedItem().toString());
                        parameters.put("huowei", huowei);
                        parameters.put("olditemcode", jiuwuliaobianma);
                        parameters.put("itemname", wuliaomingcheng);
                        parameters.put("tuhaoguige", tuhaoguige);
                        parameters.put("danwei", danwei);
                        parameters.put("shuliang", shuliang);
                        parameters.put("userName", PreferenceHelper.getUserId());
                        parameters.put("token", PreferenceHelper.getToken());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    NetworkUtil networkUtil = new NetworkUtil();
                    networkUtil.setCallback(
                            new Callback() {
                                @Override
                                public void onFailure(Call call, IOException e) {
                                    Looper.prepare();
                                    XToastUtil.showToast(PandianActivity.this,"失败！"+e.getLocalizedMessage());
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
                                        XToastUtil.showToast(PandianActivity.this,msg);
                                        if(msg.equals("OK")){
                                            ed_huowei.setText("");
                                            tv_wuliaobianma.setText("");
                                            tv_wuliaomingcheng.setText("");
                                            tv_jiuwuliaobianma.setText("");
                                            tv_tuhaoguige.setText("");
                                            tv_danwei.setText("");
                                            ed_shuliang.setText("");
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
            case R.id.scan_wuliaobianma: {
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
            case R.id.scan_huowei: {
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
            case R.id.back:{
                this.finish();
                break;
            }
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case PHOTO_REQUEST_SAOYISAO: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 权限申请成功，扫一扫
                    // 调用扫一扫
                    Intent intent = new Intent(this,
                            CaptureActivity.class);
                    startActivityForResult(intent, REQUEST_CODE_SCAN);
                }
                break;
            }
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
                            tv_wuliaobianma.setText(arrays[3]);
                            wuliaobianma = arrays[3];
                            OkHttpClient client = new OkHttpClient();
                            String url = "http://172.16.1.131:9090/api/app/data?userName="+PreferenceHelper.getUserId()+
                                    "&token="+PreferenceHelper.getToken()+"&partNumber="+wuliaobianma;
                            Request build = new Request.Builder().url(url).build();
                            Call call = client.newCall(build);
                            call.enqueue(new Callback() {
                                @Override
                                public void onFailure(Call call, IOException e) {
                                    Looper.prepare();
                                    XToastUtil.showToast(PandianActivity.this,"失败！"+e.getLocalizedMessage());
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
                                        JSONObject json = new JSONObject(string);
                                        String data = json.optString("data");
                                        JSONObject jsonData = new JSONObject(data);
                                        jiuwuliaobianma = jsonData.optString("jiuwuliaobianma");//旧物料号
                                        String wuliaomingchengandguigexinghao = jsonData.optString("sapwuliaomiaoshu");//物料名称
                                        String[] arr = wuliaomingchengandguigexinghao.split("\\|\\|");
                                        if(arr.length < 2){
                                            arr = wuliaomingchengandguigexinghao.split("‖") ;
                                        }
                                        wuliaomingcheng = arr[0];
                                        tuhaoguige = arr[1];
                                        danwei = jsonData.optString("jiliangdanwei");//单位
                                        Looper.prepare();
                                        missDialog = false;
                                        LoadingStaticDialog.loadDialogDismiss();
                                        stopTimer();
                                        //设置值
                                        tv_danwei.setText(danwei);
                                        tv_jiuwuliaobianma.setText(jiuwuliaobianma);
                                        tv_tuhaoguige.setText(tuhaoguige);
                                        tv_wuliaomingcheng.setText(wuliaomingcheng);
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
            case REQUEST_CODE_SCAN1://货位
                if(intent != null){
                    if(resultCode == RESULT_OK) {
                        String content = intent.getStringExtra(Constant.CODED_CONTENT);
                        ed_huowei.setText(content);
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
                    XToastUtil.showToast(PandianActivity.this, "请求超时！");
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
