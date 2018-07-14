package com.agmbat.db.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Column {

    /**
     * 声明表中字段名
     *
     * @return
     */
    String name();

    /**
     * ?
     *
     * @return
     */
    String property() default "";

    /**
     * 声明是否为id字段
     *
     * @return
     */
    boolean isId() default false;

    /**
     * 如果是id字段是否自动生成?,用于自增加字段吗？
     */
    boolean autoGen() default true;
}
