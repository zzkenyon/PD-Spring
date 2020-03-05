package com.pd.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Target;

/**
 * @description: spring
 * @author: zhaozhengkang
 * @date: 2020-02-05 17:11
 */
@Target({ElementType.TYPE,ElementType.METHOD})
@Inherited
public @interface PDRequestMapping {
    String value() default "";
}
