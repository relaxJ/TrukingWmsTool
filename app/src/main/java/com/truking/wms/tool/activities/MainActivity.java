package com.truking.wms.tool.activities;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.FileProvider;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.truking.wms.tool.R;
import com.truking.wms.tool.fragment.Fragment_Practicalfeatures;
import com.truking.wms.tool.fragment.Fragment_Me;
import com.truking.wms.tool.fragment.Fragment_BasicData;
import com.truking.wms.tool.utils.DownloadUtil;
import com.truking.wms.tool.utils.PreferenceHelper;
import com.truking.wms.tool.utils.XToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements  View.OnClickListener{

    @BindView(R.id.tab)
    TabLayout tabLayout;
    @BindView(R.id.viewpager)
    ViewPager viewpager;



    boolean isInitTab = false;
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;
    private String appurl;
    private String TAG = "MainActivity";
    private File file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MyApp.getActivities().add(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            if (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            }
        }
        versionCheck();
    }

    //新版本检测
    private void versionCheck(){
        OkHttpClient client = new OkHttpClient();
        String url = "http://172.16.1.131:9090/api/app/version?userName="+ PreferenceHelper.getUserId()+
                "&token="+PreferenceHelper.getToken();
        Request build = new Request.Builder().url(url).build();
        Call call = client.newCall(build);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.i("11111","111111");
                String string = response.body().string();
                JSONObject json = null;
                try {
                    json = new JSONObject(string);
                    int codeResult = json.optInt("code");
                    if (codeResult != 200) {
                        return;
                    }
                    String data = json.optString("data");
                    JSONObject jsonData = new JSONObject(data);
                    String version = jsonData.optString("version");
                    appurl = jsonData.optString("url");
                    //从本地获取版本号和线上的进行匹配
                    String versionLocal = PreferenceHelper.getVersion();
                    Log.i("version555",versionLocal+";"+version);
                    if(versionLocal.equals("")){
                        //需要更新
                        PreferenceHelper.saveVersion(version);
                    }else{
                        if(!version.equals(versionLocal)){
                            //需要更新
                            PreferenceHelper.saveVersion(version);
                            showTip(MainActivity.this);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    //升级弹窗
    private void showTip(Context context) {
        Looper.prepare();
        AlertDialog dialog = new AlertDialog.Builder(context).setTitle("升级提示").setMessage("检测到新版本，请升级")
                .setNeutralButton("升级", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        doDownload();
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
        dialog.setCanceledOnTouchOutside(false);//可选
        dialog.setCancelable(false);//可选
        Looper.loop();
    }

    //下载升级包
    private void doDownload() {
        String parentPath = "";
        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                parentPath = this.getExternalFilesDir(null).getPath();
            } else {
                parentPath = this.getFilesDir().getPath();
            }
        } catch (Exception e) {
            Log.d(TAG, "doDownload e:" + e.getMessage());
        }

        Log.d(TAG, "doDownload parentPath:" + parentPath);
        file = new File(parentPath, "trukingwms.apk");
        final String filePath = file.getAbsolutePath();

        //如果已有文件，删除
        if (file.exists()) {
            Log.d(TAG, "doDownload delete APK");
            file.delete();
        }

        try {
            DownloadUtil.get().download(appurl, filePath, new DownloadUtil.OnDownloadListener() {
                @Override
                public void onDownloadSuccess() {
                    //成功
                    Log.d(TAG, "doDownload download success");
                    installApk();
                }

                @Override
                public void onDownloading(int progress) {
                    //进度
                    //Log.d(TAG, "doDownload download:" + progress +"%");
                }

                @Override
                public void onDownloadFailed() {
                    //失败
                    Log.d(TAG, "doDownload download fail");
                }
            });
        } catch (Exception e) {
            Log.d(TAG, "doDownload e2:" + e.getMessage());
        }
    }

    //安装app
    private void installApk() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri data;
        //7.0以上安卓系统安装app需要通过fileProvider（需要在AndroidManifest.xml声明）
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            data = FileProvider.getUriForFile(this, "com.truking.wms.tool.fileprovider", file);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Log.d(TAG,"installApk 7.0data:" + data);
        } else {
            data = Uri.fromFile(file);
            Log.d(TAG,"installApk data:" + data);
        }
        intent.setDataAndType(data, "application/vnd.android.package-archive");
        startActivity(intent);
//        Intent intent = new Intent();
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        //设置intent的Action属性
//        intent.setAction(Intent.ACTION_VIEW);   //这里的action——view之功用5下一篇博客
//        //获取文件file的MIME类型
//        String type = getMIMEType(file);
//        //设置intent的data和Type属性。
//        intent.setDataAndType(/*uri*/Uri.fromFile(file), type);
//        //跳转
//        startActivity(intent);

    }
    /**
     * 根据文件后缀名获得对应的MIME类型。
     * @param file
     */
    private String getMIMEType(File file) {

        String type="*/*";
        String fName = file.getName();
        //获取后缀名前的分隔符"."在fName中的位置。
        int dotIndex = fName.lastIndexOf(".");
        if(dotIndex < 0){
            return type;
        }
        /* 获取文件的后缀名*/
        String end=fName.substring(dotIndex,fName.length()).toLowerCase();
        if(end=="")return type;
        //在MIME和文件类型的匹配表中找到对应的MIME类型。
        for(int i=0;i<MIME_MapTable.length;i++){ //MIME_MapTable??在这里你一定有疑问，这个MIME_MapTable是什么？
            if(end.equals(MIME_MapTable[i][0]))
                type = MIME_MapTable[i][1];
        }
        return type;
    }

    final String[][] MIME_MapTable={
            //{后缀名，MIME类型}
            {".3gp",    "video/3gpp"},
            {".apk",    "application/vnd.android.package-archive"},
            {".asf",    "video/x-ms-asf"},
            {".avi",    "video/x-msvideo"},
            {".bin",    "application/octet-stream"},
            {".bmp",    "image/bmp"},
            {".c",  "text/plain"},
            {".class",  "application/octet-stream"},
            {".conf",   "text/plain"},
            {".cpp",    "text/plain"},
            {".doc",    "application/msword"},
            {".docx",   "application/vnd.openxmlformats-officedocument.wordprocessingml.document"},
            {".xls",    "application/vnd.ms-excel"},
            {".xlsx",   "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"},
            {".exe",    "application/octet-stream"},
            {".gif",    "image/gif"},
            {".gtar",   "application/x-gtar"},
            {".gz", "application/x-gzip"},
            {".h",  "text/plain"},
            {".htm",    "text/html"},
            {".html",   "text/html"},
            {".jar",    "application/java-archive"},
            {".java",   "text/plain"},
            {".jpeg",   "image/jpeg"},
            {".jpg",    "image/jpeg"},
            {".js", "application/x-javascript"},
            {".log",    "text/plain"},
            {".m3u",    "audio/x-mpegurl"},
            {".m4a",    "audio/mp4a-latm"},
            {".m4b",    "audio/mp4a-latm"},
            {".m4p",    "audio/mp4a-latm"},
            {".m4u",    "video/vnd.mpegurl"},
            {".m4v",    "video/x-m4v"},
            {".mov",    "video/quicktime"},
            {".mp2",    "audio/x-mpeg"},
            {".mp3",    "audio/x-mpeg"},
            {".mp4",    "video/mp4"},
            {".mpc",    "application/vnd.mpohun.certificate"},
            {".mpe",    "video/mpeg"},
            {".mpeg",   "video/mpeg"},
            {".mpg",    "video/mpeg"},
            {".mpg4",   "video/mp4"},
            {".mpga",   "audio/mpeg"},
            {".msg",    "application/vnd.ms-outlook"},
            {".ogg",    "audio/ogg"},
            {".pdf",    "application/pdf"},
            {".png",    "image/png"},
            {".pps",    "application/vnd.ms-powerpoint"},
            {".ppt",    "application/vnd.ms-powerpoint"},
            {".pptx",   "application/vnd.openxmlformats-officedocument.presentationml.presentation"},
            {".prop",   "text/plain"},
            {".rc", "text/plain"},
            {".rmvb",   "audio/x-pn-realaudio"},
            {".rtf",    "application/rtf"},
            {".sh", "text/plain"},
            {".tar",    "application/x-tar"},
            {".tgz",    "application/x-compressed"},
            {".txt",    "text/plain"},
            {".wav",    "audio/x-wav"},
            {".wma",    "audio/x-ms-wma"},
            {".wmv",    "audio/x-ms-wmv"},
            {".wps",    "application/vnd.ms-works"},
            {".xml",    "text/plain"},
            {".z",  "application/x-compress"},
            {".zip",    "application/x-zip-compressed"},
            {"",        "*/*"}
    };

    @Override
    protected void onResume() {
        super.onResume();
        if (!isInitTab) {
            isInitTab = true;
            initViewPagers();
            initTabs();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //Log.e("hhh",data.getStringExtra(Constant.CODED_CONTENT)+";"+requestCode+";"+resultCode);
        //如果不佳super那么requestCode会有问题
        super.onActivityResult(requestCode,resultCode,data);
        getSupportFragmentManager().getFragments().get(0).onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void initViewPagers(){
        final List<Fragment> fragments = new ArrayList<>();
        fragments.add(new Fragment_BasicData());
        fragments.add(new Fragment_Practicalfeatures());
        fragments.add(new Fragment_Me());
        FragmentPagerAdapter adapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return fragments.get(position);
            }

            @Override
            public int getCount() {
                return fragments.size();

            }
        };
        viewpager.setAdapter(adapter);
        viewpager.setOffscreenPageLimit(3);

    }

    private void initTabs() {
        tabLayout.setupWithViewPager(viewpager);

        // 设置初始化菜单
        if (tabLayout.getTabCount() < 3) {
            return;
        }
        try {
            tabLayout.getTabAt(0).setCustomView(addTabIcon(getResources().getString(R.string.act_main_tab_one), true, R.drawable.icon_001));
            tabLayout.getTabAt(1).setCustomView(addTabIcon(getResources().getString(R.string.act_main_tab_two), false, R.drawable.icon_02));
            tabLayout.getTabAt(2).setCustomView(addTabIcon(getResources().getString(R.string.act_main_tab_three), false, R.drawable.icon_03));
        } catch (Exception e){
            e.printStackTrace();
        }


        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                // 选中
                if (tab == tabLayout.getTabAt(0)) {
                    setTabIcon(tab.getCustomView(), getResources().getString(R.string.act_main_tab_one), true, R.drawable.icon_001);
                    viewpager.setCurrentItem(0);
                } else if (tab == tabLayout.getTabAt(1)) {
                    setTabIcon(tab.getCustomView(), getResources().getString(R.string.act_main_tab_two), true, R.drawable.icon_002);
                    viewpager.setCurrentItem(1);
                } else if (tab == tabLayout.getTabAt(2)) {
                    setTabIcon(tab.getCustomView(), getResources().getString(R.string.act_main_tab_three), true, R.drawable.icon_003);
                    viewpager.setCurrentItem(2);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // 未选中
                if (tab == tabLayout.getTabAt(0)) {
                    setTabIcon(tab.getCustomView(), getResources().getString(R.string.act_main_tab_one), false, R.drawable.icon_01);
                } else if (tab == tabLayout.getTabAt(1)) {
                    setTabIcon(tab.getCustomView(), getResources().getString(R.string.act_main_tab_two), false, R.drawable.icon_02);
                } else if (tab == tabLayout.getTabAt(2)) {
                    setTabIcon(tab.getCustomView(), getResources().getString(R.string.act_main_tab_three), false, R.drawable.icon_03);
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    /****
     * 创建自定义的菜单视图
     * @param name 名字
     * @param select true 选中 false 未选中
     * @param iconID 图标
     * @return
     */
    private View addTabIcon(String name,  boolean select,int iconID){
        // 自定义视图
        View newtab =  LayoutInflater.from(this).inflate(R.layout.gm_tab_custom_view,null);
        TextView tv =  newtab.findViewById(R.id.tv_tab);
        ImageView im = newtab.findViewById(R.id.img_tab);
        im.setImageResource(iconID);
        String html = select? "<font color=\"#03acfd\">"+name+"</font>":"<font color=\"#000000\">"+name+"</font>" ;
        tv.setText(Html.fromHtml(html));
        return newtab;
    }

    /***
     * 设置自定义菜单视图内容
     * @param newtab 视图
     * @param name 名字
     * @param select true 选中 false 未选中
     * @param iconID 图标
     * @return
     */
    private View setTabIcon( View newtab, String name, boolean select,int iconID) {
        TextView tv = newtab.findViewById(R.id.tv_tab);
        ImageView im = newtab.findViewById(R.id.img_tab);
        im.setImageResource(iconID);
        String html = select ? "<font color=\"#03acfd\" >" + name + "</font>" : "<font color=\"#000000\" >" + name + "</font>";
        tv.setText(Html.fromHtml(html));
        return newtab;
    }




    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exit();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    // 定义一个变量，来标识是否退出
    private static boolean isExit = false;
    Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            isExit = false;
        }
    };


    private void exit() {
        if (!isExit) {
            isExit = true;
            XToastUtil.showToast(this, this.getResources().getString(R.string.toast_back_message));
            // 利用handler延迟发送更改状态信息
            mHandler.sendEmptyMessageDelayed(0, 2000);
        } else {
            for (Activity activity : MyApp.getActivities()) {
                activity.finish();
            }
        }
    }

    @Override
    public void onClick(View view) {

    }
}
