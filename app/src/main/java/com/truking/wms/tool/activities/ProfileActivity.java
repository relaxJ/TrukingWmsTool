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
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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

public class ProfileActivity extends AppCompatActivity {
    @BindView(R.id.tv_gonghao)
    TextView tv_gonghao;
    @BindView(R.id.tv_name)
    TextView tv_name;
    @BindView(R.id.tv_department)
    TextView tv_department;
    @BindView(R.id.tv_phoneNo)
    TextView tv_phoneNo;
    @BindView(R.id.tv_email)
    TextView tv_email;

    private String name ;
    private String department;
    private String phoneNo ;
    private String email;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MyApp.getActivities().add(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        ImageView about_back = findViewById(R.id.personalInfo_back);
        about_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProfileActivity.this.finish();
            }
        });

        //网络请求
        String url="http://172.16.1.131:9090/api/app/getuserinfo";
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
        } catch (JSONException e) {
            e.printStackTrace();
        }
        NetworkUtil networkUtil = new NetworkUtil();
        networkUtil.setCallback(
                new okhttp3.Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {

                    }

                    @Override
                    public void onResponse(Call call, okhttp3.Response response) throws IOException {
                        Looper.prepare();
                        String result = response.body().string();
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(result);
                            String data = jsonObject.getString("data");
                            int code = jsonObject.getInt("code");
                            if(code != 200){
                                return;
                            }
                            JSONObject jsonObject1 = new JSONObject(data);
                            name = jsonObject1.getString("name");
                            department = jsonObject1.getString("department");
                            phoneNo = jsonObject1.getString("phoneNo");
                            email = jsonObject1.getString("email");
                            Message message = handler.obtainMessage();
                            message.what = 1;
                            handler.sendMessage(message);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Looper.loop();
                    }
                });
        networkUtil.post(url,parameters);
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                tv_department.setText(department);
                tv_email.setText(email);
                tv_phoneNo.setText(phoneNo);
                tv_gonghao.setText(PreferenceHelper.getUserId());
                tv_name.setText(name);
            }
            super.handleMessage(msg);
        }
    };


}
