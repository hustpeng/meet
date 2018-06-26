# To enable ProGuard in your project, edit project.properties
# to define the proguard.config property as described in that file.
#
# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in ${sdk.dir}/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the ProGuard
# include property in project.properties.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

-optimizationpasses 5
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontpreverify
-verbose
-ignorewarnings
-dontoptimize
#-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*

-keepattributes Signature
-keepattributes *Annotation*

##第一句的意思是隐藏源文件的名字，并且在堆栈信息中源文件名字被替换为SourceFile这个名字，当然SourceFile也可以使用别的字符串比如a
##第二句是堆栈信息中保留行号和SourceFile名字
# 将.class信息中的类名重新定义为"Proguard"字符串
-renamesourcefileattribute Proguard

# 并保留源文件名为"Proguard"字符串，而非原始的类名 并保留行号
-keepattributes SourceFile,LineNumberTable

-keepattributes JavascriptInterface

-dontwarn com.tencent.mm.**
-keep class com.tencent.mm.sdk.openapi.WXMediaMessage {*;}
-keep class com.tencent.mm.sdk.openapi.** implements com.tencent.mm.sdk.openapi.WXMediaMessage$IMediaObject {*;}

# 不混淆微信sdk
-dontwarn    com.tencent.mm.*
-keep class  com.tencent.mm.** { *;}


-keepclasseswithmembernames class * {
    native <methods>;
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

-keepclassmembers class * extends android.app.Activity {
   public void *(android.view.View);
}

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}


# 不混淆org.apache.http
-dontwarn    org.apache.http.*
-keep class  org.apache.http.** { *;}

# eventbus

-keepclassmembers class ** {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }

# Only required if you use AsyncExecutor
-keepclassmembers class * extends org.greenrobot.eventbus.util.ThrowableFailureEvent {
    <init>(java.lang.Throwable);
}

# okhttp
-dontwarn okhttp3.**
-keep class okhttp3.** { *; }

# rebound
-dontwarn com.facebook.rebound.**
-keep class com.facebook.rebound.** { *; }


# dom4j
-keep class org.dom4j.** { *; }

-keep class de.measite.smack.AndroidDebugger { *; }

-keep class com.agmbat.meetyou.group.CircleGroup { *; }
-keep class com.agmbat.imsdk.asmack.roster.ContactGroup { *; }
-keep class com.agmbat.imsdk.asmack.roster.ContactInfo { *; }
-keep class com.agmbat.imsdk.asmack.roster.FriendRequest { *; }


# 百度地图
-keep class com.baidu.** {*;}
-keep class mapsdkvi.com.** {*;}