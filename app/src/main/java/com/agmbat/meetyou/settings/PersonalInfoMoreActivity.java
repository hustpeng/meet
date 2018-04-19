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
import com.agmbat.picker.address.Address;
import com.agmbat.picker.address.AddressPicker;
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
     * 修改签名
     */
    @BindView(R.id.signature)
    TextView mSignatureView;

    @BindView(R.id.demand)
    TextView mDemandView;

    @BindView(R.id.hobby)
    TextView mHobbyView;

    /**
     * 自我简介
     */
    @BindView(R.id.introduce)
    TextView mIntroduceView;

    /**
     * 工作地区
     */
    @BindView(R.id.workarea)
    TextView mWorkareaView;
    /**
     * 籍贯
     */
    @BindView(R.id.birthplace)
    TextView mBirthplaceView;

    /**
     * 户口在地
     */
    @BindView(R.id.residence)
    TextView mResidenceView;
    /**
     * 户口在地
     */
    @BindView(R.id.car)
    TextView mCarView;
    /**
     * 户口在地
     */
    @BindView(R.id.house)
    TextView mHouseView;

    @BindView(R.id.industry)
    TextView mIndustryView;

    @BindView(R.id.career)
    TextView mCareerView;

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

        mSignatureView.setText(mVCardExtendObject.getStatus());
        mDemandView.setText(mVCardExtendObject.getDemand());
        mHobbyView.setText(mVCardExtendObject.getHobby());
        mIndustryView.setText(mVCardExtendObject.getIndustry());
        mCareerView.setText(mVCardExtendObject.getCareer());

        mIntroduceView.setText(mVCardExtendObject.getIntroduce());
        mCarView.setText(mVCardExtendObject.getCar());
        mHouseView.setText(mVCardExtendObject.getHouse());

        Address workarea = Address.fromProvinceCityText(mVCardExtendObject.getWorkarea());
        if (workarea != null) {
            mWorkareaView.setText(workarea.getDisplayName());
        } else {
            mWorkareaView.setText("");
        }

        Address birthplace = Address.fromProvinceCityText(mVCardExtendObject.getBirthplace());
        if (birthplace != null) {
            mBirthplaceView.setText(birthplace.getDisplayName());
        } else {
            mBirthplaceView.setText("");
        }

        Address residence = Address.fromProvinceCityText(mVCardExtendObject.getResidence());
        if (residence != null) {
            mResidenceView.setText(residence.getDisplayName());
        } else {
            mResidenceView.setText("");
        }
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

    @OnClick(R.id.btn_car)
    void onClickCar() {
        String selected = mVCardExtendObject.getCar();
        OptionPicker.OnOptionPickListener l = new OptionPicker.OnOptionPickListener() {
            @Override
            public void onOptionPicked(int index, String item) {
                mVCardExtendObject.setCar(item);
                EventBus.getDefault().post(mVCardExtendObject);
                XMPPManager.getInstance().getvCardExtendManager().setMyVCardExtend(mVCardExtendObject);
            }
        };
        PickerHelper.showCarPicker(this, selected, l);
    }

    @OnClick(R.id.btn_house)
    void onClickHouse() {
        String selected = mVCardExtendObject.getHouse();
        OptionPicker.OnOptionPickListener l = new OptionPicker.OnOptionPickListener() {
            @Override
            public void onOptionPicked(int index, String item) {
                mVCardExtendObject.setHouse(item);
                EventBus.getDefault().post(mVCardExtendObject);
                XMPPManager.getInstance().getvCardExtendManager().setMyVCardExtend(mVCardExtendObject);
            }
        };
        PickerHelper.showHousePicker(this, selected, l);
    }

    @OnClick(R.id.btn_career)
    void onClickCareer() {
        String selected = mVCardExtendObject.getCareer();
        OptionPicker.OnOptionPickListener l = new OptionPicker.OnOptionPickListener() {
            @Override
            public void onOptionPicked(int index, String item) {
                mVCardExtendObject.setCareer(item);
                EventBus.getDefault().post(mVCardExtendObject);
                XMPPManager.getInstance().getvCardExtendManager().setMyVCardExtend(mVCardExtendObject);
            }
        };
        PickerHelper.showCareerPicker(this, selected, l);
    }


    @OnClick(R.id.btn_industry)
    void onClickIndustry() {
        String selected = mVCardExtendObject.getIntroduce();
        OptionPicker.OnOptionPickListener l = new OptionPicker.OnOptionPickListener() {
            @Override
            public void onOptionPicked(int index, String item) {
                mVCardExtendObject.setIndustry(item);
                EventBus.getDefault().post(mVCardExtendObject);
                XMPPManager.getInstance().getvCardExtendManager().setMyVCardExtend(mVCardExtendObject);
            }
        };
        PickerHelper.showIndustryPicker(this, selected, l);
    }


    @OnClick(R.id.btn_demand)
    void onClickDemand() {
        Intent intent = new Intent(this, EditDemandActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.btn_signature)
    void onClickSignature() {
        Intent intent = new Intent(this, EditSignatureActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.btn_introduce)
    void onClickIntroduce() {
        Intent intent = new Intent(this, EditIntroduceActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.btn_hobby)
    void onClickHobby() {
        Intent intent = new Intent(this, EditHobbyActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.btn_workarea)
    void onClickWorkarea() {
        Address address = Address.fromJson(mVCardExtendObject.getWorkarea());
        PickerHelper.showProvinceCityPicker(this, address, new AddressPicker.OnAddressPickListener() {
            @Override
            public void onAddressPicked(Address address) {
                mVCardExtendObject.setWorkarea(address.toProvinceCityText());
                EventBus.getDefault().post(mVCardExtendObject);
                XMPPManager.getInstance().getvCardExtendManager().setMyVCardExtend(mVCardExtendObject);
            }
        });
    }

    @OnClick(R.id.btn_birthplace)
    void onClickBirthplace() {
        Address address = Address.fromJson(mVCardExtendObject.getBirthplace());
        PickerHelper.showProvinceCityPicker(this, address, new AddressPicker.OnAddressPickListener() {
            @Override
            public void onAddressPicked(Address address) {
                mVCardExtendObject.setBirthplace(address.toProvinceCityText());
                EventBus.getDefault().post(mVCardExtendObject);
                XMPPManager.getInstance().getvCardExtendManager().setMyVCardExtend(mVCardExtendObject);
            }

        });
    }

    @OnClick(R.id.btn_residence)
    void onClickResidence() {
        Address address = Address.fromJson(mVCardExtendObject.getResidence());
        PickerHelper.showProvinceCityPicker(this, address, new AddressPicker.OnAddressPickListener() {
            @Override
            public void onAddressPicked(Address address) {
                mVCardExtendObject.setResidence(address.toProvinceCityText());
                EventBus.getDefault().post(mVCardExtendObject);
                XMPPManager.getInstance().getvCardExtendManager().setMyVCardExtend(mVCardExtendObject);
            }

        });
    }

}
