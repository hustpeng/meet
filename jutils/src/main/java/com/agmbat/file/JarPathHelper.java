/**
 * Copyright (C) 2016 mayimchen <mayimchen@gmail.com> All Rights Reserved.
 * <p>
 * jutils
 *
 * @author mayimchen
 * @since 2017-05-01
 */
package com.agmbat.file;

import java.io.File;
import java.net.URL;
import java.net.URLDecoder;

/**
 * 获取jar文件目录工具
 */
public class JarPathHelper {

    public static void printPathInfo() {
        String path = System.getProperty("java.class.path");
        System.out.println("java.class.path:" + path);
        System.out.println("Resource:" + getClassResourcePath());
        System.out.println("Project path:" + getProjectPath());
        System.out.println("App path:" + getAppPath());
        System.out.println("Jar parent:" + getJarParentFile());
    }


    /**
     * 获取jar运行时所在目录
     *
     * @return
     */
    public static File getJarParentFile() {
        String resourcePath = getClassResourcePath();
        String appPath = getAppPath();
        if (resourcePath.startsWith("jar:")) {
            // 运行在jar中
            String flagText = ".jar!";
            int index = appPath.indexOf(flagText);
            if (index != -1) {
                appPath = appPath.substring(0, index + flagText.length() - 1);
            }
            // if (appPath.endsWith("!/BOOT-INF")) {
            // appPath = appPath.substring(0, appPath.lastIndexOf("!/BOOT-INF"));
            // }
        }
        return new File(appPath).getParentFile();
    }

    public static String getProjectPath() {
        URL url = JarPathHelper.class.getProtectionDomain().getCodeSource().getLocation();
        String filePath = null;
        try {
            filePath = URLDecoder.decode(url.getPath(), "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (filePath.endsWith(".jar")) {
            filePath = filePath.substring(0, filePath.lastIndexOf("/") + 1);
        }
        File file = new File(filePath);
        filePath = file.getAbsolutePath();
        return filePath;
    }

    public static String getAppPath() {
        return getAppPath(JarPathHelper.class);
    }

    public static String getAppPath(Class<?> cls) {
        // 检查用户传入的参数是否为空
        if (cls == null) {
            throw new IllegalArgumentException("参数不能为空！");
        }
        ClassLoader loader = cls.getClassLoader();
        // 获得类的全名，包括包名
        String clsName = cls.getName();
        // 此处简单判定是否是Java基础类库，防止用户传入JDK内置的类库
        if (clsName.startsWith("java.") || clsName.startsWith("javax.")) {
            throw new IllegalArgumentException("不要传送系统类！");
        }
        // 将类的class文件全名改为路径形式
        String clsPath = clsName.replace(".", "/") + ".class";

        // 调用ClassLoader的getResource方法，传入包含路径信息的类文件名
        URL url = loader.getResource(clsPath);
        // 从URL对象中获取路径信息
        String realPath = url.getPath();
        // 去掉路径信息中的协议名"file:"
        int pos = realPath.indexOf("file:");
        if (pos > -1) {
            realPath = realPath.substring(pos + 5);
        }
        // 去掉路径信息最后包含类文件信息的部分，得到类所在的路径
        pos = realPath.indexOf(clsPath);
        realPath = realPath.substring(0, pos - 1);
        // 如果类文件被打包到JAR等文件中时，去掉对应的JAR等打包文件名
        if (realPath.endsWith("!")) {
            realPath = realPath.substring(0, realPath.lastIndexOf("/"));
        }
        File file = new File(realPath);
        realPath = file.getAbsolutePath();
        try {
            realPath = URLDecoder.decode(realPath, "utf-8");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return realPath;
    }

    /**
     * 获取app module path, 针对idea输出module的路径名称
     *
     * @return
     */
    public static String getAppModulePath(Class<?> cls) {
        String appPath = getAppPath(cls);
        String ideaBuildFlag = "/build/classes/main";
        String ideaBuildFlag2 = "/out/production/classes";
        if (appPath.endsWith(ideaBuildFlag)) {
            return appPath.substring(0, appPath.length() - ideaBuildFlag.length());
        }
        if (appPath.endsWith(ideaBuildFlag2)) {
            return appPath.substring(0, appPath.length() - ideaBuildFlag2.length());
        }
        return appPath;
    }

    /**
     * 获取类资源路径
     *
     * @return
     */
    private static String getClassResourcePath() {
        String resourceName = JarPathHelper.class.getSimpleName() + ".class";
        String resourcePath = JarPathHelper.class.getResource(resourceName).toString();
        return resourcePath;
    }
}