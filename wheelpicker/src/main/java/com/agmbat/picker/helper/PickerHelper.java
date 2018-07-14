package com.agmbat.picker.helper;

import android.content.Context;
import android.view.Gravity;

import com.agmbat.android.AppResources;
import com.agmbat.picker.NumberPicker;
import com.agmbat.picker.OptionPicker;
import com.agmbat.picker.R;
import com.agmbat.picker.SinglePicker;
import com.agmbat.picker.address.Address;
import com.agmbat.picker.address.AddressPicker;
import com.agmbat.picker.address.Province;
import com.agmbat.picker.tag.CategoryTagPicker;
import com.agmbat.picker.wheelview.DividerConfig;
import com.agmbat.server.GsonHelper;
import com.agmbat.tagpicker.CategoryTag;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

/**
 * 选择器辅助类
 */
public class PickerHelper {

    /**
     * 地址缓存信息
     */
    private static List<Province> sProvinceCache = null;

    /**
     * 由于初始化地址选择信息需要一定时间,建议放在异步去初始化
     */
    private static void initPickerInfo() {
        if (sProvinceCache == null) {
            String text = AppResources.readAssetFile("wheelpicker/city2.json");
            Type type = new TypeToken<List<Province>>() {
            }.getType();
            sProvinceCache = GsonHelper.fromJson(text, type);
        }
    }

    /**
     * 显示选择[省市区]面板
     *
     * @param context
     * @param l
     */
    public static void showProvinceCityCountyPicker(Context context, Address address, AddressPicker.OnAddressPickListener l) {
        showAddressPicker(context, address, false, false, l);
    }

    /**
     * 显示选择[省市]面板
     *
     * @param context
     * @param l
     */
    public static void showProvinceCityPicker(Context context, Address address, AddressPicker.OnAddressPickListener l) {
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

    /**
     * 显示行业选择器
     *
     * @param context
     * @param industry 默认选中
     * @param l
     */
    public static void showIndustryPicker(Context context, String industry, OptionPicker.OnOptionPickListener l) {
        showOptionPicker(context, industry, l, "wheelpicker/industry.json");
    }

    /**
     * 显示职业选择器
     *
     * @param context
     * @param career  默认选中
     * @param l
     */
    public static void showCareerPicker(Context context, String career, OptionPicker.OnOptionPickListener l) {
        showOptionPicker(context, career, l, "wheelpicker/career.json");
    }


    /**
     * 显示房子情况选择器
     *
     * @param context
     * @param selected 默认选中
     * @param l
     */
    public static void showHousePicker(Context context, HouseItem selected,
                                       SinglePicker.OnItemPickListener<HouseItem> l) {
        // 默认[有房], 可选择 [没房, 有房]
        Option<HouseItem> option = HouseItem.getOption();
        SinglePicker<HouseItem> picker = new SinglePicker<>(context, option.mItems);
        picker.setSelectedIndex(option.index(selected));
        picker.setOnItemPickListener(l);
        configPickerShow(picker);
    }

    /**
     * 显示车况选择器
     *
     * @param context
     * @param selected 默认选中
     * @param l
     */
    public static void showCarPicker(Context context, CarItem selected, SinglePicker.OnItemPickListener<CarItem> l) {
        // 默认[有车], 可选择 [没车, 有车]
        Option<CarItem> option = CarItem.getOption();
        SinglePicker<CarItem> picker = new SinglePicker<>(context, option.mItems);
        picker.setSelectedIndex(option.index(selected));
        picker.setOnItemPickListener(l);
        configPickerShow(picker);
    }

    /**
     * 显示婚况选择器
     *
     * @param context
     * @param selected 默认选中的婚况
     * @param l
     */
    public static void showMarriagePicker(Context context, MarriageItem selected,
                                          SinglePicker.OnItemPickListener<MarriageItem> l) {
        // 默认未婚, 可选择 未婚, 已婚
        Option<MarriageItem> option = MarriageItem.getOption();
        SinglePicker<MarriageItem> picker = new SinglePicker<>(context, option.mItems);
        picker.setSelectedIndex(option.index(selected));
        picker.setOnItemPickListener(l);
        configPickerShow(picker);
    }

    /**
     * 显示学历选择器
     *
     * @param context
     * @param selected 默认选中的性别
     * @param l
     */
    public static void showGenderPicker(Context context, GenderItem selected,
                                        SinglePicker.OnItemPickListener<GenderItem> l) {
        // 默认女, 可选择 男, 女
        Option<GenderItem> option = GenderItem.getOption();
        SinglePicker<GenderItem> picker = new SinglePicker<>(context, option.mItems);
        picker.setSelectedIndex(option.index(selected));
        picker.setOnItemPickListener(l);
        configPickerShow(picker);
        //        picker.setWidth(picker.getScreenWidthPixels() / 2);
        //        picker.setGravity(Gravity.CENTER);
        //        picker.setShadowColor(Color.RED, 40);
        //        picker.setTextSize(11);
    }


    public static void configPickerShow(SinglePicker picker) {
        picker.setCanceledOnTouchOutside(false);
        picker.setOffset(3); // 偏移量
        picker.setCycleDisable(true);
        picker.setDividerRatio(DividerConfig.FILL);
        picker.setAnimationStyle(R.style.PickerPopupAnimation);
        picker.show();
    }

    /**
     * 显示学历选择器
     *
     * @param context
     * @param selected 默认选中的学历
     * @param l
     */
    public static void showEducationPicker(Context context, EducationItem selected,
                                           SinglePicker.OnItemPickListener<EducationItem> l) {
        // 默认本科, 可选择  小学, 初中, 高中, 专科, 本科, 硕士, 博士
        Option<EducationItem> option = EducationItem.getOption();
        SinglePicker<EducationItem> picker = new SinglePicker<>(context, option.mItems);
        picker.setSelectedIndex(option.index(selected));
        picker.setOnItemPickListener(l);
        configPickerShow(picker);
    }

    /**
     * 显示月薪选择器
     *
     * @param context
     * @param selected
     * @param l
     */
    public static void showWagePicker(Context context, WageItem selected,
                                      SinglePicker.OnItemPickListener<WageItem> l) {
        // 参考支付宝定义
        // 1000, 3000, 5000, 1万, 2万, 5万, 10万, 50万 100万以上
        Option<WageItem> option = WageItem.getOption();
        SinglePicker<WageItem> picker = new SinglePicker<>(context, option.mItems);
        picker.setSelectedIndex(option.index(selected));
        picker.setOnItemPickListener(l);
        configPickerShow(picker);
    }

    /**
     * 显示使用json配置的选项
     *
     * @param context
     * @param item
     * @param l
     * @param optionPath
     */
    public static void showOptionPicker(Context context, String item, OptionPicker.OnOptionPickListener l,
                                        String optionPath) {
        String text = AppResources.readAssetFile(optionPath);
        Type jsonType = new TypeToken<Option<String>>() {
        }.getType();
        Option<String> option = GsonHelper.fromJson(text, jsonType);
        OptionPicker picker = new OptionPicker(context, option.mItems);
        picker.setCanceledOnTouchOutside(false);
        picker.setSelectedIndex(option.index(item));
        picker.setOffset(3); // 偏移量
        picker.setCycleDisable(true);
        picker.setOnOptionPickListener(l);
        picker.setDividerRatio(DividerConfig.FILL);
        picker.setAnimationStyle(R.style.PickerPopupAnimation);
        picker.show();
    }


    /**
     * 显示选择身高的Picker
     *
     * @param context
     * @param selected 显示时选择的一项
     * @param l
     */
    public static void showHeightPicker(Context context, int selected, NumberPicker.OnNumberPickListener l) {
        // 参考支付宝定义
        int start = 138;
        int end = 230;
        if (selected < start || selected > end) {
            selected = 170;
        }
        NumberPicker picker = new NumberPicker(context);
        picker.setUnit("cm");
        picker.setDividerRatio(DividerConfig.FILL);
        picker.setCycleDisable(true);
        picker.setOffset(3); // 偏移量
        picker.setRange(start, end, 1); // 数字范围
        picker.setSelectedItem(selected);
        picker.setOnNumberPickListener(l);
        picker.setAnimationStyle(R.style.PickerPopupAnimation);
        picker.show();
    }

    /**
     * 显示选择体重的Picker
     *
     * @param context
     * @param selected 显示时选择的一项
     * @param l
     */
    public static void showWeightPicker(Context context, int selected, NumberPicker.OnNumberPickListener l) {
        // 参考支付宝定义
        int start = 30;
        int end = 150;
        if (selected < start || selected > end) {
            selected = 50;
        }
        NumberPicker picker = new NumberPicker(context);
        picker.setUnit("kg");
        picker.setDividerRatio(DividerConfig.FILL);
        picker.setCycleDisable(true);
        picker.setOffset(3); // 偏移量
        picker.setRange(start, end, 1); // 数字范围
        picker.setSelectedItem(selected);
        picker.setOnNumberPickListener(l);
        picker.setAnimationStyle(R.style.PickerPopupAnimation);
        picker.show();
    }


    /**
     * 显示选择年的Picker
     */
    public static void showYearPicker(Context context, int selectedYear, NumberPicker.OnNumberPickListener l) {
        int startYear = 1920;
        int endYear = 2018;
        if (selectedYear < startYear || selectedYear > endYear) {
            selectedYear = 1990;
        }
        NumberPicker picker = new NumberPicker(context);
        picker.setWidth(picker.getScreenWidthPixels() / 2);
        picker.setCycleDisable(true);
        picker.setDividerVisible(false);
        picker.setOffset(3); // 偏移量
        picker.setGravity(Gravity.CENTER);
        picker.setRange(startYear, endYear, 1); // 数字范围
        picker.setSelectedItem(selectedYear);
        picker.setLabel("年");
        picker.setOnNumberPickListener(l);
        picker.show();
    }


    /**
     * 兴趣分类显示选择
     *
     * @param context
     * @param selected
     */
    public static void showHobbyCategoryPicker(Context context, List<String> selected) {
        String text = AppResources.readAssetFile("wheelpicker/hobby_category.json");
        Type jsonType = new TypeToken<List<CategoryTag>>() {
        }.getType();
        List<CategoryTag> list = GsonHelper.fromJson(text, jsonType);
        CategoryTagPicker picker = new CategoryTagPicker(context);
        picker.setMaxSelectCount(5);
        picker.setCategoryTagList(list);
        picker.setCheckedList(selected);
        picker.getWindow().setWindowAnimations(R.style.PickerPopupAnimation);
        picker.show();
    }


}
