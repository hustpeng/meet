package com.agmbat.meetyou.settings;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.TextView;

import com.agmbat.android.utils.WindowUtils;
import com.agmbat.imsdk.IM;
import com.agmbat.imsdk.asmack.XMPPManager;
import com.agmbat.meetyou.R;
import com.agmbat.picker.NumberPicker;
import com.agmbat.picker.OptionPicker;
import com.agmbat.picker.helper.PickerHelper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jivesoftware.smackx.vcardextend.VCardExtendObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 我的信息界面
 */
public class PersonalInfoMoreActivity extends Activity {


    @BindView(R.id.height)
    TextView mHeightView;

    @BindView(R.id.weight)
    TextView mWeightView;

    @BindView(R.id.wage)
    TextView mWageView;

    @BindView(R.id.education)
    TextView mEducationView;

    @BindView(R.id.marriage)
    TextView mMarriageView;

    /**
     * 用户信息
     */
    private VCardExtendObject mVCardExtendObject;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowUtils.setStatusBarColor(this, 0xff232325);
        setContentView(R.layout.activity_personal_more_info);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        IM.get().fetchMyVCardExtend();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
    }

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }

    /**
     * 收到vcard更新信息
     *
     * @param vCardObject
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(VCardExtendObject vCardObject) {
        mVCardExtendObject = vCardObject;
        mHeightView.setText(String.valueOf(mVCardExtendObject.getHeight()) + "cm");
        mWeightView.setText(String.valueOf(mVCardExtendObject.getWeight()) + "kg");
        mWageView.setText(String.valueOf(mVCardExtendObject.getWage()) + "元以上/月");
        mEducationView.setText(mVCardExtendObject.getEducation());
        mMarriageView.setText(mVCardExtendObject.getMarriage());
    }

    /**
     * 点击返回键
     */
    @OnClick(R.id.title_btn_back)
    void onClickBack() {
        finish();
    }

    @OnClick(R.id.btn_height)
    void onClickHeight() {
        int selected = mVCardExtendObject.getHeight();
        NumberPicker.OnNumberPickListener l = new NumberPicker.OnNumberPickListener() {
            @Override
            public void onNumberPicked(int index, Number item) {
                mVCardExtendObject.setHeight(item.intValue());
                EventBus.getDefault().post(mVCardExtendObject);
                XMPPManager.getInstance().getvCardExtendManager().setMyVCardExtend(mVCardExtendObject);
            }
        };
        PickerHelper.showHeightPicker(this, selected, l);
    }

    @OnClick(R.id.btn_weight)
    void onClickWeight() {
        int selected = mVCardExtendObject.getWeight();
        NumberPicker.OnNumberPickListener l = new NumberPicker.OnNumberPickListener() {
            @Override
            public void onNumberPicked(int index, Number item) {
                mVCardExtendObject.setWeight(item.intValue());
                EventBus.getDefault().post(mVCardExtendObject);
                XMPPManager.getInstance().getvCardExtendManager().setMyVCardExtend(mVCardExtendObject);
            }
        };
        PickerHelper.showWeightPicker(this, selected, l);
    }

    @OnClick(R.id.btn_wage)
    void onClickWage() {
        int selected = mVCardExtendObject.getWage();
        NumberPicker.OnNumberPickListener l = new NumberPicker.OnNumberPickListener() {
            @Override
            public void onNumberPicked(int index, Number item) {
                mVCardExtendObject.setWage(item.intValue());
                EventBus.getDefault().post(mVCardExtendObject);
                XMPPManager.getInstance().getvCardExtendManager().setMyVCardExtend(mVCardExtendObject);
            }
        };
        PickerHelper.showWagePicker(this, selected, l);
    }

    @OnClick(R.id.btn_education)
    void onClickEducation() {
        String selected = mVCardExtendObject.getEducation();
        OptionPicker.OnOptionPickListener l = new OptionPicker.OnOptionPickListener() {
            @Override
            public void onOptionPicked(int index, String item) {
                mVCardExtendObject.setEducation(item);
                EventBus.getDefault().post(mVCardExtendObject);
                XMPPManager.getInstance().getvCardExtendManager().setMyVCardExtend(mVCardExtendObject);
            }
        };
        PickerHelper.showEducationPicker(this, selected, l);
    }

    @OnClick(R.id.btn_marriage)
    void onClickMarriage() {
        String selected = mVCardExtendObject.getMarriage();
        OptionPicker.OnOptionPickListener l = new OptionPicker.OnOptionPickListener() {
            @Override
            public void onOptionPicked(int index, String item) {
                mVCardExtendObject.setMarriage(item);
                EventBus.getDefault().post(mVCardExtendObject);
                XMPPManager.getInstance().getvCardExtendManager().setMyVCardExtend(mVCardExtendObject);
            }
        };
        PickerHelper.showMarriagePicker(this, selected, l);
    }

}
