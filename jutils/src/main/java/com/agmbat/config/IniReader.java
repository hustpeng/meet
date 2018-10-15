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
import com.agmbat.io.IoUtils;
import com.agmbat.text.StringUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * ini配置文件读取
 */
public class IniReader {

    private Map<String, Properties> mSections;

    private Properties mCurrentProperties;

    public IniReader(String path) {
        mSections = new HashMap<String, Properties>();
        parseFile(path);
    }

    public IniReader(InputStream in) {
        mSections = new HashMap<String, Properties>();
        parseStream(in);
    }

    private void parseFile(String path) {
        if (FileUtils.existsFile(path)) {
            FileInputStream fin = null;
            try {
                fin = new FileInputStream(new File(path));
                parseStream(fin);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } finally {
                IoUtils.closeQuietly(fin);
            }
        }
    }

    private void parseStream(InputStream in) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        try {
            read(reader);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IoUtils.closeQuietly(reader);
        }
    }

    public String getValue(String section, String name) {
        Properties p = mSections.get(section);
        if (p == null) {
            return null;
        }
        return p.getProperty(name);
    }

    private void read(BufferedReader reader) throws IOException {
        String line;
        while ((line = reader.readLine()) != null) {
            parseLine(line);
        }
    }

    private void parseLine(String line) {
        line = line.trim();
        if (StringUtils.isEmpty(line)) {
            return;
        }
        final Pattern pattern = Pattern.compile("\\[(.*)\\]");
        Matcher matcher = pattern.matcher(line);
        if (matcher.matches()) {
            String section = matcher.group(1);
            mCurrentProperties = new Properties();
            mSections.put(section, mCurrentProperties);
        } else {
            if (mCurrentProperties != null) {
                String[] array = line.split("=", 2);
                if (array.length > 1) {
                    mCurrentProperties.setProperty(array[0], array[1]);
                }
            }
        }
    }

}
