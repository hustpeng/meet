package com.agmbat.meetyou.search;

import android.view.View;

import com.agmbat.imsdk.asmack.roster.ContactInfo;
import com.agmbat.meetyou.R;

/**
 * 查看陌生人信息界面处理
 */
public class StrangerBusinessHandler extends BusinessHandler {

    public StrangerBusinessHandler(ContactInfo contactInfo) {
        super(contactInfo);
    }

    @Override
    public void setupViews(View view) {
        view.findViewById(R.id.setup_alias).setVisibility(View.GONE);
        view.findViewById(R.id.btn_chat).setVisibility(View.GONE);
        view.findViewById(R.id.btn_pass_validation).setVisibility(View.GONE);
    }
}
