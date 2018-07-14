package com.agmbat.text;

import com.agmbat.utils.ArrayUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Tag文本工具类
 */
public class TagText {

    /**
     * 转成tag文本
     *
     * @param tagList
     * @return
     */
    public static String toTagText(List<String> tagList) {
        return ArrayUtils.join(tagList, ",");
    }

    /**
     * 解析tag list
     *
     * @param tagsInfo
     * @return
     */
    public static List<String> parseTagList(String tagsInfo) {
        List<String> list = new ArrayList<>();
        if (!StringUtils.isEmpty(tagsInfo)) {
            String[] array = tagsInfo.split(",");
            for (String item : array) {
                list.add(item);
            }
        }
        return list;
    }

    /**
     * tags string to array
     *
     * @param tagsInfo
     * @return
     */
    public static String[] parseTagArray(String tagsInfo) {
        String[] tags = new String[]{};
        if (!StringUtils.isEmpty(tagsInfo)) {
            tags = ArrayUtils.normalize(tagsInfo.split(","));
        }
        return tags;
    }
}
