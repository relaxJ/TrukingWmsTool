package com.truking.wms.tool.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.truking.wms.tool.R;
import com.truking.wms.tool.utils.ButtonUtils;
import com.truking.wms.tool.utils.LoadingStaticDialog;
import com.truking.wms.tool.utils.MyDialog;
import com.truking.wms.tool.utils.MyDialog2;
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

public class DafuImportBasicDataActivity extends AppCompatActivity implements View.OnClickListener{
    private static final int PHOTO_REQUEST_SAOYISAO = 1;
    private static final int REQUEST_CODE_SCAN = 2;
    boolean isInitTab = false;
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;
    private static final String[] xiaolei1={"??????","?????????","?????????","?????????"};
    private ArrayAdapter<String> adapter;
    @BindView(R.id.btn_send)
    public Button submit;

    private String wuliaobianma;//????????????
    private String wuliaomingcheng;//????????????
    private String rongqidalei;//????????????
    private String rongqixiaolei;//????????????
    private String shuliang;//??????
    private String danwei;//??????
    @BindView(R.id.wuliaobianma)
    public TextView ed_wuliaobianma;
    @BindView(R.id.wuliaomingcheng)
    public TextView tv_wuliaomingcheng;
    @BindView(R.id.danwei)
    public TextView tv_danwei;
    @BindView(R.id.shuliang)
    public EditText ed_shuliang;
    private NiceSpinner niceSpinnerdalei;
    private NiceSpinner niceSpinnerxiaolei;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MyApp.getActivities().add(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dafuimportbasicdata);
        ButterKnife.bind(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        rongqidalei = "?????????";
        rongqixiaolei = "??????";
        niceSpinnerdalei = (NiceSpinner) findViewById(R.id.nice_spinner_dalei);
        niceSpinnerxiaolei = (NiceSpinner) findViewById(R.id.nice_spinner_xiaolei);
        List<String> datasetdalei = new LinkedList<>(Arrays.asList("?????????", "?????????", "??????"));
        List<String> datasetxiaolei1 = new LinkedList<>(Arrays.asList("??????", "?????????", "?????????","?????????"));
        List<String> datasetxiaolei2 = new LinkedList<>(Arrays.asList("??????", "??????"));
        niceSpinnerdalei.attachDataSource(datasetdalei);
        niceSpinnerxiaolei.attachDataSource(datasetxiaolei1);
        niceSpinnerdalei.setOnSpinnerItemSelectedListener(new OnSpinnerItemSelectedListener() {
            @Override
            public void onItemSelected(NiceSpinner parent, View view, int position, long id) {
                // This example uses String, but your type can be any
                rongqidalei = (String) parent.getItemAtPosition(position);
                if(rongqidalei.equals("?????????")||rongqidalei.equals("?????????")){
                    niceSpinnerxiaolei.attachDataSource(datasetxiaolei1);
                }else if(rongqidalei.equals("??????")){
                    niceSpinnerxiaolei.attachDataSource(datasetxiaolei2);
                }
            }
        });
        niceSpinnerxiaolei.setOnSpinnerItemSelectedListener(new OnSpinnerItemSelectedListener() {
            @Override
            public void onItemSelected(NiceSpinner parent, View view, int position, long id) {
                // This example uses String, but your type can be any
                rongqixiaolei = (String) parent.getItemAtPosition(position);
            }
        });
    }

    @OnClick({R.id.btn_send,R.id.scan,R.id.back})
    public void onViewClicked(View view){
        switch (view.getId()) {
            case R.id.btn_send: {
                if(!ButtonUtils.isFastDoubleClick(R.id.btn_send)){
                    //?????????
                    wuliaobianma = ed_wuliaobianma.getText().toString();
                    shuliang = ed_shuliang.getText().toString();
                    if(wuliaobianma == null || wuliaobianma != null && "".equals(wuliaobianma)
                            || shuliang == null || shuliang != null && "".equals(shuliang)){
                        XToastUtil.showToast(this,"????????????????????????????????????");
                        return;
                    }
                    LoadingStaticDialog.showLoadingDialog(DafuImportBasicDataActivity.this, "????????????????????????");
                    startTimer();
                    missDialog = true;
                    //????????????
                    String url="http://172.16.1.131:9090/api/app/dafuwcsbasicdata";
                    JSONObject parameters = new JSONObject();
                    //????????????
                    try {
                        parameters.put("wuliaobianma", wuliaobianma);
                        parameters.put("wuliaomingcheng", wuliaomingcheng);
                        parameters.put("danwei", danwei);
                        parameters.put("rongqidalei", rongqidalei);
                        parameters.put("rongqixiaolei", rongqixiaolei);
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
                            new okhttp3.Callback() {
                                @Override
                                public void onFailure(Call call, IOException e) {
                                    Looper.prepare();
                                    XToastUtil.showToast(DafuImportBasicDataActivity.this,"?????????"+e.getLocalizedMessage());
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
                                        XToastUtil.showToast(DafuImportBasicDataActivity.this,msg);
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
                // ??????????????????
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, PHOTO_REQUEST_SAOYISAO);
                }else{
                    // ???????????????
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
                    // ??????????????????????????????
                    // ???????????????
                    Intent intent = new Intent(this,
                            CaptureActivity.class);
                    startActivityForResult(intent, REQUEST_CODE_SCAN);
                }
        }
    }

    /**
     * ????????????????????????????????????
     **/
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        switch (requestCode){
            // ??????????????????
            case REQUEST_CODE_SCAN:
                if(intent != null){
                    if(resultCode == RESULT_OK) {
                        String content = intent.getStringExtra(Constant.CODED_CONTENT);
                        String[] arrays = content.split(",",-1);
                        if(arrays.length == 6){
                            String skucode = arrays[3];
                            ed_wuliaobianma.setText(arrays[3]);
                            LoadingStaticDialog.showLoadingDialog(DafuImportBasicDataActivity.this, "????????????????????????");
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
                                    XToastUtil.showToast(DafuImportBasicDataActivity.this,"?????????"+e.getLocalizedMessage());
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
                                            wuliaomingcheng = jsonData.optString("sapwuliaomiaoshu");//????????????
                                            danwei = jsonData.optString("jiliangdanwei");//??????
                                            rongqidalei = jsonData.optString("rongqidalei");
                                            rongqixiaolei = jsonData.optString("rongqixiaolei");
                                            shuliang = jsonData.optString("shuliang");
                                            //?????????
                                            tv_danwei.setText(danwei);
                                            ed_shuliang.setText(shuliang);
                                            tv_wuliaomingcheng.setText(wuliaomingcheng);
                                            if("?????????".equals(rongqidalei)){
                                                niceSpinnerdalei.setSelectedIndex(0);
                                            }else if("?????????".equals(rongqidalei)){
                                                niceSpinnerdalei.setSelectedIndex(1);
                                            }else if("??????".equals(rongqidalei)){
                                                niceSpinnerdalei.setSelectedIndex(2);
                                            }
                                            if("??????".equals(rongqixiaolei) || "??????".equals(rongqixiaolei)){
                                                niceSpinnerxiaolei.setSelectedIndex(0);
                                            }else if("?????????".equals(rongqixiaolei) || "??????".equals(rongqixiaolei)){
                                                niceSpinnerxiaolei.setSelectedIndex(1);
                                            }else if("?????????".equals(rongqixiaolei)){
                                                niceSpinnerxiaolei.setSelectedIndex(2);
                                            }else if("?????????".equals(rongqixiaolei) ){
                                                niceSpinnerxiaolei.setSelectedIndex(3);
                                            }
                                            //????????????
                                            //??????
                                            MyDialog2 dialog = new MyDialog2(DafuImportBasicDataActivity.this,new
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
                                            wuliaomingcheng = jsonData.optString("sapwuliaomiaoshu");//????????????
                                            danwei = jsonData.optString("jiliangdanwei");//??????
                                            //?????????
                                            tv_danwei.setText(danwei);
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
                    XToastUtil.showToast(this, "????????????????????????");
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
                    XToastUtil.showToast(DafuImportBasicDataActivity.this, "???????????????");
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
