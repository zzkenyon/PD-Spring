package com.pd.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * @description: spring
 * @author: zhaozhengkang
 * @date: 2020-02-05 17:06
 */
@Inherited
@Target(ElementType.TYPE)
public @interface PDController {
    String value() default "";
}
