package com.agmbat.picker.helper.filter;

import android.content.Context;

import com.agmbat.android.AppResources;
import com.agmbat.picker.OptionPicker;
import com.agmbat.picker.R;
import com.agmbat.picker.SinglePicker;
import com.agmbat.picker.address.Address;
import com.agmbat.picker.address.AddressPicker;
import com.agmbat.picker.address.Province;
import com.agmbat.picker.helper.Option;
import com.agmbat.picker.helper.PickerHelper;
import com.agmbat.server.GsonHelper;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class FilterHelper {

    /**
     * 地址缓存信息
     */
    private static List<Province> sProvinceCache = null;

    /**
     * 由于初始化地址选择信息需要一定时间,建议放在异步去初始化
     */
    private static void initPickerInfo() {
        if (sProvinceCache == null) {
            String text = AppResources.readAssetFile("wheelpicker_filter/city2.json");
            Type type = new TypeToken<List<Province>>() {
            }.getType();
            sProvinceCache = GsonHelper.fromJson(text, type);
        }
    }

    /**
     * 显示性别选择器
     *
     * @param context
     * @param selected 默认选中的性别
     * @param l
     */
    public static void showGenderFilterPicker(Context context, GenderFilterItem selected,
                                              SinglePicker.OnItemPickListener<GenderFilterItem> l) {
        Option<GenderFilterItem> option = GenderFilterItem.getOption();
        SinglePicker<GenderFilterItem> picker = new SinglePicker<>(context, option.mItems);
        picker.setSelectedIndex(option.index(selected));
        picker.setOnItemPickListener(l);
        PickerHelper.configPickerShow(picker);
    }

    /**
     * 显示婚况选择器
     *
     * @param context
     * @param selected 默认选中的婚况
     * @param l
     */
    public static void showMarriageFilterPicker(Context context, MarriageFilterItem selected,
                                                SinglePicker.OnItemPickListener<MarriageFilterItem> l) {
        Option<MarriageFilterItem> option = MarriageFilterItem.getOption();
        SinglePicker<MarriageFilterItem> picker = new SinglePicker<>(context, option.mItems);
        picker.setSelectedIndex(option.index(selected));
        picker.setOnItemPickListener(l);
        PickerHelper.configPickerShow(picker);
    }

    /**
     * 显示学历选择器
     *
     * @param context
     * @param selected 默认选中的学历
     * @param l
     */
    public static void showEducationFilterPicker(Context context, EducationFilterItem selected,
                                                 SinglePicker.OnItemPickListener<EducationFilterItem> l) {
        Option<EducationFilterItem> option = EducationFilterItem.getOption();
        SinglePicker<EducationFilterItem> picker = new SinglePicker<>(context, option.mItems);
        picker.setSelectedIndex(option.index(selected));
        picker.setOnItemPickListener(l);
        PickerHelper.configPickerShow(picker);
    }

    /**
     * 显示职业选择器
     *
     * @param context
     * @param career  默认选中
     * @param l
     */
    public static void showCareerFilterPicker(Context context, String career, OptionPicker.OnOptionPickListener l) {
        PickerHelper.showOptionPicker(context, career, l, "wheelpicker_filter/career.json");
    }

    /**
     * 显示月薪选择器
     *
     * @param context
     * @param selected
     * @param l
     */
    public static void showWageFilterPicker(Context context, WageFilterItem selected,
                                            SinglePicker.OnItemPickListener<WageFilterItem> l) {
        // 参考支付宝定义
        // 1000, 3000, 5000, 1万, 2万, 5万, 10万, 50万 100万以上
        Option<WageFilterItem> option = WageFilterItem.getOption();
        SinglePicker<WageFilterItem> picker = new SinglePicker<>(context, option.mItems);
        picker.setSelectedIndex(option.index(selected));
        picker.setOnItemPickListener(l);
        PickerHelper.configPickerShow(picker);
    }

    /**
     * 显示房子情况选择器
     *
     * @param context
     * @param selected 默认选中
     * @param l
     */
    public static void showHouseFilterPicker(Context context, HouseFilterItem selected,
                                             SinglePicker.OnItemPickListener<HouseFilterItem> l) {
        Option<HouseFilterItem> option = HouseFilterItem.getOption();
        SinglePicker<HouseFilterItem> picker = new SinglePicker<>(context, option.mItems);
        picker.setSelectedIndex(option.index(selected));
        picker.setOnItemPickListener(l);
        PickerHelper.configPickerShow(picker);
    }

    /**
     * 显示车况选择器
     *
     * @param context
     * @param selected 默认选中
     * @param l
     */
    public static void showCarFilterPicker(Context context, CarFilterItem selected,
                                           SinglePicker.OnItemPickListener<CarFilterItem> l) {
        Option<CarFilterItem> option = CarFilterItem.getOption();
        SinglePicker<CarFilterItem> picker = new SinglePicker<>(context, option.mItems);
        picker.setSelectedIndex(option.index(selected));
        picker.setOnItemPickListener(l);
        PickerHelper.configPickerShow(picker);
    }


    /**
     * 显示选择[省市]面板
     *
     * @param context
     * @param l
     */
    public static void showProvinceCityFilterPicker(Context context, Address address, AddressPicker.OnAddressPickListener l) {
        showAddressPicker(context, address, false, true, l);
    }

    /**
     * 显示地址显示面板
     *
     * @param context
     * @param address      默认选择的地址
     * @param hideProvince 是否隐藏省
     * @param hideCounty   是否隐藏区
     * @param l
     */
    private static void showAddressPicker(Context context, Address address, boolean hideProvince,
                                          boolean hideCounty, AddressPicker.OnAddressPickListener l) {
        if (sProvinceCache == null) {
            initPickerInfo();
        }
        AddressPicker picker = new AddressPicker(context, sProvinceCache);
        picker.setHideProvince(hideProvince);
        picker.setHideCounty(hideCounty);
        if (hideCounty) {
            // 将屏幕分为3份，省级和地级的比例为1:2
            picker.setColumnWeight(1 / 3.0f, 2 / 3.0f);
        } else {
            //省级、地级和县级的比例为2:3:3
            picker.setColumnWeight(2 / 8.0f, 3 / 8.0f, 3 / 8.0f);
        }
        // 支持传入空地址
        if (address == null) {
            address = new Address();
        }
        String selectedProvince = address.getProvince();
        String selectedCity = address.getCity();
        String selectedCounty = address.getCounty();
        picker.setSelectedItem(selectedProvince, selectedCity, selectedCounty);
        picker.setOnAddressPickListener(l);
        picker.setAnimationStyle(R.style.PickerPopupAnimation);
        picker.show();
    }

}
