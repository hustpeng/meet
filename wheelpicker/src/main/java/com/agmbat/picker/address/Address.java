package com.agmbat.picker.address;

import android.text.TextUtils;

import com.agmbat.server.GsonHelper;
import com.google.gson.annotations.SerializedName;

/**
 * 对外描述地址
 */
public class Address {

    /**
     * 省
     */
    @SerializedName("province")
    private String province;

    /**
     * 市
     */
    @SerializedName("city")
    private String city;

    /**
     * 区
     */
    @SerializedName("county")
    private String county;

    /**
     * 将json串转为地址对象
     *
     * @param text
     * @return
     */
    public static Address fromJson(String text) {
        return GsonHelper.fromJson(text, Address.class);
    }

    /**
     * 使用 "," 分隔存储
     *
     * @param text
     * @return
     */
    public static Address fromProvinceCityText(String text) {
        if (TextUtils.isEmpty(text)) {
            return null;
        }
        String[] array = text.split(",");
        if (array.length != 2) {
            return null;
        }
        Address address = new Address();
        address.province = array[0];
        address.city = array[1];
        return address;
    }

    /**
     * 转成json
     *
     * @return
     */
    public String toJson() {
        return GsonHelper.toJson(this);
    }

    /**
     * 转成省市文本,用 "," 分隔
     *
     * @return
     */
    public String toProvinceCityText() {
        StringBuilder builder = new StringBuilder();
        builder.append(province);
        builder.append(",");
        builder.append(city);
        return builder.toString();
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCounty() {
        return county;
    }

    public void setCounty(String county) {
        this.county = county;
    }

    /**
     * 获取显示的名称
     *
     * @return
     */
    public String getDisplayName() {
        StringBuilder builder = new StringBuilder();
        if (!TextUtils.isEmpty(province)) {
            builder.append(province);
        }
        if (!TextUtils.isEmpty(city)) {
            builder.append(city);
        }
        if (!TextUtils.isEmpty(county)) {
            builder.append(county);
        }
        return builder.toString();
    }
}
