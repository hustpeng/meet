package com.agmbat.utils;

import java.util.Random;

/**
 * 产生随机数的工具类
 */
public class RandomUtils {

    /**
     * 随机算法, 将随机数分配在[min, max]区间, 平均值为avg
     *
     * @param min 最小值
     * @param max 最大值
     * @param avg 平均值
     * @return
     */
    public static int random(int min, int max, int avg) {
        // 平均值一定大于最小值,小于最大值
        // 将数分为两个区间 [min, avg] , [avg, max]
        // 让随机数分配在 [min, avg] 区间内出现的次数高，在 [avg, max] 区间出现的次数低，这样便能拉低平均值
        // 设在 [min, avg] 区间的概率为x，那么 [avg, max] 区间的概率为1-x，（小区间均值*概率）+（大区间均值*概率）= 总均值
        // (min + avg) / 2 * x + (avg + max) / 2 * (1 - x) = avg
        // 计算出 x = (max - avg) / (max - min)
        float x = (max - avg) * 1.0f / (max - min);
        Random random = new Random();
        float randomNumber = random.nextFloat();
        int result;
        if (randomNumber < x) {
            result = min + random.nextInt(avg - min);
        } else {
            result = avg + random.nextInt(max - avg);
        }
        return result;
    }

    /**
     * 随机数字字符串
     *
     * @param length 指定长度的数字串
     * @return
     */
    public static String randomNumber(int length) {
        Random random = new Random();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            builder.append(String.valueOf(random.nextInt(10)));
        }
        return builder.toString();
    }
}
