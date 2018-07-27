package com.agmbat.imsdk.group;

import android.text.TextUtils;

import com.agmbat.imsdk.search.group.GroupCategory;

import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.provider.IQProvider;
import org.xmlpull.v1.XmlPullParser;

import java.util.ArrayList;
import java.util.List;

public class GroupFormIQProvider implements IQProvider {

    public static String elementName() {
        return "query";
    }

    public static String namespace() {
        return "http://jabber.org/protocol/muc#owner";
    }

    @Override
    public IQ parseIQ(XmlPullParser parser) throws Exception {
        GroupFormReply groupFormReply = new GroupFormReply();

        List<GroupCategory> groupCategories = new ArrayList<>();
        boolean done = false;
        // 判断事件是否到最后
        while(!done)
        {
            // 进入下一个元素并触发相应事件
            int eventType = parser.next();
            switch (eventType) {

                // 判断当前事件是否为标签元素开始事件
                case XmlPullParser.START_TAG:
                    if (parser.getName().equals("field")) {
                        String var = parser.getAttributeValue("", "var");
                        if("muc#circleprofile_circle_name".equals(var)){
                            parser.next();
                            parser.next();
                            groupFormReply.setName(parser.getText());
                        }else if("muc#circleprofile_headline".equals(var)){
                            parser.next();
                            parser.next();
                            groupFormReply.setHeadline(parser.getText());
                        }else if("muc#circleprofile_description".equals(var)){
                            parser.next();
                            parser.next();
                            groupFormReply.setDescription(parser.getText());
                        }else if("muc#circleprofile_cover".equals(var)){
                            parser.next();
                            parser.next();
                            groupFormReply.setAvatar(parser.getText());
                        }else if("muc#circleprofile_is_verify".equals(var)){
                            parser.next();
                            parser.next();
                            groupFormReply.setNeedVerify("1".equals(parser.getText()));
                        }else if("muc#circleprofile_category_id".equals(var)){
                            parser.next();
                            parser.next();
                            String categoryId = parser.getText();
                            if(!TextUtils.isEmpty(categoryId) && TextUtils.isDigitsOnly(categoryId)){
                                groupFormReply.setCategoryId(Integer.parseInt(categoryId));
                            }
                        }
                    }else if(parser.getName().equals("option")) {
                        GroupCategory groupCategory = new GroupCategory();
                        String label = parser.getAttributeValue("", "label");
                        groupCategory.setName(label);
                        parser.next();
                        parser.next();
                        String id = parser.getText();
                        if (!TextUtils.isEmpty(id) && TextUtils.isDigitsOnly(id)) {
                            groupCategory.setId(Integer.parseInt(id));
                        }
                        groupCategories.add(groupCategory);
                    }
                    break;
                // 判断当前事件是否为标签元素结束事件
                case XmlPullParser.END_TAG:
                    if(parser.getName().equals("query")){
                        done = true;
                    }
                    break;
            }
        }
        groupFormReply.setCategories(groupCategories);
        return groupFormReply;
    }
}
