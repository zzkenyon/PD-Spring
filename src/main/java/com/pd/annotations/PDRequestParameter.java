package com.pd.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * @description: spring
 * @author: zhaozhengkang
 * @date: 2020-02-05 19:24
 */
@Target(ElementType.PARAMETER)
public @interface PDRequestParameter {
    String name()default "";
}
