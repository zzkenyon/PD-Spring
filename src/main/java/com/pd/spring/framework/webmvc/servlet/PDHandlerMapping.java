package com.pd.spring.framework.webmvc.servlet;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.lang.reflect.Method;
import java.util.regex.Pattern;

/**
 * @author zhaozhengkang
 * @description
 * @date 2020-4-2 20:33
 */
@Data
@AllArgsConstructor
public class PDHandlerMapping {

    private Object controller;
    private Pattern pattern;
    private Method method;

}