package com.agmbat.meetyou.search;

import android.view.View;

import com.agmbat.imsdk.asmack.roster.ContactInfo;
import com.agmbat.meetyou.R;

/**
 * 查看陌生人信息界面处理
 */
public class VerifyBusinessHandler extends BusinessHandler {

    public VerifyBusinessHandler(ContactInfo contactInfo) {
        super(contactInfo);
    }

    @Override
    public void setupViews(View view) {
        super.setupViews(view);
        view.findViewById(R.id.btn_pass_validation).setVisibility(View.VISIBLE);
    }
}
