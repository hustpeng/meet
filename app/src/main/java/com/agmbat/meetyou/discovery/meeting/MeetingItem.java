package com.agmbat.meetyou.discovery.meeting;

import com.google.gson.annotations.SerializedName;

/**
 * 每一条记录
 */
public class MeetingItem {

    //
//         * "result": true,      //true API调用成功，否则调用失败
//                 * "records": 2,       //匹配的总记录数
//                 * "resp": [
//                 * {
//     * "city": "东莞;深圳",
//     * "enable_signup": false,  //true 正在报名中
//     * "id": 4,
//     * "title": "6.6 莞深单身教师相亲会",
//     * "url": "http://www.dglove.com/console/public-events-detail.jsp?id=4"
//                * },

    @SerializedName("id")
    public int id;

    @SerializedName("title")
    public String title;

    @SerializedName("city")
    public String city;

    @SerializedName("enable_signup")
    public boolean enableSignup;

    @SerializedName("url")
    public String url;

}
