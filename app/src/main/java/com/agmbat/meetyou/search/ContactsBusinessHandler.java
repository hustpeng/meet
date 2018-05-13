package com.agmbat.meetyou.search;

import android.view.View;

import com.agmbat.imsdk.data.ContactInfo;
import com.agmbat.meetyou.R;

/**
 * 查看联系人信息界面处理
 */
public class ContactsBusinessHandler extends BusinessHandler {

    public ContactsBusinessHandler(ContactInfo contactInfo) {
        super(contactInfo);
    }

    @Override
    public void setupViews(View view) {
        view.findViewById(R.id.btn_add_to_contact).setVisibility(View.GONE);
        view.findViewById(R.id.btn_pass_validation).setVisibility(View.GONE);
    }
}
