package com.agmbat.debug;

import com.agmbat.text.StringUtils;

import java.util.Objects;

/**
 * Log信息
 */
public class LogInfo {

    /**
     * 文件名
     */
    public String mFileName;

    /**
     * 类名
     */
    public String mClassName;

    /**
     * 方法名
     */
    public String mMethodName;

    /**
     * 实例名称
     */
    public String mInstanceName;

    /**
     * 方法开始时间
     */
    public long mStartTime;

    /**
     * 方法结束时间
     */
    public long mEndTime;

    /**
     * 计算方法执行时间
     *
     * @return
     */
    public long calcCost() {
        return mEndTime - mStartTime;
    }

    public String key() {
        return mFileName + ":" + mClassName + ":" + mInstanceName + ":" + mMethodName;
    }

    @Override
    public String toString() {
        return key();
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o != null) {
            if (o instanceof LogInfo) {
                LogInfo other = (LogInfo) o;
                return Objects.equals(key(), other.key());
            }
        }
        return false;
    }

    /**
     * 打印方法开始时间
     */
    public void printStartTime() {
        StringBuilder builder = new StringBuilder();
        builder.append(getClassSimpleName()).append("::");
        builder.append(mMethodName).append("--");
        builder.append("start");
        if (!StringUtils.isEmpty(mInstanceName)) {
            builder.append(":").append(mInstanceName);
        }
        String msg = builder.toString();
        Debug.print(msg, mStartTime);
    }

    /**
     * 打印方法结束时间
     */
    public void printEndTime() {
        StringBuilder builder = new StringBuilder();
        builder.append(getClassSimpleName()).append("::");
        builder.append(mMethodName).append("--");
        builder.append("end");
        if (!StringUtils.isEmpty(mInstanceName)) {
            builder.append(":").append(mInstanceName);
        }
        builder.append(",cost:").append(calcCost());
        String msg = builder.toString();
        Debug.print(msg, mEndTime);
    }

    /**
     * 获取简单的类名
     *
     * @return
     */
    private String getClassSimpleName() {
        String name = mClassName;
        int dot = name.lastIndexOf('.');
        if (dot != -1) {
            return name.substring(dot + 1);
        }
        return name;
    }
}
