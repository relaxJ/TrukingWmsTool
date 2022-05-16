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
import com.truking.wms.tool.activities.DafuImportBasicDataActivity1;
import com.truking.wms.tool.activities.HuoweiduizhaoActivity;
import com.truking.wms.tool.activities.ImportBasicDataActivity;
import com.truking.wms.tool.activities.WuliaoxingxiActivity;
import com.truking.wms.tool.utils.ButtonUtils;

import butterknife.BindView;
import butterknife.ButterKnife;


public class Fragment_Report extends Fragment {

    protected View rootView;

    @BindView(R.id.write)
    LinearLayout write;
    @BindView(R.id.dafuwrite)
    LinearLayout dafuwrite;
    @BindView(R.id.otherBasicDatas)
    LinearLayout otherBasicDatas;
    @BindView(R.id.wuliaoxingxi)
    public LinearLayout view_wuliaoxingxi;
    @BindView(R.id.huoweiduizhao)
    public LinearLayout view_huoweiduizhao;

    private static final int PHOTO_REQUEST_SAOYISAO = 1;
    private static final int REQUEST_CODE_SCAN = 2;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_report, container, false);
        ButterKnife.bind(getActivity());
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

}

