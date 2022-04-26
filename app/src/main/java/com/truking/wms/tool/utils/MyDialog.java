package com.truking.wms.tool.utils;

import android.app.Activity;
import android.app.Dialog;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import com.truking.wms.tool.R;


public class MyDialog extends Dialog implements View.OnClickListener{
    private final View rootView;
    private final Activity mContext;
    protected TextView tvTitle;
    protected TextView tvContent;
    protected Button btnLeft;
    protected Button btnRight;

    private String content;
    private String title;
    private String rightTitle;
    private MyOnclickListener mMyOnclickListener;

    public void show() {
        super.show();
        Window window = getWindow();
        window.setGravity(Gravity.CENTER);  //此处可以设置dialog显示的位置
        WindowManager m = mContext.getWindowManager();
        Display d = m.getDefaultDisplay();  //为获取屏幕宽、高
        WindowManager.LayoutParams p = window.getAttributes();  //获取对话框当前的参数值
        p.width = (int) (d.getWidth()*0.8);      //宽度设置为屏幕的0.8
        window.setAttributes(p);     //设置生效
    }

    public MyDialog(Activity context,MyOnclickListener myOnclickListener) {
        super(context, R.style.DialogThemeNoTitle);
        mContext = context;
        this.mMyOnclickListener=myOnclickListener;
        rootView = LayoutInflater.from(context).inflate(R.layout.dialog_custom_info, null);
        setContentView(rootView);
        initView();
    }

    public void setTitle(String content) {
        this.content = content;
        if (tvTitle != null){
            tvTitle.setText(content);
        }
    }

    public void setContent(String title) {
        this.title = title;
        if (tvContent != null) {
            tvContent.setText(title);
        }
    }

    public void setRightBtnTitle(String rightTitle){
        this.rightTitle = rightTitle;
        if (btnRight != null){
            btnRight.setText(rightTitle);
        }
    }

    private void initView(){
        tvTitle = rootView.findViewById(R.id.dialog_common_tv_title);
        tvContent = rootView.findViewById(R.id.dialog_common_tv_content);
        if (title != null){
            tvTitle.setText(title);
        }
        if (content != null){
            tvContent.setText(content);
        }
        btnLeft = rootView.findViewById(R.id.dialog_common_btn_left);
        btnLeft.setOnClickListener(this);

        btnRight = rootView.findViewById(R.id.dialog_common_btn_right);
        btnRight.setOnClickListener(this);
        if (rightTitle != null){
            btnRight.setText(rightTitle);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dialog_common_btn_left:
                dismiss();
                mMyOnclickListener.onYesClick("11111");
                break;
            case R.id.dialog_common_btn_right:
                dismiss();
                mMyOnclickListener.onYesClick("22222");
                break;
            default:
                break;
        }
    }

    //内部接口
    public interface MyOnclickListener{
        public void onYesClick(String message);
    }


}
