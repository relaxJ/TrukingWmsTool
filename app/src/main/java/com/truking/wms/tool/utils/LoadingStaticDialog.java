package com.truking.wms.tool.utils;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.truking.wms.tool.R;


public class LoadingStaticDialog {

    static Dialog loadingDialog;
    private static String TAG = "LoadingStaticDialog";

    public static Dialog showLoadingDialog(Context context, String msg){
        if (loadingDialog != null && loadingDialog.isShowing()){
            Log.i(TAG, "return loadingDialog;");
            return loadingDialog;
        }

        loadingDialog = new Dialog(context, R.style.DialogThemeNoTitle);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_loading, null);
        loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        loadingDialog.setContentView(view);
//        ProgressBar progressBar = view.findViewById(R.id.progress);
//        ImageView iv = view.findViewById(R.id.iv);
        TextView tv_msg = view.findViewById(R.id.message);
        if (!TextUtils.isEmpty(msg)){
            tv_msg.setText(msg);
        }
        loadingDialog.setCancelable(true);//能否通过返回键使其消失，应该为true
        loadingDialog.show();
        return loadingDialog;
    }

    public static void loadDialogDismiss(){
        if (loadingDialog != null && loadingDialog.isShowing()){
            loadingDialog.dismiss();
        }
    }
}
