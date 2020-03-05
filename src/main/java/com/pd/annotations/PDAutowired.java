package com.pd.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * @description: spring
 * @author: zhaozhengkang
 * @date: 2020-02-05 18:46
 */
@Target({ElementType.FIELD,ElementType.METHOD,ElementType.CONSTRUCTOR})
public @interface PDAutowired {
    String value() default "";
}
