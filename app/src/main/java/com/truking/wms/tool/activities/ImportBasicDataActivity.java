package com.truking.wms.tool.activities;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.truking.wms.tool.R;
import com.truking.wms.tool.utils.ButtonUtils;
import com.truking.wms.tool.utils.LoadingStaticDialog;
import com.truking.wms.tool.utils.NetworkUtil;
import com.truking.wms.tool.utils.PreferenceHelper;
import com.truking.wms.tool.utils.StatusBarUtil;
import com.truking.wms.tool.utils.Toolbar;
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
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ImportBasicDataActivity extends AppCompatActivity implements View.OnClickListener{
    private static final int PHOTO_REQUEST_SAOYISAO = 1;
    private static final int REQUEST_CODE_SCAN = 2;
    boolean isInitTab = false;
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;
    private static final String[] m={"1","2","3","4","??????"};
    private ArrayAdapter<String> adapter;
    @BindView(R.id.btn_send)
    public Button submit;

    private String wuliaobianma;//????????????
    private String xiangxing;//??????
    private Double gekouzaizhong;//????????????
    private Double maozhong;//??????
    private int gekouzailiang;//????????????
    @BindView(R.id.wuliaobianma)
    public EditText ed_wuliaobianma;
    @BindView(R.id.gekouzaizhong)
    public TextView tv_gekouzaizhong;
    @BindView(R.id.maozhong)
    public EditText ed_maozhong;
    @BindView(R.id.gekouzailiang)
    public EditText ed_gekouzailiang;
    private NiceSpinner niceSpinner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MyApp.getActivities().add(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_importbasicdata);
        ButterKnife.bind(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        gekouzaizhong = 50.0;
        tv_gekouzaizhong.setText(gekouzaizhong+"");

        niceSpinner = (NiceSpinner) findViewById(R.id.nice_spinner);
        List<String> dataset = new LinkedList<>(Arrays.asList("1", "2", "4"));
        niceSpinner.attachDataSource(dataset);
        niceSpinner.setOnSpinnerItemSelectedListener(new OnSpinnerItemSelectedListener() {
            @Override
            public void onItemSelected(NiceSpinner parent, View view, int position, long id) {
                // This example uses String, but your type can be any
                xiangxing = (String) parent.getItemAtPosition(position);
                if(xiangxing.equals("1")){
                    tv_gekouzaizhong.setText("50.0");
                }else if(xiangxing.equals("2")){
                    tv_gekouzaizhong.setText("25.0");
                }else if(xiangxing.equals("4")){
                    tv_gekouzaizhong.setText("12.5");
                }
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
                    String gekouzailiangTemp = ed_gekouzailiang.getText().toString();
                    String gekouzaizhongTemp = tv_gekouzaizhong.getText().toString();
                    String maozhongTemp = ed_maozhong.getText().toString();
                    if(wuliaobianma == null || wuliaobianma != null && "".equals(wuliaobianma)
                            || gekouzailiangTemp == null || gekouzailiangTemp != null && "".equals(gekouzailiangTemp)
                            || gekouzaizhongTemp == null || gekouzaizhongTemp != null && "".equals(gekouzaizhongTemp)
                            || maozhongTemp == null || maozhongTemp != null && "".equals(maozhongTemp)){
                        XToastUtil.showToast(this,"????????????????????????????????????");
                        return;
                    }
                    gekouzailiang = Integer.parseInt(ed_gekouzailiang.getText().toString());
                    gekouzaizhong = Double.parseDouble(tv_gekouzaizhong.getText().toString());
                    maozhong = Double.parseDouble(ed_maozhong.getText().toString());
                    if(xiangxing == null){
                        xiangxing = niceSpinner.getSelectedItem().toString();
                    }
                    //????????????*??????<=????????????
                    if(gekouzailiang * maozhong > gekouzaizhong){
                        XToastUtil.showToast(this,"?????????");
                        return;
                    }
                    LoadingStaticDialog.showLoadingDialog(ImportBasicDataActivity.this, "????????????????????????");
                    startTimer();
                    missDialog = true;
//                    //????????????
//                    String url="http://172.16.1.131:9090/api/app/wcsbasicdata";
//                    JSONObject parameters = new JSONObject();
//                    //????????????
//                    try {
//                        parameters.put("skuCode", wuliaobianma);
//                        parameters.put("compartmentsAmount", gekouzailiang);
//                        parameters.put("compartmentsWeight", tv_gekouzaizhong.getText().toString());
//                        parameters.put("grossWeight", ed_maozhong.getText().toString());
//                        parameters.put("numCompartments", Integer.parseInt(xiangxing));
//                        parameters.put("userName", PreferenceHelper.getUserId());
//                        parameters.put("token", PreferenceHelper.getToken());
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                    NetworkUtil networkUtil = new NetworkUtil();
//                    networkUtil.setCallback(
//                            new okhttp3.Callback() {
//                                @Override
//                                public void onFailure(Call call, IOException e) {
//                                    Looper.prepare();
//                                    XToastUtil.showToast(ImportBasicDataActivity.this,"?????????"+e.getLocalizedMessage());
//                                    missDialog = false;
//                                    LoadingStaticDialog.loadDialogDismiss();
//                                    stopTimer();
//                                    Looper.loop();
//                                }
//
//                                @Override
//                                public void onResponse(Call call, okhttp3.Response response) throws IOException {
//                                    Looper.prepare();
//                                    String result = response.body().string();
//                                    Log.i("8888888888",result);
//                                    missDialog = false;
//                                    LoadingStaticDialog.loadDialogDismiss();
//                                    stopTimer();
//                                    JSONObject jsonObject = null;
//                                    try {
//                                        jsonObject = new JSONObject(result);
//                                        int code = jsonObject.getInt("code");
//                                        String msg = jsonObject.getString("info");
//                                        XToastUtil.showToast(ImportBasicDataActivity.this,msg);
//                                    } catch (JSONException e) {
//                                        e.printStackTrace();
//                                    }
//                                    Looper.loop();
//                                }
//                            });
//                    networkUtil.post(url,parameters);

                    //?????????????????????????????????????????????????????????????????????
                    MediaType JSON = MediaType.parse("application/json;charset=utf-8");
                    JSONArray array = new JSONArray();
                    JSONObject object = new JSONObject();
                    try {
                        object.put("skuCode",wuliaobianma);
                        object.put("numCompartments",Integer.parseInt(xiangxing));
                        object.put("compartmentsAmount",gekouzailiang);
                        object.put("grossWeight",ed_maozhong.getText().toString());
                        object.put("compartmentsWeight",tv_gekouzaizhong.getText().toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    array.put(object);
                    //??????????????????????????????json???
                    //????????????OkHttpClient??????
                    OkHttpClient okHttpClient = new OkHttpClient();
                    //????????????RequestBody(??????1??????????????? ??????2?????????json???)
                    //json???String?????????json??????
                    RequestBody requestBody = RequestBody.create(JSON, String.valueOf(array));
                    //????????????????????????
//                        String format = String.format(KeyPath.Path.head + KeyPath.Path.waybillinfosensor, username, key, current_timestamp);
                    Request request = new Request.Builder()
                            .url("http://172.16.1.110/core/api/haiq/LOAD_CONF_ADD")
                            .post(requestBody)
                            .build();

                    okHttpClient.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            Looper.prepare();
                            XToastUtil.showToast(ImportBasicDataActivity.this,"?????????"+e.getLocalizedMessage());
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
                                String message = json.optString("message");
                                String code = json.optString("code");
                                Looper.prepare();
                                missDialog = false;
                                LoadingStaticDialog.loadDialogDismiss();
                                stopTimer();
                                XToastUtil.showToast(ImportBasicDataActivity.this,message);
                                Looper.loop();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
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
                            //?????????????????????????????????????????????????????????????????????
                            MediaType JSON = MediaType.parse("application/json;charset=utf-8");
                            JSONArray array = new JSONArray();
                            array.put(skucode);
                            //??????????????????????????????json???
                            //????????????OkHttpClient??????
                            OkHttpClient okHttpClient = new OkHttpClient();
                            //????????????RequestBody(??????1??????????????? ??????2?????????json???)
                            //json???String?????????json??????
                            RequestBody requestBody = RequestBody.create(JSON, String.valueOf(array));
                            //????????????????????????
//                        String format = String.format(KeyPath.Path.head + KeyPath.Path.waybillinfosensor, username, key, current_timestamp);
                            Request request = new Request.Builder()
                                    .url("http://172.16.1.110/core/api/haiq/LOAD_CONF_QUERY")
                                    .post(requestBody)
                                    .build();

                            okHttpClient.newCall(request).enqueue(new Callback() {
                                @Override
                                public void onFailure(Call call, IOException e) {
                                    //DialogUtils.showPopMsgInHandleThread(Release_Fragment.this.getContext(), mHandler, "???????????????????????????????????????");
                                }

                                @Override
                                public void onResponse(Call call, Response response) throws IOException {
                                    String string = response.body().string();
                                    Log.i("info",string+"");
                                    try {
                                        JSONObject json = new JSONObject(string);
                                        String message = json.optString("message");
                                        String code = json.optString("code");
                                        if("0".equals(code)){//????????????
                                            String data = json.optString("data");
                                            JSONArray jsonArray = new JSONArray(data);
                                            if(jsonArray.length() == 0){
                                                ImportBasicDataActivity.this.runOnUiThread(new Runnable() {
                                                    public void run() {
                                                        tv_gekouzaizhong.setText("50");
                                                        niceSpinner.setSelectedIndex(0);
                                                        ed_maozhong.setText("");
                                                        ed_gekouzailiang.setText("");
                                                    }
                                                });
                                                Looper.prepare();
                                                XToastUtil.showToast(ImportBasicDataActivity.this,"?????????!");
                                                Looper.loop();
                                                return;
                                            }
                                            //JSONArray??????
                                            for(int i=0; i<jsonArray.length(); i++) {
                                                JSONObject jsonObj = jsonArray.getJSONObject(i);
                                                String numCompartments = jsonObj.get("numCompartments").toString();
                                                String compartmentsAmount = jsonObj.get("compartmentsAmount").toString();
                                                String grossWeight = jsonObj.get("grossWeight").toString();
                                                String compartmentsWeight = jsonObj.get("compartmentsWeight").toString();
                                                ImportBasicDataActivity.this.runOnUiThread(new Runnable() {
                                                    public void run() {
                                                        tv_gekouzaizhong.setText(compartmentsWeight);
                                                        if("1".equals(numCompartments)){
                                                            niceSpinner.setSelectedIndex(0);
                                                        }else if("2".equals(numCompartments)){
                                                            niceSpinner.setSelectedIndex(1);
                                                        }else if("4".equals(numCompartments)){
                                                            niceSpinner.setSelectedIndex(2);
                                                        }
                                                        ed_maozhong.setText(grossWeight);
                                                        ed_gekouzailiang.setText(compartmentsAmount);
                                                    }
                                                });
                                                break;
                                            }
                                        }else{
                                            Looper.prepare();
                                            XToastUtil.showToast(ImportBasicDataActivity.this,"????????????!"+message);
                                            Looper.loop();
                                        }
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
                    XToastUtil.showToast(ImportBasicDataActivity.this, "???????????????");
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
