package com.agmbat.meetyou.tab.profile;

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
import com.agmbat.meetyou.account.ChangePasswordActivity;
import com.agmbat.meetyou.credits.CoinsActivity;

/**
 * Tab 我的界面
 */
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
        view.findViewById(R.id.btn_change_password).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), ChangePasswordActivity.class));
                getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });

        view.findViewById(R.id.btn_credits).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), CoinsActivity.class));
                getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });
    }

    @Override
    public void onClick(View v) {

    }
}
