package com.truking.wms.tool.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.truking.wms.tool.R;
import com.truking.wms.tool.adapters.ShipmentdetailAdapter;
import com.truking.wms.tool.lanyadayingUtils.DeviceConnFactoryManager;
import com.truking.wms.tool.lanyadayingUtils.ThreadPool;
import com.truking.wms.tool.utils.LoadingStaticDialog;
import com.truking.wms.tool.utils.PreferenceHelper;
import com.truking.wms.tool.utils.XToastUtil;
import com.yzq.zxinglibrary.android.CaptureActivity;
import com.yzq.zxinglibrary.common.Constant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class BocibiaoqianActivity extends AppCompatActivity implements View.OnClickListener{
    private static final int PHOTO_REQUEST_SAOYISAO = 1;
    private static final int REQUEST_CODE_SCAN = 2;
    /**
     * 蓝牙请求码
     */
    public static final int BLUETOOTH_REQUEST_CODE = 0x006;

    /**
     * 打印机是否连接
     */
    private static final int CONN_PRINTER = 0x003;
    /**
     * 使用打印机指令错误
     */
    private static final int PRINTER_COMMAND_ERROR = 0x004;

    /**
     * 连接状态断开
     */
    private static final int CONN_STATE_DISCONN = 0x005;
    private ThreadPool threadPool;//线程
    /**
     * 判断打印机所使用指令是否是ESC指令
     */
    private int id = 0;

    boolean isInitTab = false;
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;

    private String taskCode;//任务号
    private String warehouseCode;//仓库
    private String lx;//类型
    private String shiCode;//出库单号
    private String ck;//发货仓库
    private String zc_warehousecode;//总单数-仓库
    private String waveid;//波次号
    private String mblnr;//领料申请单
    private String shipToName_shipToFaxNum;//申请人/电话
    private String shipToAddress1;//地址
    private String aufnr;//工单
    private String maktx;//产品
    private String userDef6;//工序描述
    private String userDef2_userDef1;//工作中心
    private String userDef8_userDef7;//项目
    @BindView(R.id.tv_taskcode)
    public TextView tv_taskcode;
    @BindView(R.id.tv_lx)
    public TextView tv_lx;
    @BindView(R.id.tv_shiCode)
    public TextView tv_shiCode;
    @BindView(R.id.tv_ck)
    public TextView tv_ck;
    @BindView(R.id.tv_zc_warehousecode)
    public TextView tv_zc_warehousecode;
    @BindView(R.id.tv_waveid)
    public TextView tv_waveid;
    @BindView(R.id.tv_mblnr)
    public TextView tv_mblnr;
    @BindView(R.id.tv_shipToName_shipToFaxNum)
    public TextView tv_shipToName_shipToFaxNum;
    @BindView(R.id.tv_shipToAddress1)
    public TextView tv_shipToAddress1;
    @BindView(R.id.tv_aufnr)
    public TextView tv_aufnr;
    @BindView(R.id.tv_maktx)
    public TextView tv_maktx;
    @BindView(R.id.tv_userDef6)
    public TextView tv_userDef6;
    @BindView(R.id.tv_userDef2_userDef1)
    public TextView tv_userDef2_userDef1;
    @BindView(R.id.tv_userDef8_userDef7)
    public TextView tv_tv_userDef8_userDef7;

    private RecyclerView recyclerView;
    private ShipmentdetailAdapter adapter;
    private JSONArray array;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MyApp.getActivities().add(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bocibiaoqian);
        ButterKnife.bind(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        warehouseCode = getIntent().getStringExtra("warehouseCode");
        taskCode = getIntent().getStringExtra("taskCode");
        tv_taskcode.setText(taskCode);
        LoadingStaticDialog.showLoadingDialog(BocibiaoqianActivity.this, "获取中，请稍等！");
        startTimer();
        missDialog = true;
        OkHttpClient client = new OkHttpClient();
        String url = "http://172.16.1.131:9090/api/app/bocibiaoqian?userName="+PreferenceHelper.getUserId()+
                "&token="+PreferenceHelper.getToken()+"&taskCode="+taskCode+"&warehouseCode="+warehouseCode;
        Request build = new Request.Builder().url(url).build();
        Call call = client.newCall(build);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Looper.prepare();
                XToastUtil.showToast(BocibiaoqianActivity.this,"失败！"+e.getLocalizedMessage());
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
                    int codeResult = json.optInt("code");
                    if(codeResult != 200){
                        String msg = json.optString("info");
                        Looper.prepare();
                        missDialog = false;
                        LoadingStaticDialog.loadDialogDismiss();
                        stopTimer();
                        XToastUtil.showToast(BocibiaoqianActivity.this,msg);
                        Looper.loop();
                        return;
                    }
                    array = json.getJSONArray("data");
                    if(array != null && array.length() > 0){
                        JSONObject obj = (JSONObject) array.get(0);
                        lx = obj.optString("lx");
                        shiCode = obj.optString("shiCode");
                        ck = obj.optString("ck");
                        zc_warehousecode = obj.optString("ZS")+"-"+obj.optString("warehouseCode");
                        waveid = obj.optString("waveId");
                        mblnr = obj.optString("mblnr");
                        shipToName_shipToFaxNum = obj.optString("shipToName")+"/"+obj.optString("shipToFaxNum");
                        shipToAddress1 = obj.optString("shipToAddress1");
                        aufnr = obj.optString("aufnr");
                        maktx = obj.optString("maktx");
                        userDef6 = obj.optString("userDef6");
                        userDef2_userDef1 = obj.optString("userDef2")+"-"+obj.optString("userDef1");
                        userDef8_userDef7 = obj.optString("userDef8")+"-"+obj.optString("userDef7");
                    }
                    Looper.prepare();
                    missDialog = false;
                    LoadingStaticDialog.loadDialogDismiss();
                    stopTimer();
                    //设置值
                    tv_lx.setText(lx);
                    tv_shiCode.setText(shiCode);
                    tv_ck.setText(ck);
                    tv_zc_warehousecode.setText(zc_warehousecode);
                    tv_waveid.setText(waveid);
                    tv_mblnr.setText(mblnr);
                    tv_shipToName_shipToFaxNum.setText(shipToName_shipToFaxNum);
                    tv_shipToAddress1.setText(shipToAddress1);
                    tv_aufnr.setText(aufnr);
                    tv_maktx.setText(maktx);
                    tv_userDef6.setText(userDef6);
                    tv_userDef2_userDef1.setText(userDef2_userDef1);
                    tv_tv_userDef8_userDef7.setText(userDef8_userDef7);
                    Message message = handler.obtainMessage();
                    message.what = 1;
                    handler.sendMessage(message);
                    Looper.loop();
//                                    new Thread(new Runnable() {
//                                        @Override
//                                        public void run() {
//                                            //使用 handler 发送空消息
//                                            Message message = handler.obtainMessage();
//                                            message.what = 1;
//                                            handler.sendMessage(message);
//                                        }
//                                    }).start();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @OnClick({R.id.back})
    public void onViewClicked(View view){
        switch (view.getId()) {
            case R.id.back:{
                this.finish();
                break;
            }
        }

    }

    /**
     * 重新连接回收上次连接的对象，避免内存泄漏
     */
    private void closePort() {
        if (DeviceConnFactoryManager.getDeviceConnFactoryManagers()[id] != null && DeviceConnFactoryManager.getDeviceConnFactoryManagers()[id].mPort != null) {
            DeviceConnFactoryManager.getDeviceConnFactoryManagers()[id].reader.cancel();
            DeviceConnFactoryManager.getDeviceConnFactoryManagers()[id].mPort.closePort();
            DeviceConnFactoryManager.getDeviceConnFactoryManagers()[id].mPort = null;
        }
    }


    @Override
    protected void onStop() {
        super.onStop();
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
                        tv_taskcode.setText(content);
                        LoadingStaticDialog.showLoadingDialog(BocibiaoqianActivity.this, "操作中，请稍等！");
                        startTimer();
                        missDialog = true;
                        OkHttpClient client = new OkHttpClient();
                        String url = "http://172.16.1.131:9090/api/app/bocibiaoqian?userName="+PreferenceHelper.getUserId()+
                                "&token="+PreferenceHelper.getToken()+"&taskcode="+content;
                        Request build = new Request.Builder().url(url).build();
                        Call call = client.newCall(build);
                        call.enqueue(new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                Looper.prepare();
                                XToastUtil.showToast(BocibiaoqianActivity.this,"失败！"+e.getLocalizedMessage());
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
                                    int codeResult = json.optInt("code");
                                    if(codeResult != 200){
                                        String msg = json.optString("info");
                                        Looper.prepare();
                                        missDialog = false;
                                        LoadingStaticDialog.loadDialogDismiss();
                                        stopTimer();
                                        XToastUtil.showToast(BocibiaoqianActivity.this,msg);
                                        Looper.loop();
                                        return;
                                    }
                                    array = json.getJSONArray("data");
                                    if(array != null && array.length() > 0){
                                        JSONObject obj = (JSONObject) array.get(0);
                                        lx = obj.optString("lx");
                                        shiCode = obj.optString("shiCode");
                                        ck = obj.optString("ck");
                                        zc_warehousecode = obj.optString("ZS")+"-"+obj.optString("warehouseCode");
                                        waveid = obj.optString("waveId");
                                        mblnr = obj.optString("mblnr");
                                        shipToName_shipToFaxNum = obj.optString("shipToName")+"/"+obj.optString("shipToFaxNum");
                                        shipToAddress1 = obj.optString("shipToAddress1");
                                        aufnr = obj.optString("aufnr");
                                        maktx = obj.optString("maktx");
                                        userDef6 = obj.optString("userDef6");
                                        userDef2_userDef1 = obj.optString("userDef2")+"-"+obj.optString("userDef1");
                                        userDef8_userDef7 = obj.optString("userDef8")+"-"+obj.optString("userDef7");
                                    }
                                    Looper.prepare();
                                    missDialog = false;
                                    LoadingStaticDialog.loadDialogDismiss();
                                    stopTimer();
                                    //设置值
                                    tv_lx.setText(lx);
                                    tv_shiCode.setText(shiCode);
                                    tv_ck.setText(ck);
                                    tv_zc_warehousecode.setText(zc_warehousecode);
                                    tv_waveid.setText(waveid);
                                    tv_mblnr.setText(mblnr);
                                    tv_shipToName_shipToFaxNum.setText(shipToName_shipToFaxNum);
                                    tv_shipToAddress1.setText(shipToAddress1);
                                    tv_aufnr.setText(aufnr);
                                    tv_maktx.setText(maktx);
                                    tv_userDef6.setText(userDef6);
                                    tv_userDef2_userDef1.setText(userDef2_userDef1);
                                    tv_tv_userDef8_userDef7.setText(userDef8_userDef7);
                                    Message message = handler.obtainMessage();
                                    message.what = 1;
                                    handler.sendMessage(message);
                                    Looper.loop();
//                                    new Thread(new Runnable() {
//                                        @Override
//                                        public void run() {
//                                            //使用 handler 发送空消息
//                                            Message message = handler.obtainMessage();
//                                            message.what = 1;
//                                            handler.sendMessage(message);
//                                        }
//                                    }).start();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                }
                break;
            //蓝牙连接
            case BLUETOOTH_REQUEST_CODE:{
                if(intent != null){
                    closePort();
                    //获取蓝牙mac地址
                    String macAddress = intent.getStringExtra(BluetoothListActivity.EXTRA_DEVICE_ADDRESS);
                    //初始化DeviceConnFactoryManager 并设置信息
                    new DeviceConnFactoryManager.Build()
                            //设置标识符
                            .setId(id)
                            //设置连接方式
                            .setConnMethod(DeviceConnFactoryManager.CONN_METHOD.BLUETOOTH)
                            //设置连接的蓝牙mac地址
                            .setMacAddress(macAddress)
                            .build();
                    //配置完信息，就可以打开端口连接了
                    Log.i("TAG", "onActivityResult: 连接蓝牙" + id);
                    threadPool = ThreadPool.getInstantiation();
                    threadPool.addTask(new Runnable() {
                        @Override
                        public void run() {
                            DeviceConnFactoryManager.getDeviceConnFactoryManagers()[id].openPort();
                        }
                    });
                }
                break;
            }
            default:
                if(intent != null && resultCode != RESULT_OK){
                    XToastUtil.showToast(this, "二维码识别失败！");
                }
                break;
        }
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Log.i("66666666666",msg.toString());
            if (msg.what == 1) {
                adapter = new ShipmentdetailAdapter(BocibiaoqianActivity.this,array);
                recyclerView.setAdapter(adapter);
            }
            super.handleMessage(msg);
        }
    };

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
                    XToastUtil.showToast(BocibiaoqianActivity.this, "请求超时！");
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


    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("TAG", "onDestroy");
        DeviceConnFactoryManager.closeAllPort();
        if (threadPool != null) {
            threadPool.stopThreadPool();
            threadPool = null;
        }
    }


}
