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
     * ???????????????
     */
    public static final int BLUETOOTH_REQUEST_CODE = 0x006;

    /**
     * ?????????????????????
     */
    private static final int CONN_PRINTER = 0x003;
    /**
     * ???????????????????????????
     */
    private static final int PRINTER_COMMAND_ERROR = 0x004;

    /**
     * ??????????????????
     */
    private static final int CONN_STATE_DISCONN = 0x005;
    private ThreadPool threadPool;//??????
    /**
     * ???????????????????????????????????????ESC??????
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

    private String xinwuliaobianma;//????????????
    private String jiuwuliaobianma;//???????????????
    private String wuliaomingcheng;//????????????
    private String tuhaoguige;//????????????
    private String danwei;//??????
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
                    XToastUtil.showToast(CangkutiaozhengActivity.this,"?????????????????????!");
                    break;
                }
                printLabel();
                break;
            }
            case R.id.btn_duocidaying:{
                if(!isConnected){
                    XToastUtil.showToast(CangkutiaozhengActivity.this,"?????????????????????!");
                    break;
                }
                startActivityForResult(new Intent(CangkutiaozhengActivity.this, InputDayingNumberActivity.class), 777);
                break;
            }
            case R.id.btn_search: {
                if(!ButtonUtils.isFastDoubleClick(R.id.btn_search)){
                    //?????????
                    xinwuliaobianma = ed_wuliaobianma.getText().toString();
                    if(xinwuliaobianma == null || xinwuliaobianma != null && "".equals(xinwuliaobianma)){
                        XToastUtil.showToast(this,"???????????????????????????????????????");
                        return;
                    }
                    LoadingStaticDialog.showLoadingDialog(CangkutiaozhengActivity.this, "????????????????????????");
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
                            XToastUtil.showToast(CangkutiaozhengActivity.this,"?????????"+e.getLocalizedMessage());
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
                                jiuwuliaobianma = jsonData.optString("jiuwuliaobianma");//????????????
                                String wuliaomingchengandguigexinghao = jsonData.optString("sapwuliaomiaoshu");//????????????
                                String[] arr = wuliaomingchengandguigexinghao.split("\\|\\|");
                                if(arr.length < 2){
                                    arr = wuliaomingchengandguigexinghao.split("???") ;
                                }
                                wuliaomingcheng = arr[0];
                                tuhaoguige = arr[1];
                                danwei = jsonData.optString("jiliangdanwei");//??????
                                Looper.prepare();
                                missDialog = false;
                                LoadingStaticDialog.loadDialogDismiss();
                                stopTimer();
                                //?????????
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

    /**
     * ????????????????????????????????????????????????????????????
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
         * ?????????????????????????????????
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
     * ?????????????????????
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
                        if (id == deviceId) tv_state.setText("?????????");
                        break;
                    case DeviceConnFactoryManager.CONN_STATE_CONNECTING:
                        tv_state.setText("?????????");
                        break;
                    case DeviceConnFactoryManager.CONN_STATE_CONNECTED:
                        tv_state.setText("?????????");
                        tv_state.setTextColor(getResources().getColor(R.color.colorPrimary));
                        isConnected = true;
                        Toast.makeText(CangkutiaozhengActivity.this, "?????????", Toast.LENGTH_SHORT).show();
                        break;
                    case CONN_STATE_FAILED:
                        tv_state.setText("?????????");
                        Toast.makeText(CangkutiaozhengActivity.this, "?????????????????????????????????????????????", Toast.LENGTH_SHORT).show();
                        break;
                }
                /* Usb??????????????????????????????????????? */
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
                        Toast.makeText(CangkutiaozhengActivity.this, "??????????????????", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case PRINTER_COMMAND_ERROR:
                    Toast.makeText(CangkutiaozhengActivity.this, "?????????????????????????????????", Toast.LENGTH_SHORT).show();
                    break;
                case CONN_PRINTER:
                    Toast.makeText(CangkutiaozhengActivity.this, "?????????????????????", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

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
                        }
                    }
                }
                break;
            //????????????
            case BLUETOOTH_REQUEST_CODE:{
                if(intent != null){
                    closePort();
                    //????????????mac??????
                    String macAddress = intent.getStringExtra(BluetoothListActivity.EXTRA_DEVICE_ADDRESS);
                    //?????????DeviceConnFactoryManager ???????????????
                    new DeviceConnFactoryManager.Build()
                            //???????????????
                            .setId(id)
                            //??????????????????
                            .setConnMethod(DeviceConnFactoryManager.CONN_METHOD.BLUETOOTH)
                            //?????????????????????mac??????
                            .setMacAddress(macAddress)
                            .build();
                    //????????????????????????????????????????????????
                    Log.i("TAG", "onActivityResult: ????????????" + id);
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
                    XToastUtil.showToast(this, "????????????????????????");
                }
                break;
        }
    }

    public void printLabel() {
        Log.i("TAG", "????????????");
        threadPool = ThreadPool.getInstantiation();
        threadPool.addTask(new Runnable() {
            @Override
            public void run() {
                //??????????????????????????????
                if (DeviceConnFactoryManager.getDeviceConnFactoryManagers()[id] == null ||
                        !DeviceConnFactoryManager.getDeviceConnFactoryManagers()[id].getConnState()) {
                    mHandler.obtainMessage(CONN_PRINTER).sendToTarget();
                    return;
                }
                if (DeviceConnFactoryManager.getDeviceConnFactoryManagers()[id].getCurrentPrinterCommand() == PrinterCommand.TSC) {
                    Log.i("TAG", "????????????");
                    sendLabel();
                } else {
                    mHandler.obtainMessage(PRINTER_COMMAND_ERROR).sendToTarget();
                }
            }
        });
    }

    private void sendLabel() {
        LabelCommand tsc = new LabelCommand();
        tsc.addSize(70, 50); // ?????????????????????????????????????????????
        tsc.addGap(1); // ?????????????????????????????????????????????????????????????????????????????????0
        tsc.addDirection(LabelCommand.DIRECTION.FORWARD, LabelCommand.MIRROR.NORMAL);// ??????????????????
        tsc.addQueryPrinterStatus(LabelCommand.RESPONSE_MODE.ON);//?????????Response??????????????????????????????
        tsc.addReference(0, 0);// ??????????????????
        tsc.addTear(EscCommand.ENABLE.ON); // ??????????????????
        //tsc.addCls();// ?????????????????????
        tsc.clrCommand();
        tsc.addText(xinwuliaobianma,jiuwuliaobianma,wuliaomingcheng,tuhaoguige,danwei);
        //tsc.addPrint(1, 1); // ????????????
        tsc.addSound(2, 100); // ??????????????? ????????????

        /* ???????????? */
        Vector<Byte> data = tsc.getCommand();
        if (DeviceConnFactoryManager.getDeviceConnFactoryManagers()[id] == null) {
            Log.i("TAG", "sendLabel: ???????????????");
            return;
        }
        DeviceConnFactoryManager.getDeviceConnFactoryManagers()[id].sendDataImmediately(data);
    }

    public void printLabel3(int number) {
        Log.i("TAG", "????????????");
        threadPool = ThreadPool.getInstantiation();
        threadPool.addTask(new Runnable() {
            @Override
            public void run() {
                //??????????????????????????????
                if (DeviceConnFactoryManager.getDeviceConnFactoryManagers()[id] == null ||
                        !DeviceConnFactoryManager.getDeviceConnFactoryManagers()[id].getConnState()) {
                    mHandler.obtainMessage(CONN_PRINTER).sendToTarget();
                    return;
                }
                if (DeviceConnFactoryManager.getDeviceConnFactoryManagers()[id].getCurrentPrinterCommand() == PrinterCommand.TSC) {
                    Log.i("TAG", "????????????");
                    //??????????????????
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
                    XToastUtil.showToast(CangkutiaozhengActivity.this, "???????????????");
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
