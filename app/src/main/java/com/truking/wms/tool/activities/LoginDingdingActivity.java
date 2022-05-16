package com.truking.wms.tool.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.truking.wms.tool.R;
import com.truking.wms.tool.adapters.ShipmentdetailAdapter;
import com.truking.wms.tool.utils.ButtonUtils;
import com.truking.wms.tool.utils.LoadingStaticDialog;
import com.truking.wms.tool.utils.NetworkUtil;
import com.truking.wms.tool.utils.PreferenceHelper;
import com.truking.wms.tool.utils.XToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;

public class LoginDingdingActivity extends AppCompatActivity implements View.OnClickListener{
    @BindView(R.id.phoneNo)
    public EditText ed_phoneNo;
    @BindView(R.id.vcode)
    public EditText ed_vcode;
    @BindView(R.id.forget_btn_captcha)
    Button forgetBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MyApp.getActivities().add(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_dingding);
        ButterKnife.bind(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }

    @OnClick({R.id.login_confirm,R.id.forget_btn_captcha})
    public void onViewClicked(View view){
        switch (view.getId()) {
            case R.id.login_confirm: {
                if(!ButtonUtils.isFastDoubleClick(R.id.login_confirm)){
                    //检验用户名和密码
                    String phoneNo = ed_phoneNo.getText().toString();
                    String vcode = ed_vcode.getText().toString();
                    if(phoneNo == null || phoneNo != null && "".equals(phoneNo)){
                        XToastUtil.showToast(this,"请输入电话！");
                        return;
                    }
                    if(vcode == null || vcode != null && "".equals(vcode)){
                        XToastUtil.showToast(this,"请输入验证码！");
                        return;
                    }
                    //登录
                    LoadingStaticDialog.showLoadingDialog(LoginDingdingActivity.this, "登录中");
                    startTimer();
                    missDialog = true;
                    //网络请求
                    String url="http://172.16.1.131:9090/api/app/appuser";
                    JSONObject parameters = new JSONObject();
                    //添加参数
                    try {
                        parameters.put("phoneNo", phoneNo);
                        parameters.put("vcode", vcode);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    NetworkUtil networkUtil = new NetworkUtil();
                    networkUtil.setCallback(
                        new okhttp3.Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                Looper.prepare();
                                XToastUtil.showToast(LoginDingdingActivity.this,"登录失败！"+e.getLocalizedMessage());
                                missDialog = false;
                                LoadingStaticDialog.loadDialogDismiss();
                                stopTimer();
                                Looper.loop();
                            }

                            @Override
                            public void onResponse(Call call, okhttp3.Response response) throws IOException {
                                Looper.prepare();
                                String result = response.body().string();
                                missDialog = false;
                                LoadingStaticDialog.loadDialogDismiss();
                                stopTimer();
                                JSONObject jsonObject = null;
                                try {
                                    jsonObject = new JSONObject(result);
                                    String data = jsonObject.getString("data");
                                    int code = jsonObject.getInt("code");
                                    String info = jsonObject.getString("info");
                                    if(code != 200){
                                        XToastUtil.showToast(LoginDingdingActivity.this,info);
                                        Looper.loop();
                                        return;
                                    }
                                    JSONObject jsonObject1 = new JSONObject(data);
                                    String token = jsonObject1.getString("token");
                                    PreferenceHelper.saveToken(token);
                                    PreferenceHelper.saveUserId(phoneNo);
                                    Intent intent = new Intent(LoginDingdingActivity.this, MainActivity.class);
                                    startActivity(intent);
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
            case R.id.forget_btn_captcha: {
                //校验手机号格式是否正确
                String phoneNo = ed_phoneNo.getText().toString();
                if (phoneNo.length() == 0 || phoneNo.contains("@")) {
                    XToastUtil.showToast(LoginDingdingActivity.this, "手机号格式不对！");
                    break;
                }
                //网络请求
                String url = "http://172.16.1.131:9090/api/app/appuser";
                JSONObject parameters = new JSONObject();
                //添加参数
                try {
                    parameters.put("phoneNo", phoneNo);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                NetworkUtil networkUtil = new NetworkUtil();
                networkUtil.setCallback(
                        new okhttp3.Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                Looper.prepare();
                                XToastUtil.showToast(LoginDingdingActivity.this, "获取验证码失败！" + e.getLocalizedMessage());
                                Looper.loop();
                            }

                            @Override
                            public void onResponse(Call call, okhttp3.Response response) throws IOException {
                                Looper.prepare();
                                Message message = handler.obtainMessage();
                                message.what = 1;
                                handler.sendMessage(message);
                                Looper.loop();
                            }
                        });
                networkUtil.post(url, parameters);
                break;
            }
        }
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                ed_vcode.requestFocus();
                ed_vcode.setSelection(ed_vcode.getText().length());
                countDownTimer1.start();
            }
            super.handleMessage(msg);
        }
    };

    private CountDownTimer countDownTimer1 = new CountDownTimer(60 * 1000, 1000) {
        @Override
        public void onTick(long millisUntilFinished) {
            forgetBtn.setText(millisUntilFinished / 1000 + "s");//设置倒计时时间
            //设置按钮为灰色，这时是不能点击的
            forgetBtn.setEnabled(false);
        }

        @Override
        public void onFinish() {
            forgetBtn.setText("重试");
            forgetBtn.setClickable(true);//重新获得点击
            forgetBtn.setEnabled(true);
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
                    XToastUtil.showToast(LoginDingdingActivity.this, "请求超时！");
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
