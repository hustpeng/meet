package com.agmbat.picker.address;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;

import com.agmbat.picker.linkage.LinkagePicker;
import com.agmbat.picker.wheelview.DividerConfig;
import com.agmbat.picker.wheelview.WheelView;

import java.util.ArrayList;
import java.util.List;

/**
 * 地址选择器（包括省级、地级、县级），地址数据见demo项目assets目录下。
 * “assets/city.json”转换自国家统计局（http://www.stats.gov.cn/tjsj/tjbz/xzqhdm）
 *
 * @see Province
 * @see City
 * @see County
 */
public class AddressPicker extends LinkagePicker<Province, City, County> {

    private OnAddressPickListener onAddressPickListener;
    private OnWheelListener onWheelListener;
    //只显示地市及区县
    private boolean hideProvince = false;
    //只显示省份及地市
    private boolean hideCounty = false;
    //省市区数据
    private List<Province> provinces = new ArrayList<>();

    public AddressPicker(Context context, List<Province> provinces) {
        super(context, new AddressProvider(provinces));
        this.provinces = provinces;
    }

    /**
     * 设置默认选中的省市县
     */
    public void setSelectedItem(Province province, City city, County county) {
        super.setSelectedItem(province, city, county);
    }

    public void setSelectedItem(String province, String city, String county) {
        setSelectedItem(new Province(province), new City(city), new County(county));
    }

    @NonNull
    public Province getSelectedProvince() {
        return provinces.get(selectedFirstIndex);
    }

    @Nullable
    public City getSelectedCity() {
        List<City> cities = getSelectedProvince().getCities();
        if (cities.size() == 0) {
            return null;//可能没有第二级数据
        }
        return cities.get(selectedSecondIndex);
    }

    @Nullable
    public County getSelectedCounty() {
        City selectedCity = getSelectedCity();
        if (selectedCity == null) {
            return null;
        }
        List<County> counties = selectedCity.getCounties();
        if (counties.size() == 0) {
            return null;//可能没有第三级数据
        }
        return counties.get(selectedThirdIndex);
    }

    /**
     * 隐藏省级行政区，只显示地市级和区县级。
     * 设置为true的话，地址数据中只需要某个省份的即可
     * 参见demo中的“assets/city2.json”
     */
    public void setHideProvince(boolean hideProvince) {
        this.hideProvince = hideProvince;
    }

    /**
     * 隐藏县级行政区，只显示省级和市级。
     * 设置为true的话，hideProvince将强制为false
     * 数据源依然使用demo中的“assets/city.json” 仅在逻辑上隐藏县级选择框，实际项目中应该去掉县级数据。
     */
    public void setHideCounty(boolean hideCounty) {
        this.hideCounty = hideCounty;
    }

    /**
     * 设置滑动监听器
     */
    public void setOnWheelListener(OnWheelListener onWheelListener) {
        this.onWheelListener = onWheelListener;
    }

    public void setOnAddressPickListener(OnAddressPickListener listener) {
        this.onAddressPickListener = listener;
    }

    @NonNull
    @Override
    protected View makeCenterView() {
        if (null == provider) {
            throw new IllegalArgumentException("please set address provider before make view");
        }
        float provinceWeight = firstColumnWeight;
        float cityWeight = secondColumnWeight;
        float countyWeight = thirdColumnWeight;
        if (hideCounty) {
            hideProvince = false;
        }
        if (hideProvince) {
            provinceWeight = 0;
            cityWeight = firstColumnWeight;
            countyWeight = secondColumnWeight;
        }
        dividerConfig.setRatio(DividerConfig.FILL);

        LinearLayout layout = new LinearLayout(mContext);
        layout.setOrientation(LinearLayout.HORIZONTAL);
        layout.setGravity(Gravity.CENTER);

        final WheelView provinceView = createWheelView();
        provinceView.setLayoutParams(new LinearLayout.LayoutParams(0, WRAP_CONTENT, provinceWeight));
        layout.addView(provinceView);
        if (hideProvince) {
            provinceView.setVisibility(View.GONE);
        }

        final WheelView cityView = createWheelView();
        cityView.setLayoutParams(new LinearLayout.LayoutParams(0, WRAP_CONTENT, cityWeight));
        layout.addView(cityView);

        final WheelView countyView = createWheelView();
        countyView.setLayoutParams(new LinearLayout.LayoutParams(0, WRAP_CONTENT, countyWeight));
        layout.addView(countyView);
        if (hideCounty) {
            countyView.setVisibility(View.GONE);
        }

        provinceView.setItems(provider.initFirstData(), selectedFirstIndex);
        provinceView.setOnItemSelectListener(new WheelView.OnItemSelectListener() {
            @Override
            public void onSelected(int index) {
                selectedFirstIndex = index;
                selectedFirstItem = getSelectedProvince();
                if (onWheelListener != null) {
                    onWheelListener.onProvinceWheeled(selectedFirstIndex, selectedFirstItem);
                }
                selectedSecondIndex = 0;//重置地级索引
                selectedThirdIndex = 0;//重置县级索引
                //根据省份获取地市
                //noinspection unchecked
                List<City> cities = provider.linkageSecondData(selectedFirstIndex);
                if (cities.size() > 0) {
                    selectedSecondItem = cities.get(selectedSecondIndex);
                    cityView.setItems(cities, selectedSecondIndex);
                } else {
                    selectedSecondItem = null;
                    cityView.setItems(new ArrayList<String>());
                }
                //根据地市获取区县
                //noinspection unchecked
                List<County> counties = provider.linkageThirdData(selectedFirstIndex, selectedSecondIndex);
                if (counties.size() > 0) {
                    selectedThirdItem = counties.get(selectedThirdIndex);
                    countyView.setItems(counties, selectedThirdIndex);
                } else {
                    selectedThirdItem = null;
                    countyView.setItems(new ArrayList<String>());
                }
            }
        });

        cityView.setItems(provider.linkageSecondData(selectedFirstIndex), selectedSecondIndex);
        cityView.setOnItemSelectListener(new WheelView.OnItemSelectListener() {
            @Override
            public void onSelected(int index) {
                selectedSecondIndex = index;
                selectedSecondItem = getSelectedCity();
                if (onWheelListener != null) {
                    onWheelListener.onCityWheeled(selectedSecondIndex, selectedSecondItem);
                }
                selectedThirdIndex = 0;//重置县级索引
                //根据地市获取区县
                //noinspection unchecked
                List<County> counties = provider.linkageThirdData(selectedFirstIndex, selectedSecondIndex);
                if (counties.size() > 0) {
                    selectedThirdItem = counties.get(selectedThirdIndex);
                    //若不是用户手动滚动，说明联动需要指定默认项
                    countyView.setItems(counties, selectedThirdIndex);
                } else {
                    selectedThirdItem = null;
                    countyView.setItems(new ArrayList<String>());
                }
            }
        });

        countyView.setItems(provider.linkageThirdData(selectedFirstIndex, selectedSecondIndex), selectedThirdIndex);
        countyView.setOnItemSelectListener(new WheelView.OnItemSelectListener() {
            @Override
            public void onSelected(int index) {
                selectedThirdIndex = index;
                selectedThirdItem = getSelectedCounty();
                if (onWheelListener != null) {
                    onWheelListener.onCountyWheeled(selectedThirdIndex, selectedThirdItem);
                }
            }
        });
        return layout;
    }

    @Override
    public void onSubmit() {
        if (onAddressPickListener != null) {
            Province province = getSelectedProvince();
            City city = getSelectedCity();
            County county = null;
            if (!hideCounty) {
                county = getSelectedCounty();
            }
            Address address = new Address();
            address.setProvince(province.getName());
            address.setCity(city == null ? "" : city.getName());
            address.setCounty(county == null ? "" : county.getName());
            onAddressPickListener.onAddressPicked(address);
        }
    }

    /**
     * 地址选择回调
     */
    public interface OnAddressPickListener {

        void onAddressPicked(Address address);

    }

    /**
     * 滑动回调
     */
    public interface OnWheelListener {

        void onProvinceWheeled(int index, Province province);

        void onCityWheeled(int index, City city);

        void onCountyWheeled(int index, County county);

    }


}
