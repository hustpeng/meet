package com.agmbat.meetyou.search;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.TextView;

import com.agmbat.android.utils.WindowUtils;
import com.agmbat.imsdk.asmack.roster.ContactInfo;
import com.agmbat.meetyou.R;
import com.agmbat.picker.address.Address;
import com.agmbat.picker.helper.CarItem;
import com.agmbat.picker.helper.EducationItem;
import com.agmbat.picker.helper.HouseItem;
import com.agmbat.picker.helper.MarriageItem;
import com.agmbat.picker.helper.WageItem;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 我的信息界面
 */
public class MoreUserInfoActivity extends Activity {

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
        ContactInfo contactInfo = ViewUserHelper.getContactInfoFromIntent(getIntent());
        update(contactInfo);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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
     * 点击返回键
     */
    @OnClick(R.id.title_btn_back)
    void onClickBack() {
        finish();
    }

    private void update(ContactInfo user) {
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
