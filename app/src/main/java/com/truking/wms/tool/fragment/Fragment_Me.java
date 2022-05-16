package com.truking.wms.tool.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.truking.wms.tool.R;
import com.truking.wms.tool.activities.AboutActivity;
import com.truking.wms.tool.activities.ProfileActivity;
import com.truking.wms.tool.activities.SettingsActivity;
import com.truking.wms.tool.utils.PreferenceHelper;

import butterknife.BindView;

public class Fragment_Me extends Fragment {

    @BindView(R.id.drawer_menu_header_img)
    SimpleDraweeView headPic;
    @BindView(R.id.drawer_menu_name)
    TextView textView;
    protected View rootView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        //ButterKnife.bind(getActivity());
        Fresco.initialize(getActivity());
        rootView = inflater.inflate(R.layout.fragment_me, container, false);


        View view2 = rootView.findViewById(R.id.about);
        view2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), AboutActivity.class));
            }
        });

        View view3 = rootView.findViewById(R.id.setting);
        view3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
            }
        });

        headPic = rootView.findViewById(R.id.drawer_menu_header_img);

        headPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), ProfileActivity.class));
            }
        });
        textView = rootView.findViewById(R.id.drawer_menu_name);
        textView.setText(PreferenceHelper.getUserId());
        return rootView;
    }


}
