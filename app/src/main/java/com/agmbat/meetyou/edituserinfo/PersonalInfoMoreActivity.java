package com.agmbat.meetyou.edituserinfo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.TextView;

import com.agmbat.android.utils.WindowUtils;
import com.agmbat.imsdk.asmack.XMPPManager;
import com.agmbat.imsdk.imevent.LoginUserUpdateEvent;
import com.agmbat.imsdk.user.LoginUser;
import com.agmbat.meetyou.R;
import com.agmbat.picker.NumberPicker;
import com.agmbat.picker.OptionPicker;
import com.agmbat.picker.SinglePicker;
import com.agmbat.picker.address.Address;
import com.agmbat.picker.address.AddressPicker;
import com.agmbat.picker.helper.CarItem;
import com.agmbat.picker.helper.EducationItem;
import com.agmbat.picker.helper.HouseItem;
import com.agmbat.picker.helper.MarriageItem;
import com.agmbat.picker.helper.PickerHelper;
import com.agmbat.picker.helper.WageItem;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

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
     * 购车情况
     */
    @BindView(R.id.car)
    TextView mCarView;

    /**
     * 房子情况
     */
    @BindView(R.id.house)
    TextView mHouseView;

    /**
     * 行业
     */
    @BindView(R.id.industry)
    TextView mIndustryView;

    @BindView(R.id.career)
    TextView mCareerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowUtils.setStatusBarColor(this, getResources().getColor(R.color.bg_status_bar));
        setContentView(R.layout.activity_personal_more_info);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        update(XMPPManager.getInstance().getRosterManager().getLoginUser());
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
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(LoginUserUpdateEvent event) {
        LoginUser user = event.getLoginUser();
        update(user);
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
        final LoginUser user = XMPPManager.getInstance().getRosterManager().getLoginUser();
        int selected = user.getHeight();
        NumberPicker.OnNumberPickListener l = new NumberPicker.OnNumberPickListener() {
            @Override
            public void onNumberPicked(int index, Number item) {
                user.setHeight(item.intValue());
                XMPPManager.getInstance().getRosterManager().saveLoginUser(user);
            }
        };
        PickerHelper.showHeightPicker(this, selected, l);
    }

    @OnClick(R.id.btn_weight)
    void onClickWeight() {
        final LoginUser user = XMPPManager.getInstance().getRosterManager().getLoginUser();
        int selected = user.getWeight();
        NumberPicker.OnNumberPickListener l = new NumberPicker.OnNumberPickListener() {
            @Override
            public void onNumberPicked(int index, Number item) {
                user.setWeight(item.intValue());
                XMPPManager.getInstance().getRosterManager().saveLoginUser(user);
            }
        };
        PickerHelper.showWeightPicker(this, selected, l);
    }

    @OnClick(R.id.btn_wage)
    void onClickWage() {
        final LoginUser user = XMPPManager.getInstance().getRosterManager().getLoginUser();
        int value = user.getWage();
        SinglePicker.OnItemPickListener<WageItem> l = new SinglePicker.OnItemPickListener<WageItem>() {
            @Override
            public void onItemPicked(int index, WageItem item) {
                user.setWage(item.mValue);
                XMPPManager.getInstance().getRosterManager().saveLoginUser(user);
            }
        };
        WageItem item = WageItem.valueOf(value);
        PickerHelper.showWagePicker(this, item, l);
    }

    @OnClick(R.id.btn_education)
    void onClickEducation() {
        final LoginUser user = XMPPManager.getInstance().getRosterManager().getLoginUser();
        int value = user.getEducation();
        EducationItem item = EducationItem.valueOf(value);
        SinglePicker.OnItemPickListener<EducationItem> l = new SinglePicker.OnItemPickListener<EducationItem>() {
            @Override
            public void onItemPicked(int index, EducationItem item) {
                user.setEducation(item.mValue);
                XMPPManager.getInstance().getRosterManager().saveLoginUser(user);
            }
        };
        PickerHelper.showEducationPicker(this, item, l);
    }

    @OnClick(R.id.btn_marriage)
    void onClickMarriage() {
        final LoginUser user = XMPPManager.getInstance().getRosterManager().getLoginUser();
        int selected = user.getMarriage();
        MarriageItem item = MarriageItem.valueOf(selected);
        SinglePicker.OnItemPickListener<MarriageItem> l = new SinglePicker.OnItemPickListener<MarriageItem>() {
            @Override
            public void onItemPicked(int index, MarriageItem item) {
                user.setMarriage(item.mValue);
                XMPPManager.getInstance().getRosterManager().saveLoginUser(user);
            }
        };
        PickerHelper.showMarriagePicker(this, item, l);
    }

    @OnClick(R.id.btn_car)
    void onClickCar() {
        final LoginUser user = XMPPManager.getInstance().getRosterManager().getLoginUser();
        int selected = user.getCar();
        CarItem item = CarItem.valueOf(selected);
        SinglePicker.OnItemPickListener<CarItem> l = new SinglePicker.OnItemPickListener<CarItem>() {
            @Override
            public void onItemPicked(int index, CarItem item) {
                user.setCar(item.mValue);
                XMPPManager.getInstance().getRosterManager().saveLoginUser(user);
            }
        };
        PickerHelper.showCarPicker(this, item, l);
    }

    @OnClick(R.id.btn_house)
    void onClickHouse() {
        final LoginUser user = XMPPManager.getInstance().getRosterManager().getLoginUser();
        int selected = user.getHouse();
        HouseItem item = HouseItem.valueOf(selected);
        SinglePicker.OnItemPickListener<HouseItem> l = new SinglePicker.OnItemPickListener<HouseItem>() {
            @Override
            public void onItemPicked(int index, HouseItem item) {
                user.setHouse(item.mValue);
                XMPPManager.getInstance().getRosterManager().saveLoginUser(user);
            }
        };
        PickerHelper.showHousePicker(this, item, l);
    }

    @OnClick(R.id.btn_career)
    void onClickCareer() {
        final LoginUser user = XMPPManager.getInstance().getRosterManager().getLoginUser();
        String selected = user.getCareer();
        OptionPicker.OnOptionPickListener l = new OptionPicker.OnOptionPickListener() {
            @Override
            public void onOptionPicked(int index, String item) {
                user.setCareer(item);
                XMPPManager.getInstance().getRosterManager().saveLoginUser(user);
            }
        };
        PickerHelper.showCareerPicker(this, selected, l);
    }


    @OnClick(R.id.btn_industry)
    void onClickIndustry() {
        final LoginUser user = XMPPManager.getInstance().getRosterManager().getLoginUser();
        String selected = user.getIndustry();
        OptionPicker.OnOptionPickListener l = new OptionPicker.OnOptionPickListener() {
            @Override
            public void onOptionPicked(int index, String item) {
                user.setIndustry(item);
                XMPPManager.getInstance().getRosterManager().saveLoginUser(user);
            }
        };
        PickerHelper.showIndustryPicker(this, selected, l);
    }


    @OnClick(R.id.btn_demand)
    void onClickDemand() {
        Intent intent = new Intent(this, EditDemandActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @OnClick(R.id.btn_signature)
    void onClickSignature() {
        Intent intent = new Intent(this, EditSignatureActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @OnClick(R.id.btn_introduce)
    void onClickIntroduce() {
        Intent intent = new Intent(this, EditIntroduceActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @OnClick(R.id.btn_hobby)
    void onClickHobby() {
        Intent intent = new Intent(this, EditHobbyActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @OnClick(R.id.btn_workarea)
    void onClickWorkarea() {
        final LoginUser user = XMPPManager.getInstance().getRosterManager().getLoginUser();
        Address address = Address.fromProvinceCityText(user.getWorkarea());
        PickerHelper.showProvinceCityPicker(this, address, new AddressPicker.OnAddressPickListener() {
            @Override
            public void onAddressPicked(Address address) {
                user.setWorkarea(address.toProvinceCityText());
                XMPPManager.getInstance().getRosterManager().saveLoginUser(user);
            }
        });
    }

    @OnClick(R.id.btn_birthplace)
    void onClickBirthplace() {
        final LoginUser user = XMPPManager.getInstance().getRosterManager().getLoginUser();
        Address address = Address.fromProvinceCityText(user.getBirthplace());
        PickerHelper.showProvinceCityPicker(this, address, new AddressPicker.OnAddressPickListener() {
            @Override
            public void onAddressPicked(Address address) {
                user.setBirthplace(address.toProvinceCityText());
                XMPPManager.getInstance().getRosterManager().saveLoginUser(user);
            }

        });
    }

    @OnClick(R.id.btn_residence)
    void onClickResidence() {
        final LoginUser user = XMPPManager.getInstance().getRosterManager().getLoginUser();
        Address address = Address.fromProvinceCityText(user.getResidence());
        PickerHelper.showProvinceCityPicker(this, address, new AddressPicker.OnAddressPickListener() {
            @Override
            public void onAddressPicked(Address address) {
                user.setResidence(address.toProvinceCityText());
                XMPPManager.getInstance().getRosterManager().saveLoginUser(user);
            }

        });
    }

    private void update(LoginUser user) {
        mHeightView.setText(String.valueOf(user.getHeight()) + "cm");
        mWeightView.setText(String.valueOf(user.getWeight()) + "kg");
        WageItem wageItem = WageItem.valueOf(user.getWage());
        mWageView.setText(wageItem == null ? "" : wageItem.mName);
        EducationItem educationItem = EducationItem.valueOf(user.getEducation());
        mEducationView.setText(educationItem == null ? "" : educationItem.mName);

        MarriageItem marriageItem = MarriageItem.valueOf(user.getMarriage());
        mMarriageView.setText(marriageItem == null ? "" : marriageItem.mName);

        mSignatureView.setText(user.getStatus());
        mDemandView.setText(user.getDemand());
        mHobbyView.setText(user.getHobby());
        mIndustryView.setText(user.getIndustry());
        mCareerView.setText(user.getCareer());

        mIntroduceView.setText(user.getIntroduce());

        CarItem carItem = CarItem.valueOf(user.getCar());
        mCarView.setText(carItem == null ? "" : carItem.mName);

        HouseItem houseItem = HouseItem.valueOf(user.getHouse());
        mHouseView.setText(houseItem == null ? "" : houseItem.mName);

        Address workarea = Address.fromProvinceCityText(user.getWorkarea());
        if (workarea != null) {
            mWorkareaView.setText(workarea.getDisplayName());
        } else {
            mWorkareaView.setText("");
        }

        Address birthplace = Address.fromProvinceCityText(user.getBirthplace());
        if (birthplace != null) {
            mBirthplaceView.setText(birthplace.getDisplayName());
        } else {
            mBirthplaceView.setText("");
        }

        Address residence = Address.fromProvinceCityText(user.getResidence());
        if (residence != null) {
            mResidenceView.setText(residence.getDisplayName());
        } else {
            mResidenceView.setText("");
        }
    }

}
