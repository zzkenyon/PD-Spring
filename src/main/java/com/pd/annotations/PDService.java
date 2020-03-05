package com.pd.annotations;

import java.lang.annotation.*;

/**
 * @description: spring
 * @author: zhaozhengkang
 * @date: 2020-02-05 18:43
 */
@Inherited
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface PDService {
    String value() default "";
}
