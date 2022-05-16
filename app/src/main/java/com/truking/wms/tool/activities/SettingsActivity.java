package com.truking.wms.tool.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.truking.wms.tool.R;
import com.truking.wms.tool.utils.LoadingStaticDialog;
import com.truking.wms.tool.utils.NetworkUtil;
import com.truking.wms.tool.utils.PreferenceHelper;
import com.truking.wms.tool.utils.XToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;


public class SettingsActivity extends AppCompatActivity {

    @BindView(R.id.settings_btn_logoff)
    Button logOff;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_settings);
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        LinearLayout back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SettingsActivity.this.finish();
            }
        });

        logOff = findViewById(R.id.settings_btn_logoff);
        logOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //退出登录
                LoadingStaticDialog.showLoadingDialog(SettingsActivity.this, "操作中");
                startTimer();
                missDialog = true;
                //网络请求
                String url="http://172.16.1.131:9090/api/app/wcsbasicdata";
                JSONObject parameters = new JSONObject();
                //添加参数
                try {
                    parameters.put("code", PreferenceHelper.getUserId());
                    parameters.put("name", "");
                    parameters.put("password", "");
                    parameters.put("department", "");
                    parameters.put("email", "");
                    parameters.put("phoneNo", "");
                    parameters.put("remark", "");
                    parameters.put("vcode", "");
                    parameters.put("userId", "");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                NetworkUtil networkUtil = new NetworkUtil();
                networkUtil.setCallback(
                        new okhttp3.Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                Looper.prepare();
                                XToastUtil.showToast(SettingsActivity.this,"退出登录失败！"+e.getLocalizedMessage());
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
                                PreferenceHelper.logOff(SettingsActivity.this);
                                Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
                                startActivity(intent);
                                Looper.loop();
                            }
                        });
                networkUtil.post(url,parameters);
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
                    XToastUtil.showToast(SettingsActivity.this, "请求超时！");
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

}
