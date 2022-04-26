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
import android.widget.TextView;

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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class BocibiaoqianDetailActivity extends AppCompatActivity implements View.OnClickListener{
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

    private String itemCode;//物料编码
    private String zon;//区域
    private String fromLoc;//货位
    private String itemName;//物料名称
    private String attribute1;//工厂
    private String batch;//批次
    private String attr;//特殊属性
    private String fromQty;//数量
    private String convertedQtyUm;//单位

    @BindView(R.id.tv_itemCode)
    public TextView tv_itemCode;
    @BindView(R.id.tv_zon)
    public TextView tv_zon;
    @BindView(R.id.tv_fromLoc)
    public TextView tv_fromLoc;
    @BindView(R.id.tv_itemName)
    public TextView tv_itemName;
    @BindView(R.id.tv_attribute1)
    public TextView tv_attribute1;
    @BindView(R.id.tv_batch)
    public TextView tv_batch;
    @BindView(R.id.tv_attr)
    public TextView tv_attr;
    @BindView(R.id.tv_fromQty)
    public TextView tv_fromQty;
    @BindView(R.id.tv_convertedQtyUm)
    public TextView tv_convertedQtyUm;

    private JSONObject obj;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MyApp.getActivities().add(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bocibiaoqiandetail);
        ButterKnife.bind(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        try {
            obj = new JSONObject(getIntent().getStringExtra("obj"));
            itemCode = obj.optString("itemCode");
            tv_itemCode.setText(itemCode);
            itemName = obj.optString("itemName");
            tv_itemName.setText(itemName);
            zon = obj.optString("zon");
            tv_zon.setText(zon);
            fromLoc = obj.optString("fromLoc");
            tv_fromLoc.setText(fromLoc);
            attribute1 = obj.optString("attribute1");
            tv_attribute1.setText(attribute1);
            batch = obj.optString("batch");
            tv_batch.setText(batch);
            attr = obj.optString("attr");
            tv_attr.setText(attr);
            fromQty = obj.optString("fromQty");
            tv_fromQty.setText(fromQty);
            convertedQtyUm = obj.optString("convertedQtyUm");
            tv_convertedQtyUm.setText(convertedQtyUm);
        } catch (JSONException e) {
            e.printStackTrace();
        }
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

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    @Override
    public void onClick(View view) {

    }
}
