package com.pd.servlet;

import java.lang.reflect.Method;
import java.util.regex.Pattern;

/**
 * @author zhaozhengkang
 * @description
 * @date 2020-4-2 20:33
 */
public class PDHandlerMapping {
    private Object controller;
    private Pattern pattern;
    private Method method;
    public PDHandlerMapping(Pattern pattern, Method method, Object instance) {
        this.controller = instance;
        this.pattern = pattern;
        this.method = method;
    }

    public Object getController() {
        return controller;
    }

    public void setController(Object controller) {
        this.controller = controller;
    }

    public Pattern getPattern() {
        return pattern;
    }

    public void setPattern(Pattern pattern) {
        this.pattern = pattern;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }
}