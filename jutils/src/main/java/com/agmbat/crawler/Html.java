package com.agmbat.crawler;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Html {

    /**
     * 支持过虑器
     *
     * @param result
     * @param filter
     * @return
     */
    public static List<String> parseUrlList(String result, Filter filter) {
        List<String> outputList = new ArrayList<>();
        List<String> urlList = findUrl(result);
        for (String url : urlList) {
            if (filter == null || filter.accept(url)) {
                outputList.add(url);
            }
        }
        return outputList;
    }

    /**
     * 查找网页中所有url
     *
     * @param text
     * @return
     */
    public static List<String> findUrl(String text) {
        List<String> urlList = new ArrayList<>();
        String regex = "<a href=\"(http.*?)\"";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            String url = matcher.group(1);
            urlList.add(url);
        }
        return urlList;
    }

}
