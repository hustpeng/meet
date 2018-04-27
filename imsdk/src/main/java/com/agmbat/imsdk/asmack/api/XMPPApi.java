package com.agmbat.imsdk.asmack.api;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 实现异步数据获取, 用于同步交互线程所需要的信息
 */
public class XMPPApi {

    private static final ExecutorService SINGLE_THREAD_EXECUTOR = Executors.newSingleThreadExecutor();

    public void fetchContactInfo(final String contactJid, OnFetchContactListener listener) {
        Runnable runnable = new FetchContactInfoRunnable(contactJid, listener);
        SINGLE_THREAD_EXECUTOR.execute(runnable);
    }


}

