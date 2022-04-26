package com.truking.wms.tool.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.truking.wms.tool.R;
import com.truking.wms.tool.activities.BocibiaoqianWarehousesActivity;
import com.truking.wms.tool.activities.ImportBasicDataActivity;
import com.truking.wms.tool.activities.PandianActivity;
import com.truking.wms.tool.activities.WuliaobiaoqianDayingActivity;
import com.truking.wms.tool.utils.ButtonUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Fragment_Practicalfeatures extends Fragment {
    private static final int PHOTO_REQUEST_SAOYISAO = 1;
    private static final int REQUEST_CODE_SCAN = 2;

    protected View rootView;
    @BindView(R.id.wuliaobiaoqian)
    LinearLayout wuliaobiaoqian;
    @BindView(R.id.view_bocibiaoqian)
    public LinearLayout view_bocibiaoqian;
    @BindView(R.id.pandian)
    public LinearLayout view_pandian;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_lanyadaying, container, false);
        ButterKnife.bind(getActivity());
        super.onCreateView(inflater, container, savedInstanceState);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        wuliaobiaoqian = (LinearLayout) getActivity().findViewById(R.id.wuliaobiaoqian);
        wuliaobiaoqian.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!ButtonUtils.isFastDoubleClick(R.id.write)) {
                    Intent intent = new Intent(getActivity(), WuliaobiaoqianDayingActivity.class);
                    startActivity(intent);
                }
            }
        });
        view_pandian = (LinearLayout) getActivity().findViewById(R.id.pandian);
        view_pandian.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!ButtonUtils.isFastDoubleClick(R.id.write)) {
                    Intent intent = new Intent(getActivity(), PandianActivity.class);
                    startActivity(intent);
                }
            }
        });
        view_bocibiaoqian = (LinearLayout) getActivity().findViewById(R.id.view_bocibiaoqian);
        view_bocibiaoqian.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!ButtonUtils.isFastDoubleClick(R.id.write)) {
                    Intent intent = new Intent(getActivity(), BocibiaoqianWarehousesActivity.class);
                    startActivity(intent);
                }
            }
        });
    }
}
