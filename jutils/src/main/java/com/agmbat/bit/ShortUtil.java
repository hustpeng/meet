/**
 * Copyright (C) 2016 mayimchen <mayimchen@gmail.com> All Rights Reserved.
 * <p>
 * jutils
 *
 * @author mayimchen
 * @since 2016-10-07
 */
package com.agmbat.bit;

import java.nio.ByteOrder;

/**
 * short数据类型与byte类型转换
 */
public class ShortUtil {

    public static final boolean IS_BIG_ENDING = ByteOrder.nativeOrder() == ByteOrder.BIG_ENDIAN;

    public static byte[] averageShortByteArray(byte firstShortHighByte, byte firstShortLowByte,
                                               byte secondShortHighByte, byte secondShortLowByte) {
        short firstShort = getShort(firstShortHighByte, firstShortLowByte);
        short secondShort = getShort(secondShortHighByte, secondShortLowByte);
        return getBytes((short) (firstShort / 2 + secondShort / 2));
    }

    public static short averageShort(byte firstShortHighByte, byte firstShortLowByte, byte secondShortHighByte,
                                     byte secondShortLowByte) {
        short firstShort = getShort(firstShortHighByte, firstShortLowByte);
        short secondShort = getShort(secondShortHighByte, secondShortLowByte);
        return (short) (firstShort / 2 + secondShort / 2);
    }

    public static short weightShort(byte firstShortHighByte, byte firstShortLowByte, byte secondShortHighByte,
                                    byte secondShortLowByte, float firstWeight, float secondWeight) {
        short firstShort = getShort(firstShortHighByte, firstShortLowByte);
        short secondShort = getShort(secondShortHighByte, secondShortLowByte);
        return (short) (firstShort * firstWeight + secondShort * secondWeight);
    }

    public static byte[] getByteBuffer(short[] data) {
        return getByteBuffer(data, data.length);
    }

    public static byte[] getByteBuffer(short[] data, int length) {
        if (length > data.length) {
            length = data.length;
        }
        byte[] array = new byte[2 * length];
        for (int i = 0; i < length; i++) {
            short value = data[i];
            if (IS_BIG_ENDING) {
                array[i * 2 + 1] = (byte) (value & 0x00ff);
                value >>= 8;
                array[i * 2] = (byte) (value & 0x00ff);
            } else {
                array[i * 2] = (byte) (value & 0x00ff);
                value >>= 8;
                array[i * 2 + 1] = (byte) (value & 0x00ff);
            }
        }
        return array;
    }

    public static byte[] getBytes(short value) {
        byte[] data = new byte[2];
        if (IS_BIG_ENDING) {
            data[1] = (byte) (value & 0x00ff);
            value >>= 8;
            data[0] = (byte) (value & 0x00ff);
        } else {
            data[0] = (byte) (value & 0x00ff);
            value >>= 8;
            data[1] = (byte) (value & 0x00ff);
        }
        return data;
    }

    public static short getShort(byte firstByte, byte secondByte) {
        short value = 0;
        if (IS_BIG_ENDING) {
            value |= (firstByte & 0x00ff);
            value <<= 8;
            value |= (secondByte & 0x00ff);
        } else {
            value |= (secondByte & 0x00ff);
            value <<= 8;
            value |= (firstByte & 0x00ff);
        }
        return value;
    }
}