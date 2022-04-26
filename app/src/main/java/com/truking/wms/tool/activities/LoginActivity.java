package com.truking.wms.tool.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.View;
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

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{
    @BindView(R.id.username)
    public EditText ed_userName;
    @BindView(R.id.password)
    public EditText ed_password;
    @BindView(R.id.operatePwd)
    ImageView operatePwd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MyApp.getActivities().add(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        ed_userName.setText(PreferenceHelper.getUserId());
    }

    @OnClick({R.id.login_confirm,R.id.operatePwd})
    public void onViewClicked(View view){
        switch (view.getId()) {
            case R.id.login_confirm: {
                if(!ButtonUtils.isFastDoubleClick(R.id.login_confirm)){
                    //检验用户名和密码
                    String username = ed_userName.getText().toString();
                    String password = ed_password.getText().toString();
                    if(username == null || username != null && "".equals(username)){
                        XToastUtil.showToast(this,"请输入用户名！");
                        return;
                    }
                    if(password == null || password != null && "".equals(password)){
                        XToastUtil.showToast(this,"请输入密码！");
                        return;
                    }
                    //登录
                    LoadingStaticDialog.showLoadingDialog(LoginActivity.this, "登录中");
                    startTimer();
                    missDialog = true;
                    //网络请求
                    String url="http://172.16.1.131:9090/api/app/appuser";
                    JSONObject parameters = new JSONObject();
                    //添加参数
                    try {
                        parameters.put("userName", username);
                        parameters.put("password", password);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    NetworkUtil networkUtil = new NetworkUtil();
                    networkUtil.setCallback(
                        new okhttp3.Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                Looper.prepare();
                                XToastUtil.showToast(LoginActivity.this,"登录失败！"+e.getLocalizedMessage());
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
                                        XToastUtil.showToast(LoginActivity.this,info);
                                        Looper.loop();
                                        return;
                                    }
                                    JSONObject jsonObject1 = new JSONObject(data);
                                    String token = jsonObject1.getString("token");
                                    PreferenceHelper.saveToken(token);
                                    PreferenceHelper.saveUserId(username);
                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
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
            case R.id.operatePwd:{
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
                    XToastUtil.showToast(LoginActivity.this, "请求超时！");
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
