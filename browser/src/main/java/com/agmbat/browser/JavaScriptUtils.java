package com.agmbat.browser;

import android.os.Build;
import android.webkit.WebView;

/**
 * android 与 js 调用工具类
 *
 * @author mayimchen
 */
public class JavaScriptUtils {

    /**
     * 执行js代码
     *
     * @param webView
     * @param script
     */
    public static void evaluateJavascript(WebView webView, String script) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            webView.loadUrl("javascript:" + script);
        } else {
            webView.evaluateJavascript(script, null);
        }
    }

    /**
     * 调用js方法
     *
     * @param webView
     * @param methodName
     * @param args
     */
    public static void callJavaScript(WebView webView, String methodName, String... args) {
        String script = buildJavaScript(methodName, args);
        webView.loadUrl(script);
    }

    /**
     * 构建js函数调用
     *
     * @param methodName
     * @param args
     * @return
     */
    public static String buildJavaScript(String methodName, String... args) {
        StringBuilder sb = new StringBuilder();
        sb.append("javascript:");
        sb.append(methodName);
        sb.append('(');
        if (args.length > 0) {
            for (int i = 0; i < args.length - 1; i++) {
                sb.append('\'');
                sb.append(args[i]);
                sb.append('\'');
                sb.append(',');
            }
            sb.append('\'');
            sb.append(args[args.length - 1]);
            sb.append('\'');
        }
        sb.append(");");
        return sb.toString();
    }

}
