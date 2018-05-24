package com.agmbat.meetyou.discovery.filter;

import android.view.View;

import com.agmbat.android.AppResources;
import com.agmbat.imsdk.asmack.XMPPManager;
import com.agmbat.imsdk.user.LoginUser;
import com.agmbat.meetyou.R;
import com.agmbat.meetyou.discovery.search.DiscoveryApi;
import com.agmbat.meetyou.discovery.search.DiscoveryApiResult;
import com.agmbat.meetyou.discovery.search.DiscoveryLoader;

/**
 * 综合搜索
 */
public class FilterLoader implements DiscoveryLoader {

    private FilterInfo mFilterInfo;

    @Override
    public String getName() {
        return AppResources.getString(R.string.discovery_search_user);
    }

    @Override
    public DiscoveryApiResult load(int page) {
        LoginUser current = XMPPManager.getInstance().getRosterManager().getLoginUser();
        if (current == null) {
            return null;
        }
        String phone = XMPPManager.getInstance().getConnectionUserName();
        String token = XMPPManager.getInstance().getTokenManager().getTokenRetry();
        String center = "30.5,111.2";
        int gender = mFilterInfo.getGender();
        String age = mFilterInfo.getStartAge() + "," + mFilterInfo.getEndAge();
        String height = mFilterInfo.getStartHeight() + "," + mFilterInfo.getEndHeight();
        int marriage = mFilterInfo.getMarriage();
        String birthplace = mFilterInfo.getBirthplaceText();
        if ("不限".equals(birthplace)) {
            birthplace = "";
        }
        String workarea = mFilterInfo.getWorkareaText();
        if ("不限".equals(workarea)) {
            workarea = "";
        }
        int education = mFilterInfo.getEducation();
        String career = mFilterInfo.getCareer();
        if ("不限".equals(career)) {
            career = "";
        }
        int wage = mFilterInfo.getWage();
        int house = mFilterInfo.getHouse();
        int car = mFilterInfo.getCar();
        return DiscoveryApi.search(phone, token, center, gender, age, height, marriage, birthplace,
                workarea, education, career, wage, house, car, page);
    }

    @Override
    public void setupViews(View view) {
        view.findViewById(R.id.btn_filter).setVisibility(View.VISIBLE);
        FilterView filterView = (FilterView) view.findViewById(R.id.filter_view);
        filterView.setOnFilterInfoChangeListener(new OnFilterInfoChangeListener() {
            @Override
            public void onFilterInfoChange(FilterInfo filterInfo) {
                mFilterInfo = filterInfo;
            }
        });
        mFilterInfo = filterView.getFilterInfo();
    }
}
