package com.agmbat.meetyou.tab.profile;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.agmbat.imsdk.account.ImAccountManager;
import com.agmbat.meetyou.R;
import com.agmbat.meetyou.settings.SettingsActivity;

public class ProfileFragment extends Fragment implements View.OnClickListener {

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.tab_fragment_profile, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String userName = new ImAccountManager(getActivity()).getConnectionUserName();
        TextView tv = (TextView) view.findViewById(R.id.user_name);
        tv.setText(getString(R.string.id_name_format) + " " + userName);

        TextView nickNameView = (TextView) view.findViewById(R.id.nickname);

        view.findViewById(R.id.view_user).setOnClickListener(this);
        view.findViewById(R.id.txt_album).setOnClickListener(this);
        view.findViewById(R.id.txt_collect).setOnClickListener(this);
        view.findViewById(R.id.txt_money).setOnClickListener(this);
        view.findViewById(R.id.txt_card).setOnClickListener(this);
        view.findViewById(R.id.txt_smail).setOnClickListener(this);
        view.findViewById(R.id.txt_setting).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Activity activity = getActivity();
                activity.startActivity(new Intent(activity, SettingsActivity.class));
                activity.overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });
    }

    @Override
    public void onClick(View v) {

    }
}
