package com.agmbat.android.media;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

import com.agmbat.file.FileUtils;
import com.agmbat.io.IoUtils;

public class AmrHelper {

    private static final int AMR_HEAD_LENGTH = 6;

    // 合并两段录音
    public static void mergeAmrFile(String path1, String path2) {
        FileOutputStream fos = null;
        FileInputStream fis = null;
        try {
            fos = new FileOutputStream(path1, true);
            fis = new FileInputStream(path2);
            if (fis.available() > AMR_HEAD_LENGTH) {
                fis.skip(AMR_HEAD_LENGTH);
                IoUtils.copyStream(fis, fos);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IoUtils.closeQuietly(fis);
            IoUtils.closeQuietly(fos);
        }
        FileUtils.deleteFileIfExist(path2);
    }

    // 得到amr的时长
    public static long getAmrDuration(File file) {
        long duration = -1;
        int[] packedSize = { 12, 13, 15, 17, 19, 20, 26, 31, 5, 0, 0, 0, 0, 0, 0, 0 };
        RandomAccessFile raf = null;
        try {
            raf = new RandomAccessFile(file, "rw");
            long length = file.length();// 文件的长度
            int pos = AMR_HEAD_LENGTH;// 设置初始位置
            int frameCount = 0;// 初始帧数
            int packedPos = -1;
            byte[] datas = new byte[1];// 初始数据值
            while (pos <= length) {
                raf.seek(pos);
                if (raf.read(datas, 0, 1) != 1) {
                    duration = length > 0 ? ((length - 6) / 650) : 0;
                    break;
                }
                packedPos = (datas[0] >> 3) & 0x0F;
                pos += packedSize[packedPos] + 1;
                frameCount++;
            }
            duration += frameCount * 20;// 帧数*20
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IoUtils.closeQuietly(raf);
        }
        return duration + 1000;
    }
}
