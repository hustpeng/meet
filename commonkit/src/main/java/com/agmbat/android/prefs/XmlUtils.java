package com.agmbat.android.prefs;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import com.agmbat.utils.ReflectionUtils;

/**
 * 反射调用隐藏方法
 * com.android.internal.util.XmlUtils
 */
public class XmlUtils {

    /**
     * Read a HashMap from an InputStream containing XML. The stream can
     * previously have been written by writeMapXml().
     *
     * @param in The InputStream from which to read.
     * @return HashMap The resulting map.
     */
    public static final HashMap readMapXml(InputStream in) {
        String className = "com.android.internal.util.XmlUtils";
        String methodName = "readMapXml";
        Class[] type = new Class[] {InputStream.class};
        Object[] parameters = new Object[] {in};
        return (HashMap) ReflectionUtils.invokeStaticMethod(className, methodName, type, parameters);
    }

    /**
     * Flatten a Map into an output stream as XML. The map can later be read
     * back with readMapXml().
     *
     * @param val The map to be flattened.
     * @param out Where to write the XML data.
     */
    public static final void writeMapXml(Map val, OutputStream out) {
        String className = "com.android.internal.util.XmlUtils";
        String methodName = "writeMapXml";
        Class[] type = new Class[] {Map.class, OutputStream.class};
        Object[] parameters = new Object[] {val, out};
        ReflectionUtils.invokeStaticMethod(className, methodName, type, parameters);
    }

}