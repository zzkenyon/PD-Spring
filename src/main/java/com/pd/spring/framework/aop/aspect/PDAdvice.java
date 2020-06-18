package com.pd.spring.framework.aop.aspect;

import java.lang.reflect.Method;

/**
 * 切面通知
 * @author zhaozhengkang
 * @description
 * @date 2020/4/13 9:22
 */
public class PDAdvice {
    private Object aspect;
    private Method adviceMethod;
    private String thrownName;

    public PDAdvice(Object aspect, Method adviceMethod) {
        this.aspect = aspect;
        this.adviceMethod = adviceMethod;
    }

    public Object getAspect() {
        return aspect;
    }

    public void setAspect(Object aspect) {
        this.aspect = aspect;
    }

    public Method getAdviceMethod() {
        return adviceMethod;
    }

    public void setAdviceMethod(Method adviceMethod) {
        this.adviceMethod = adviceMethod;
    }

    public String getThrownName() {
        return thrownName;
    }

    public void setThrownName(String thrownName) {
        this.thrownName = thrownName;
    }
}
