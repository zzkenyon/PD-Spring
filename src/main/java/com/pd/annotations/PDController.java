package com.pd.annotations;

import java.lang.annotation.*;

/**
 * @description: spring
 * @author: zhaozhengkang
 * @date: 2020-02-05 17:06
 */

@Inherited
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface PDController {
    String value() default "";
}
