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
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.truking.wms.tool.R;
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

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener{
    @BindView(R.id.code)
    public EditText ed_code;
    @BindView(R.id.password)
    public EditText ed_password;
    @BindView(R.id.name)
    public EditText ed_name;
    @BindView(R.id.department)
    public EditText ed_department;
    @BindView(R.id.email)
    public EditText ed_email;
    @BindView(R.id.phoneNo)
    public EditText ed_phoneNo;
    @BindView(R.id.vcode)
    public EditText ed_vcode;
    @BindView(R.id.forget_btn_captcha)
    Button forgetBtn;
    @BindView(R.id.operatePwd)
    ImageView operatePwd;
    private String codeSave;
    private String userId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MyApp.getActivities().add(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        ImageView about_back = findViewById(R.id.about_back);
        about_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RegisterActivity.this.finish();
            }
        });
    }

    @OnClick({R.id.register_confirm,R.id.forget_btn_captcha,R.id.operatePwd})
    public void onViewClicked(View view){
        switch (view.getId()) {
            case R.id.register_confirm: {
                if(!ButtonUtils.isFastDoubleClick(R.id.register_confirm)){
                    //检验用户名和密码
                    String phoneNo = ed_phoneNo.getText().toString();
                    String vcode = ed_vcode.getText().toString();
                    codeSave = ed_code.getText().toString();
                    String name = ed_name.getText().toString();
                    String password = ed_password.getText().toString();
                    String department = ed_department.getText().toString();
                    String email = ed_email.getText().toString();
                    if(phoneNo == null || phoneNo != null && "".equals(phoneNo)||
                            vcode == null || vcode != null && "".equals(vcode)||
                            codeSave == null || codeSave != null && "".equals(codeSave)||
                            name == null || name != null && "".equals(name)||
                            password == null || password != null && "".equals(password)||
                            department == null || department != null && "".equals(department)){
                        XToastUtil.showToast(this,"请输入必填项！");
                        return;
                    }
                    Log.i("8888888888888888888888",vcode);
                    //登录
                    LoadingStaticDialog.showLoadingDialog(RegisterActivity.this, "注册中");
                    startTimer();
                    missDialog = true;
                    //网络请求
                    String url="http://172.16.1.131:9090/api/app/appuser1";
                    JSONObject parameters = new JSONObject();
                    //添加参数
                    try {
                        parameters.put("code", codeSave);
                        parameters.put("name", name);
                        parameters.put("password", password);
                        parameters.put("department", department);
                        parameters.put("email", email);
                        parameters.put("phoneNo", phoneNo);
                        parameters.put("remark", vcode);
                        parameters.put("vcode", vcode);
                        parameters.put("userId", userId);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    NetworkUtil networkUtil = new NetworkUtil();
                    networkUtil.setCallback(
                        new okhttp3.Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                Looper.prepare();
                                XToastUtil.showToast(RegisterActivity.this,"注册失败！"+e.getLocalizedMessage());
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
                                        XToastUtil.showToast(RegisterActivity.this,info);
                                        Looper.loop();
                                        return;
                                    }
                                    JSONObject jsonObject1 = new JSONObject(data);
                                    String token = jsonObject1.getString("token");
                                    PreferenceHelper.saveToken(token);
                                    PreferenceHelper.saveUserId(codeSave);
                                    Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
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
            } case R.id.operatePwd:{
                if(ed_password.getInputType() == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD) ){
                    operatePwd.setImageDrawable(getResources().getDrawable(R.drawable.hidepwd));
                    ed_password.setInputType(InputType.TYPE_CLASS_TEXT);
                    ed_password.setSelection(ed_password.getText().length());
                }else{
                    //设置密文的时候，需要同时设置type_class_text 才能生效
                    operatePwd.setImageDrawable(getResources().getDrawable(R.drawable.showpwd));
                    ed_password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    ed_password.setSelection(ed_password.getText().length());
                }
                break;
            }
            case R.id.forget_btn_captcha: {
                //校验手机号格式是否正确
                String phoneNo = ed_phoneNo.getText().toString();
                if (phoneNo.length() == 0 || phoneNo.contains("@")) {
                    XToastUtil.showToast(RegisterActivity.this, "手机号格式不对！");
                    break;
                }
                //网络请求
                String url = "http://172.16.1.131:9090/api/app/sms";
                JSONObject parameters = new JSONObject();
                //添加参数
                try {
                    parameters.put("phonenoOrEmail", phoneNo);
                    parameters.put("mark", 3);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                NetworkUtil networkUtil = new NetworkUtil();
                networkUtil.setCallback(
                        new okhttp3.Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                Looper.prepare();
                                XToastUtil.showToast(RegisterActivity.this, "获取验证码失败！" + e.getLocalizedMessage());
                                Looper.loop();
                            }

                            @Override
                            public void onResponse(Call call, okhttp3.Response response) throws IOException {
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
                                        XToastUtil.showToast(RegisterActivity.this,info);
                                        Looper.loop();
                                        return;
                                    }
                                    JSONObject jsonObject1 = new JSONObject(data);
                                    userId = jsonObject1.getString("userId");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
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
                    XToastUtil.showToast(RegisterActivity.this, "请求超时！");
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
