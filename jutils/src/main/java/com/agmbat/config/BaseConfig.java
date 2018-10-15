/**
 * Copyright (C) 2016 mayimchen <mayimchen@gmail.com> All Rights Reserved.
 * <p>
 * jutils
 *
 * @author mayimchen
 * @since 2017-03-12
 */
package com.agmbat.config;

import com.agmbat.file.FileUtils;
import com.agmbat.text.StringParser;
import com.agmbat.text.StringUtils;

import java.io.File;
import java.util.Map;

/**
 * 读取配置文件
 */
public abstract class BaseConfig {

    private Map<String, String> mMap;

    public BaseConfig() {
        mMap = FileUtils.readConfig(getConfigFile());
    }

    protected abstract File getConfigFile();

    protected int getValue(String key, int defaultValue) {
        String value = mMap.get(key);
        int intValue = defaultValue;
        if (!StringUtils.isEmpty(value)) {
            intValue = StringParser.parseInt(value);
            if (intValue < 0) {
                intValue = defaultValue;
            }
        }
        return intValue;
    }

    protected long getValue(String key, long defaultValue) {
        String value = mMap.get(key);
        long longValue = defaultValue;
        if (!StringUtils.isEmpty(value)) {
            longValue = StringParser.parseLong(value);
            if (longValue < 0) {
                longValue = defaultValue;
            }
        }
        return longValue;
    }

    protected String getValue(String key, String defaultValue) {
        String value = mMap.get(key);
        if (StringUtils.isEmpty(value)) {
            value = defaultValue;
        }
        return value;
    }

    protected float getValue(String key, float defaultValue) {
        String value = mMap.get(key);
        float floatValue = defaultValue;
        if (!StringUtils.isEmpty(value)) {
            floatValue = StringParser.parseFloat(value);
            if (floatValue < 0) {
                floatValue = defaultValue;
            }
        }
        return floatValue;
    }

}
