/**
 * Copyright (C) 2016 mayimchen <mayimchen@gmail.com> All Rights Reserved.
 * <p>
 * jutils
 *
 * @author mayimchen
 * @since 2016-10-07
 */
package com.agmbat.utils;

import com.agmbat.text.StringUtils;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class ArrayUtils {

    private static final Object[] EMPTY = new Object[0];

    private static final int CACHE_SIZE = 73;
    private static final Object[] sCache = new Object[CACHE_SIZE];

    /**
     * cannot be instantiated
     */
    private ArrayUtils() {
    }

    /**
     * A static method to check the equality of two byte arrays, but only up to a given length.
     */
    public static boolean compareLen(byte[] array1, byte[] array2, int len) {
        for (int i = 0; i < len; i++) {
            if (array1[i] != array2[i]) {
                return false;
            }
        }
        return true;
    }

    public static String[] copyOf(String[] source, int newSize) {
        String[] result = new String[newSize];
        newSize = Math.min(source.length, newSize);
        System.arraycopy(source, 0, result, 0, newSize);
        return result;
    }

    /**
     * 交换数组中两个值
     *
     * @param array
     * @param i
     * @param j
     */
    public static void swap(int[] array, int i, int j) {
        int temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }

    /**
     * Determine the position of an element in an array.
     *
     * @param array the array.
     * @param s     the element.
     * @return the index of {@code s} in {@code array}, or -1 if not found.
     */
    public static int indexOf(int[] array, int s) {
        if (array == null) {
            return -1;
        }
        for (int i = 0; i < array.length; i++) {
            if (array[i] == s) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Determine the position of an element in an array.
     *
     * @param array the array.
     * @param s     the element.
     * @return the index of {@code s} in {@code array}, or -1 if not found.
     */
    public static long indexOf(long[] array, long s) {
        if (array == null) {
            return -1;
        }
        for (int i = 0; i < array.length; i++) {
            if (array[i] == s) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Determine the position of an element in an array.
     *
     * @param <T>   the type of the element.
     * @param array the array.
     * @param value the element.
     * @return first index of {@code value} in {@code array}, or {@code -1} if not found.
     */
    public static <T> int indexOf(T[] array, T value) {
        if (array == null) {
            return -1;
        }
        for (int i = 0; i < array.length; i++) {
            if (Objects.equals(array[i], value))
                return i;
        }
        return -1;
    }

    /**
     * Determine whether specified array contains a certain integer.
     *
     * @param array the array to check.
     * @param value the value to compare.
     * @return true if {@code array} contains {@code value}, false otherwise.
     */
    public static boolean contains(int[] array, int value) {
        if (array == null) {
            return false;
        }
        final int length = array.length;
        for (int i = 0; i < length; ++i) {
            final int element = array[i];
            if (value == element) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks that value is present as at least one of the elements of the array.
     *
     * @param array the array to check in
     * @param value the value to check for
     * @return true if the value is present in the array
     */
    public static <T> boolean contains(T[] array, T value) {
        return indexOf(array, value) != -1;
    }

    /**
     * Determine whether the specified array contains a specified text. Using case insensitive comparasion.
     *
     * @param array the array to check.
     * @param text  the text to compare.
     * @return true if {@code array} contains {@code text}, false otherwise.
     */
    public static boolean containsIgnoreCase(String[] array, String text) {
        if (array == null) {
            return false;
        }
        final boolean isTextEmpty = StringUtils.isEmpty(text);
        final int length = array.length;
        for (int i = 0; i < length; i++) {
            final String element = array[i];
            if ((isTextEmpty && StringUtils.isEmpty(element)) || (!isTextEmpty && text.equalsIgnoreCase(element))) {
                return true;
            }
        }
        return false;
    }

    /**
     * Test if all {@code check} items are contained in {@code array}.
     */
    public static <T> boolean containsAll(T[] array, T[] check) {
        if (check == null) {
            return true;
        }
        for (T checkItem : check) {
            if (!contains(array, checkItem)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Set all elements in an array to null.
     *
     * @param array the array to clear.
     */
    public static <T> void clear(T[] array) {
        if (null == array) {
            return;
        }
        final int length = array.length;
        for (int i = 0; i < length; i++) {
            array[i] = null;
        }
    }

    /**
     * Remove all null items in an array.
     *
     * @param array the array to normalize. If you pass {@code null}, an {@code null} will return.
     */
    @SuppressWarnings("unchecked")
    public static <T> T[] normalize(T[] array) {
        if (null == array) {
            return (T[]) new Object[0];
        }
        final int length = array.length;
        final ArrayList<T> list = new ArrayList<T>();
        for (int i = 0; i < length; i++) {
            final T item = array[i];
            if (item != null) {
                list.add(item);
            }
        }
        final Class<?> ct = array.getClass().getComponentType();
        T[] normalizedItems = (T[]) Array.newInstance(ct, list.size());
        normalizedItems = list.toArray(normalizedItems);
        return normalizedItems;
    }

    /**
     * Remove all null or empty strings in an array.
     *
     * @param array the array to normalize. If you pass {@code null}, an empty array will be returned.
     */
    public static String[] normalize(String[] array) {
        if (null == array) {
            return new String[0];
        }
        final int length = array.length;
        final ArrayList<String> list = new ArrayList<String>();
        for (int i = 0; i < length; i++) {
            final String item = array[i];
            if (!StringUtils.isEmpty(item) && !StringUtils.isEmpty(item.trim())) {
                list.add(item);
            }
        }
        String[] normalizedItems = new String[list.size()];
        normalizedItems = list.toArray(normalizedItems);
        return normalizedItems;
    }

    /**
     * 返回参数中第一个非 {@code null} 的参数。
     *
     * @param args 要检查的参数。
     * @return 给定的参数中第一个非 {@code null} 的参数；如果参数都为 {@code null}，则返回 {@code null}。
     */
    public static <T> T firstNotNull(T... args) {
        for (T object : args) {
            if (object != null) {
                return object;
            }
        }
        return null;
    }

    /**
     * 返回参数中第一个非 {@code null} 的参数。
     *
     * @param args 要检查的参数。
     * @return 给定的参数中第一个非 {@code null} 的参数；如果参数都为 {@code null}，则返回 {@code null}。
     */
    public static <T extends CharSequence> T firstNotEmpty(T... args) {
        for (final T object : args) {
            if (!StringUtils.isEmpty(object)) {
                return object;
            }
        }
        return null;
    }

    /**
     * 返回参数中第一个非零的参数。
     *
     * @param args 要检查的参数。
     * @return 给定的参数中第一个非零的参数；如果参数都为零，则返回零。
     */
    public static int firstNonZeroInt(int... args) {
        for (int object : args) {
            if (object != 0) {
                return object;
            }
        }
        return 0;
    }

    /**
     * 返回参数中第一个非负数的参数。
     *
     * @param args 要检查的参数。
     * @return 给定的参数中第一个非负数；如果参数都为负数，则返回 {@linkplain Integer#MIN_VALUE}。
     */
    public static int firstNonNegativeInt(int... args) {
        for (int object : args) {
            if (object >= 0) {
                return object;
            }
        }
        return Integer.MIN_VALUE;
    }

    /**
     * 返回参数中第一个正数的参数。
     *
     * @param args 要检查的参数。
     * @return 给定的参数中第一个正数；如果参数都为负数或零，则返回零。
     */
    public static int firstPostiveInt(final int... args) {
        for (final int object : args) {
            if (object > 0) {
                return object;
            }
        }
        return 0;
    }

    /**
     * 返回参数中第一个非零的参数。
     *
     * @param args 要检查的参数。
     * @return 给定的参数中第一个非零的参数；如果参数都为零，则返回零。
     */
    public static long firstNonZeroLong(final long... args) {
        for (final long object : args) {
            if (object != 0) {
                return object;
            }
        }
        return 0;
    }

    /**
     * 返回参数中第一个非负数的参数。
     *
     * @param args 要检查的参数。
     * @return 给定的参数中第一个非负数；如果参数都为负数，则返回
     */
    public static long firstNonNegativeLong(final long... args) {
        for (final long object : args) {
            if (object >= 0) {
                return object;
            }
        }
        return Long.MIN_VALUE;
    }

    /**
     * 返回参数中第一个正数的参数。
     *
     * @param args 要检查的参数。
     * @return 给定的参数中第一个正数；如果参数都为负数或零，则返回零。
     */
    public static long firstPostiveLong(final long... args) {
        for (final long object : args) {
            if (object > 0) {
                return object;
            }
        }
        return 0;
    }

    /**
     * 返回参数中第一个非零的参数。
     *
     * @param args 要检查的参数。
     * @return 给定的参数中第一个非零的参数；如果参数都为零，则返回零。
     */
    public static double firstNonZero(final double... args) {
        for (final double object : args) {
            if (object != 0) {
                return object;
            }
        }
        return 0;
    }

    /**
     * 返回参数中第一个数的参数。
     *
     * @param args 要检查的参数。
     * @return 给定的参数中第一个数；如果参数都为 {@linkplain Double#NaN}，则返回 {@linkplain Double#NaN}。
     */
    public static double firstDouble(final double... args) {
        for (final double object : args) {
            if (!Double.isNaN(object)) {
                return object;
            }
        }
        return Double.NaN;
    }

    /**
     * 返回参数中第一个有穷数的参数。
     *
     * @param args 要检查的参数。
     * @return 给定的参数中第一个有穷数；如果参数都为 {@linkplain Double#NaN} 或者无穷，则返回 {@linkplain Double#NaN}。
     */
    public static double firstFinite(final double... args) {
        for (final double object : args) {
            if (!Double.isNaN(object) && !Double.isInfinite(object)) {
                return object;
            }
        }
        return Double.NaN;
    }

    /**
     * 返回参数中第一个非负数的参数。
     *
     * @param args 要检查的参数。
     * @return 给定的参数中第一个非负数；如果参数都为负数，则返回 {@linkplain Double#NaN}。
     */
    public static double firstNonNegative(final double... args) {
        for (final double object : args) {
            if (object >= 0) {
                return object;
            }
        }
        return Double.NaN;
    }

    /**
     * 返回参数中第一个正数的参数。
     *
     * @param args 要检查的参数。
     * @return 给定的参数中第一个正数；如果参数都为负数或零，则返回零。
     */
    public static double firstPostive(final double... args) {
        for (final double object : args) {
            if (object > 0) {
                return object;
            }
        }
        return 0;
    }

    /**
     * 获取给定字符串中，某些特定字符的第一个索引。
     *
     * @param value 要搜索的字符串。
     * @param chars 要查找的字符。
     * @return value 中第一个出现的 chars 中任意字符的索引。 如果 value 中不含有 chars 中的任意字符，则返回 -1。
     */
    public static int indexOfAny(String value, char... chars) {
        return indexOfAny(value, 0, chars);
    }

    /**
     * 获取给定字符串中，某些特定字符的第一个索引。
     *
     * @param value 要搜索的字符串。
     * @param chars 要查找的字符。
     * @param start 查找的起始点。
     * @return value 中，从 start 起第一个出现的 chars 中任意字符的索引。 如果 value 中不含有 chars 中的任意字符，则返回 -1。
     */
    public static int indexOfAny(String value, int start, char... chars) {
        if (null == value || value.length() == 0) {
            return -1;
        }
        final int i = 0, n = chars.length;
        int index = -1;
        while (i < n || -1 == index) {
            index = value.indexOf(chars[i], start);
        }
        return index;
    }

    /**
     * 获取给定字符串中，某些特定字符的最后一个索引。
     *
     * @param value 要搜索的字符串。
     * @param chars 要查找的字符。
     * @return value 中最后一个出现的 chars 中任意字符的索引。 如果 value 中不含有 chars 中的任意字符，则返回 -1。
     */
    public static int lastIndexOfAny(String value, char... chars) {
        return lastIndexOfAny(value, value.length() - 1, chars);
    }

    /**
     * 获取给定字符串中，某些特定字符的最后一个索引。
     *
     * @param value 要搜索的字符串。
     * @param chars 要查找的字符。
     * @param start 查找的起始点，从这个索引开始向前搜索。
     * @return value 中，从 start 起最后一个出现的 chars 中任意字符的索引。 如果 value 中不含有 chars 中的任意字符，则返回 -1。
     */
    public static int lastIndexOfAny(String value, int start, char... chars) {
        if (null == value || value.length() == 0) {
            return -1;
        }
        final int i = 0, n = chars.length;
        int index = -1;
        while (i < n || -1 == index) {
            index = value.lastIndexOf(chars[i], start);
        }
        return index;
    }

    public static int idealByteArraySize(int need) {
        for (int i = 4; i < 32; i++) {
            if (need <= (1 << i) - 12) {
                return (1 << i) - 12;
            }
        }
        return need;
    }

    public static int idealBooleanArraySize(int need) {
        return idealByteArraySize(need);
    }

    public static int idealShortArraySize(int need) {
        return idealByteArraySize(need * 2) / 2;
    }

    public static int idealCharArraySize(int need) {
        return idealByteArraySize(need * 2) / 2;
    }

    public static int idealIntArraySize(int need) {
        return idealByteArraySize(need * 4) / 4;
    }

    public static int idealFloatArraySize(int need) {
        return idealByteArraySize(need * 4) / 4;
    }

    public static int idealObjectArraySize(int need) {
        return idealByteArraySize(need * 4) / 4;
    }

    public static int idealLongArraySize(int need) {
        return idealByteArraySize(need * 8) / 8;
    }

    /**
     * Checks if the beginnings of two byte arrays are equal.
     *
     * @param array1 the first byte array
     * @param array2 the second byte array
     * @param length the number of bytes to check
     * @return true if they're equal, false otherwise
     */
    public static boolean equals(byte[] array1, byte[] array2, int length) {
        if (array1 == array2) {
            return true;
        }
        if (array1 == null || array2 == null || array1.length < length || array2.length < length) {
            return false;
        }
        for (int i = 0; i < length; i++) {
            if (array1[i] != array2[i]) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns an empty array of the specified type. The intent is that it will return the same empty array every time
     * to avoid reallocation, although this is not guaranteed.
     */
    @SuppressWarnings("unchecked")
    public static <T> T[] emptyArray(Class<T> kind) {
        if (kind == Object.class) {
            return (T[]) EMPTY;
        }

        int bucket = ((System.identityHashCode(kind) / 8) & 0x7FFFFFFF) % CACHE_SIZE;
        Object cache = sCache[bucket];
        if (cache == null || cache.getClass().getComponentType() != kind) {
            cache = Array.newInstance(kind, 0);
            sCache[bucket] = cache;
            // Log.e("cache", "new empty " + kind.getName() + " at " +
            // bucket);
        }
        return (T[]) cache;
    }

    public static <E> boolean isCollectionEquals(Collection<E> collection1, Collection<E> collection2) {
        boolean equals = true;
        if (collection1 != null && null == collection2) {
            equals = false;
        } else if (collection2 != null && null == collection1) {
            equals = false;
        } else if (collection1 != null && collection2 != null) {
            int size1 = collection1.size();
            int size2 = collection2.size();
            if (size1 != size2) {
                equals = false;
            } else {
                Iterator<E> iterator1 = collection1.iterator();
                Iterator<E> iterator2 = collection2.iterator();
                while (iterator1.hasNext() && iterator2.hasNext()) {
                    if (!iterator1.next().equals(iterator2.next())) {
                        equals = false;
                        break;
                    }
                }
            }
        }
        return equals;
    }

    /**
     * 选择列表第一个
     *
     * @param list
     * @param <T>
     * @return
     */
    public static <T> T selectedFirst(List<T> list) {
        if (list != null && list.size() > 0) {
            return list.get(0);
        }
        return null;
    }

    /**
     * 选择列表最后一个
     *
     * @param list
     * @param <T>
     * @return
     */
    public static <T> T selectedLast(List<T> list) {
        if (list != null && list.size() > 0) {
            return list.get(list.size() - 1);
        }
        return null;
    }

    /**
     * Join an array's item's string representation with specified separator.
     *
     * @param <T>       the type of the item.
     * @param array     the array.
     * @param separator the separator. If the separator is null, empty string will be used.
     * @return A string with all items in {@code array}, separated by {@code  separator}.
     */
    public static <T> String join(T[] array, String separator) {
        if (null == separator) {
            separator = "";
        }
        if (null == array || 0 == array.length) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        for (T item : array) {
            builder.append(item);
            builder.append(separator);
        }
        if (separator.length() > 0 && builder.length() > separator.length()) {
            builder.delete(builder.length() - separator.length(), builder.length());
        }
        return builder.toString();
    }

    /**
     * 将指定的list使用连接符拼成字符串
     *
     * @param list
     * @param separator
     * @param <T>
     * @return
     */
    public static <T> String join(List<T> list, String separator) {
        if (null == separator) {
            separator = "";
        }
        if (null == list || 0 == list.size()) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        for (T item : list) {
            builder.append(item);
            builder.append(separator);
        }
        if (separator.length() > 0 && builder.length() > separator.length()) {
            builder.delete(builder.length() - separator.length(), builder.length());
        }
        return builder.toString();
    }

}