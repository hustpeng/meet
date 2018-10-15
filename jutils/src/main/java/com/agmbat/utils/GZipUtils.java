/**
 * Copyright (C) 2016 mayimchen <mayimchen@gmail.com> All Rights Reserved.
 * <p>
 * jutils
 *
 * @author mayimchen
 * @since 2016-10-07
 */
package com.agmbat.utils;

import com.agmbat.io.IoUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class GZipUtils {

    /**
     * 将数据进行GZip压缩
     *
     * @param content
     * @return
     */
    public static byte[] getGZipData(String content) {
        try {
            return compress(content.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 数据压缩
     *
     * @param data
     * @throws IOException
     * @throws Exception
     */
    public static byte[] compress(byte[] data) {
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        GZIPOutputStream gzipos = null;
        byte[] bytes = null;
        try {
            gzipos = new GZIPOutputStream(baos);
            IoUtils.copyStream(bais, gzipos);
            gzipos.finish();
            bytes = baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IoUtils.closeQuietly(gzipos);
            IoUtils.closeQuietly(bais);
        }
        return bytes;
    }

    public static void decompress(InputStream is, OutputStream os) {
        GZIPInputStream gzipIn = null;
        try {
            gzipIn = new GZIPInputStream(is);
            IoUtils.copyStream(gzipIn, os);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IoUtils.closeQuietly(gzipIn);
        }
    }

    public static byte[] decompress(byte[] data) {
        ByteArrayInputStream in = new ByteArrayInputStream(data);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        decompress(in, out);
        byte[] array = out.toByteArray();
        IoUtils.closeQuietly(out);
        IoUtils.closeQuietly(in);
        return array;
    }
}