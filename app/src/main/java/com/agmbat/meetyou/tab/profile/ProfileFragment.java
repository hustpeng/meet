package com.agmbat.meetyou.tab.profile;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.agmbat.meetyou.R;

public class ProfileFragment extends Fragment implements View.OnClickListener {

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.tab_fragment_profile, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.view_user).setOnClickListener(this);
        view.findViewById(R.id.txt_album).setOnClickListener(this);
        view.findViewById(R.id.txt_collect).setOnClickListener(this);
        view.findViewById(R.id.txt_money).setOnClickListener(this);
        view.findViewById(R.id.txt_card).setOnClickListener(this);
        view.findViewById(R.id.txt_smail).setOnClickListener(this);
        view.findViewById(R.id.txt_setting).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

    }
}
