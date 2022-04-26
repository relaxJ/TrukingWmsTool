package com.truking.wms.tool.activities;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.truking.wms.tool.R;
import com.truking.wms.tool.utils.XToastUtil;

import java.util.Set;

/**
 * 输入多次打印的数量
 */
public class InputDayingNumberActivity extends Activity {

    private EditText number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.dialog_inputnumberofdaying);

        number = findViewById(R.id.number);
        findViewById(R.id.sure).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String numberStr = number.getText().toString();
                if(numberStr == null || numberStr!=null && "".equals(numberStr)){
                    XToastUtil.showToast(InputDayingNumberActivity.this,"请先输入数量");
                    return;
                }
                view.setVisibility(View.GONE);
                Intent intent = new Intent();
                intent.putExtra("number", numberStr);
                setResult(777, intent);
                finish();
            }
        });

    }

}
