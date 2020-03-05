package com.pd.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Target;

/**
 * @description: spring
 * @author: zhaozhengkang
 * @date: 2020-02-05 18:43
 */
@Inherited
@Target(ElementType.TYPE)
public @interface PDService {
    String value() default "";
}
