package com.pd.spring.framework.annotations;

import java.lang.annotation.*;

/**
 * @description: spring
 * @author: zhaozhengkang
 * @date: 2020-02-05 17:11
 */
@Target({ElementType.TYPE,ElementType.METHOD})
@Inherited
@Retention(RetentionPolicy.RUNTIME)
public @interface PDRequestMapping {
    String value() default "";
}
