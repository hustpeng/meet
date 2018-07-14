package com.agmbat.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * 使用Gson序列化对象的辅助类
 */
public class GsonHelper {

    /**
     * 整型数据序列化处理
     */
    public static final TypeAdapter<Number> INTEGER = new TypeAdapter<Number>() {

        @Override
        public Number read(JsonReader in) throws IOException {
            if (in.peek() == JsonToken.NULL) {
                in.nextNull();
                return null;
            }
            try {
                String result = in.nextString();
                if ("".equals(result)) {
                    return -1;
                }
                return Integer.parseInt(result);
            } catch (NumberFormatException e) {
                throw new JsonSyntaxException(e);
            }
        }

        @Override
        public void write(JsonWriter out, Number value) throws IOException {
            out.value(value);
        }
    };

    /**
     * double数据序列化处理
     */
    private static final TypeAdapter<Number> DOUBLE = new TypeAdapter<Number>() {

        @Override
        public Number read(JsonReader in) throws IOException {
            if (in.peek() == JsonToken.NULL) {
                in.nextNull();
                return null;
            }
            try {
                String result = in.nextString();
                if ("".equals(result)) {
                    return Double.NaN;
                }
                return Double.parseDouble(result);
            } catch (NumberFormatException e) {
                throw new JsonSyntaxException(e);
            }
        }

        @Override
        public void write(JsonWriter out, Number value) throws IOException {
            out.value(value);
        }
    };

    /**
     * 定义Gson
     * 不建议支持注册int类型转boolean的TypeAdapter。原因是反序列化时, 值又变化boolean型数据, 与服务端定义的int型不匹配
     */
    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(int.class, INTEGER)
            .registerTypeAdapter(Integer.class, INTEGER)
            .registerTypeAdapter(double.class, DOUBLE)
            .registerTypeAdapter(Double.class, DOUBLE)
            .create();

    /**
     * 将json序列化成对象
     * <p>
     * <p>
     * class Result<T>{
     * String text;
     * T data;
     * }
     * Type jsonType = new TypeToken<Result<Object>>() {
     * }.getType();
     * Result result = fromJson(text, jsonType);
     *
     * @param json
     * @param typeOfT
     * @param <T>
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> T fromJson(String json, Type typeOfT) {
        if (json == null) {
            return null;
        }
        try {
            return GSON.fromJson(json, typeOfT);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 将json序列化成对象
     *
     * @param json
     * @param classOfT
     * @param <T>
     * @return
     */
    public static <T> T fromJson(String json, Class<T> classOfT) {
        if (json == null) {
            return null;
        }
        try {
            return GSON.fromJson(json, classOfT);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 将对象转为json字符串
     *
     * @param object
     * @return
     */
    public static String toJson(Object object) {
        return GSON.toJson(object);
    }
}
