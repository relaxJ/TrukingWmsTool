package com.truking.wms.tool.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.truking.wms.tool.R;
import com.truking.wms.tool.lanyadayingUtils.DeviceConnFactoryManager;
import com.truking.wms.tool.lanyadayingUtils.ThreadPool;
import com.truking.wms.tool.utils.ButtonUtils;
import com.truking.wms.tool.utils.LoadingStaticDialog;
import com.truking.wms.tool.utils.PreferenceHelper;
import com.truking.wms.tool.utils.PrinterCommand;
import com.truking.wms.tool.utils.XToastUtil;
import com.truking.wms.tool.utils.com.printer.command.EscCommand;
import com.truking.wms.tool.utils.com.printer.command.LabelCommand;
import com.yzq.zxinglibrary.android.CaptureActivity;
import com.yzq.zxinglibrary.common.Constant;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Vector;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.hardware.usb.UsbManager.ACTION_USB_DEVICE_DETACHED;
import static com.truking.wms.tool.lanyadayingUtils.DeviceConnFactoryManager.ACTION_QUERY_PRINTER_STATE;
import static com.truking.wms.tool.lanyadayingUtils.DeviceConnFactoryManager.CONN_STATE_FAILED;

public class CangkutiaozhengActivity extends AppCompatActivity implements View.OnClickListener{
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
    private ArrayAdapter<String> adapter;
    @BindView(R.id.btn_search)
    public Button btn_search;
    @BindView(R.id.btn_daying)
    public Button btn_daying;
    @BindView(R.id.btn_duocidaying)
    public Button btn_duocidaying;

    private String xinwuliaobianma;//物料编码
    private String jiuwuliaobianma;//旧物料编码
    private String wuliaomingcheng;//物料名称
    private String tuhaoguige;//图号规格
    private String danwei;//单位
    @BindView(R.id.wuliaobianma)
    public EditText ed_wuliaobianma;
    @BindView(R.id.jiuwuliaobianma)
    public TextView tv_jiuwuliaobianma;
    @BindView(R.id.wuliaomingcheng)
    public TextView tv_wuliaomingcheng;
    @BindView(R.id.tuhaoguige)
    public TextView tv_tuhaoguige;
    @BindView(R.id.danwei)
    public TextView tv_danwei;
    @BindView(R.id.tv_state)
    public TextView tv_state;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MyApp.getActivities().add(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wuliaobiaoqiandaying);
        ButterKnife.bind(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

    }

    @OnClick({R.id.btn_search,R.id.btn_daying,R.id.btn_connect,R.id.btn_duocidaying,R.id.scan,})
    public void onViewClicked(View view){
        switch (view.getId()) {
            case R.id.btn_connect:{
                startActivityForResult(new Intent(CangkutiaozhengActivity.this, BluetoothListActivity.class), BLUETOOTH_REQUEST_CODE);
                break;
            }
            case R.id.btn_daying:{
                if(!isConnected){
                    XToastUtil.showToast(CangkutiaozhengActivity.this,"请先连接打印机!");
                    break;
                }
                printLabel();
                break;
            }
            case R.id.btn_duocidaying:{
                if(!isConnected){
                    XToastUtil.showToast(CangkutiaozhengActivity.this,"请先连接打印机!");
                    break;
                }
                startActivityForResult(new Intent(CangkutiaozhengActivity.this, InputDayingNumberActivity.class), 777);
                break;
            }
            case R.id.btn_search: {
                if(!ButtonUtils.isFastDoubleClick(R.id.btn_search)){
                    //做校验
                    xinwuliaobianma = ed_wuliaobianma.getText().toString();
                    if(xinwuliaobianma == null || xinwuliaobianma != null && "".equals(xinwuliaobianma)){
                        XToastUtil.showToast(this,"新物料编码为空！无法提交！");
                        return;
                    }
                    LoadingStaticDialog.showLoadingDialog(CangkutiaozhengActivity.this, "操作中，请稍等！");
                    startTimer();
                    missDialog = true;

                    OkHttpClient client = new OkHttpClient();
                    String url = "http://172.16.1.131:9090/api/app/data?userName="+PreferenceHelper.getUserId()+
                            "&token="+PreferenceHelper.getToken()+"&partNumber="+xinwuliaobianma;
                    Request build = new Request.Builder().url(url).build();
                    Call call = client.newCall(build);
                    call.enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            Looper.prepare();
                            XToastUtil.showToast(CangkutiaozhengActivity.this,"失败！"+e.getLocalizedMessage());
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
    protected void onStart() {
        super.onStart();
        /*
         * 注册接收连接状态的广播
         */
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_QUERY_PRINTER_STATE);
        filter.addAction(DeviceConnFactoryManager.ACTION_CONN_STATE);
        registerReceiver(receiver, filter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(receiver);

    }

    private boolean isConnected;
    /**
     * 连接状态的广播
     */
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (DeviceConnFactoryManager.ACTION_CONN_STATE.equals(action)) {
                int state = intent.getIntExtra(DeviceConnFactoryManager.STATE, -1);
                int deviceId = intent.getIntExtra(DeviceConnFactoryManager.DEVICE_ID, -1);
                switch (state) {
                    case DeviceConnFactoryManager.CONN_STATE_DISCONNECT:
                        if (id == deviceId) tv_state.setText("未连接");
                        break;
                    case DeviceConnFactoryManager.CONN_STATE_CONNECTING:
                        tv_state.setText("连接中");
                        break;
                    case DeviceConnFactoryManager.CONN_STATE_CONNECTED:
                        tv_state.setText("已连接");
                        tv_state.setTextColor(getResources().getColor(R.color.colorPrimary));
                        isConnected = true;
                        Toast.makeText(CangkutiaozhengActivity.this, "已连接", Toast.LENGTH_SHORT).show();
                        break;
                    case CONN_STATE_FAILED:
                        tv_state.setText("未连接");
                        Toast.makeText(CangkutiaozhengActivity.this, "连接失败！重试或重启打印机试试", Toast.LENGTH_SHORT).show();
                        break;
                }
                /* Usb连接断开、蓝牙连接断开广播 */
            } else if (ACTION_USB_DEVICE_DETACHED.equals(action)) {
                mHandler.obtainMessage(CONN_STATE_DISCONN).sendToTarget();
            }
        }
    };

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case CONN_STATE_DISCONN:
                    if (DeviceConnFactoryManager.getDeviceConnFactoryManagers()[id] != null || !DeviceConnFactoryManager.getDeviceConnFactoryManagers()[id].getConnState()) {
                        DeviceConnFactoryManager.getDeviceConnFactoryManagers()[id].closePort(id);
                        Toast.makeText(CangkutiaozhengActivity.this, "成功断开连接", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case PRINTER_COMMAND_ERROR:
                    Toast.makeText(CangkutiaozhengActivity.this, "请选择正确的打印机指令", Toast.LENGTH_SHORT).show();
                    break;
                case CONN_PRINTER:
                    Toast.makeText(CangkutiaozhengActivity.this, "请先连接打印机", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

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
            case 777:{
                if(intent != null) {
                    String numberStr = intent.getStringExtra("number");
                    int number = Integer.parseInt(numberStr);
                    printLabel3(number);
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

    public void printLabel() {
        Log.i("TAG", "准备打印");
        threadPool = ThreadPool.getInstantiation();
        threadPool.addTask(new Runnable() {
            @Override
            public void run() {
                //先判断打印机是否连接
                if (DeviceConnFactoryManager.getDeviceConnFactoryManagers()[id] == null ||
                        !DeviceConnFactoryManager.getDeviceConnFactoryManagers()[id].getConnState()) {
                    mHandler.obtainMessage(CONN_PRINTER).sendToTarget();
                    return;
                }
                if (DeviceConnFactoryManager.getDeviceConnFactoryManagers()[id].getCurrentPrinterCommand() == PrinterCommand.TSC) {
                    Log.i("TAG", "开始打印");
                    sendLabel();
                } else {
                    mHandler.obtainMessage(PRINTER_COMMAND_ERROR).sendToTarget();
                }
            }
        });
    }

    private void sendLabel() {
        LabelCommand tsc = new LabelCommand();
        tsc.addSize(70, 50); // 设置标签尺寸，按照实际尺寸设置
        tsc.addGap(1); // 设置标签间隙，按照实际尺寸设置，如果为无间隙纸则设置为0
        tsc.addDirection(LabelCommand.DIRECTION.FORWARD, LabelCommand.MIRROR.NORMAL);// 设置打印方向
        tsc.addQueryPrinterStatus(LabelCommand.RESPONSE_MODE.ON);//开启带Response的打印，用于连续打印
        tsc.addReference(0, 0);// 设置原点坐标
        tsc.addTear(EscCommand.ENABLE.ON); // 撕纸模式开启
        //tsc.addCls();// 清除打印缓冲区
        tsc.clrCommand();
        tsc.addText(xinwuliaobianma,jiuwuliaobianma,wuliaomingcheng,tuhaoguige,danwei);
        //tsc.addPrint(1, 1); // 打印标签
        tsc.addSound(2, 100); // 打印标签后 蜂鸣器响

        /* 发送数据 */
        Vector<Byte> data = tsc.getCommand();
        if (DeviceConnFactoryManager.getDeviceConnFactoryManagers()[id] == null) {
            Log.i("TAG", "sendLabel: 打印机为空");
            return;
        }
        DeviceConnFactoryManager.getDeviceConnFactoryManagers()[id].sendDataImmediately(data);
    }

    public void printLabel3(int number) {
        Log.i("TAG", "准备打印");
        threadPool = ThreadPool.getInstantiation();
        threadPool.addTask(new Runnable() {
            @Override
            public void run() {
                //先判断打印机是否连接
                if (DeviceConnFactoryManager.getDeviceConnFactoryManagers()[id] == null ||
                        !DeviceConnFactoryManager.getDeviceConnFactoryManagers()[id].getConnState()) {
                    mHandler.obtainMessage(CONN_PRINTER).sendToTarget();
                    return;
                }
                if (DeviceConnFactoryManager.getDeviceConnFactoryManagers()[id].getCurrentPrinterCommand() == PrinterCommand.TSC) {
                    Log.i("TAG", "开始打印");
                    //循环多次打印
                    for (int i = 0; i < number; i++) {
                        sendLabel();
                    }
                } else {
                    mHandler.obtainMessage(PRINTER_COMMAND_ERROR).sendToTarget();
                }
            }
        });
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
                    XToastUtil.showToast(CangkutiaozhengActivity.this, "请求超时！");
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
