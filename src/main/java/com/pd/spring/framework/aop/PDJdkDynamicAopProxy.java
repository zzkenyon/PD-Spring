package com.pd.spring.framework.aop;

import com.pd.spring.framework.aop.aspect.PDAdvice;
import com.pd.spring.framework.aop.support.PDAdvisedSupport;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;

/**
 * 动态代理对象生成类
 * @author zhaozhengkang
 * @description
 * @date 2020/4/13 9:25
 */
public class PDJdkDynamicAopProxy implements InvocationHandler {

    private PDAdvisedSupport config;

    public PDJdkDynamicAopProxy(PDAdvisedSupport config) {

        this.config = config;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Map<String, PDAdvice> advices = config.getAdvices(method,null);
        Object returnValue;
        if(advices == null){
            return method.invoke(this.config.getTarget(),args);
        }
        try{
            invokeAdvice(advices.get("before"));
            returnValue = method.invoke(this.config.getTarget(),args);
            invokeAdvice(advices.get("after"));
        }catch (Exception e){
            invokeAdvice(advices.get("afterThrown"));
            throw e;
        }
        return returnValue;
    }

    private void invokeAdvice(PDAdvice advice) {
        try{
            advice.getAdviceMethod().invoke(advice.getAspect());
        }catch (IllegalAccessException e) {
            e.printStackTrace();
        }catch (InvocationTargetException e){
            e.printStackTrace();
        }
    }

    public Object getProxy() {
        return Proxy.newProxyInstance(this.getClass().getClassLoader(),this.config.getTargetClass().getInterfaces(),this);
    }
}
