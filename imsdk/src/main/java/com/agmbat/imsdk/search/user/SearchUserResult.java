package com.agmbat.imsdk.search.user;

import com.agmbat.imsdk.api.ApiResult;
import com.agmbat.imsdk.asmack.roster.ContactInfo;

/**
 * {
 * "result":true, // true  API调用成功，否则调用失败
 * "resp":  // 若无resp节点，表示未找到该用户
 * {
 * "age": 22,
 * "auth_status": 0,    //1 为认证会员，0为未认证会员
 * "avatar_url": "http://p6bt95t1n.bkt.clouddn.com/9974f25e1f43ff01fa3a95dc02032d2179963.jpg",
 * "birth": 1996,
 * "birthplace": "湖北省宜昌市",
 * "car": 1,
 * "career": "空乘",
 * "create_time": 1521703218000,
 * "demand": "漂亮妹纸",
 * "education": 4,
 * "gender": 0,
 * "height": 169,
 * "hobby": "徒步,自驾游,音乐,书法,西餐",
 * "house": 0,
 * "im_uid": 1061,
 * "industry": "服务业",
 * "introduce": "一个好人",
 * "jid": "15571767415@yuan520.com",
 * "last_login": 1521703240000,
 * "marriage": 0,
 * "nickname": "好名字",
 * "residence": "湖北省宜昌市",
 * "status": "好人一枚",
 * "wage": 20000,
 * "weight": 52,
 * "workarea": "湖北省宜昌市"
 * }
 * }
 */
public class SearchUserResult extends ApiResult<ContactInfo> {
}
