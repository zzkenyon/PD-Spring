package com.pd.spring.framework.webmvc.servlet;

import com.pd.spring.framework.annotations.PDRequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author zhaozhengkang
 * @description
 * @date 2020-4-2 20:35
 */
public class PDHandlerAdapter {
    public PDModelAndView handle(HttpServletRequest req, HttpServletResponse resp, PDHandlerMapping handler)
            throws Exception{
        //1、拼接形参列表
        // 将controller方法上的参数名对应的位置保存下来
        Map<String,Integer> paramIndexMapping = new HashMap<>();
        Annotation[][] pa = handler.getMethod().getParameterAnnotations();
        for(int i = 0; i<pa.length; i++){
            for(Annotation a : pa[i]){
                if(a instanceof PDRequestParam){
                    String paramName = ((PDRequestParam) a).value();
                    if(!"".equals(paramName.trim())){
                        paramIndexMapping.put(paramName,i);
                    }
                }
            }
        }
        //初始化一下
        Class<?>[] paramTypes = handler.getMethod().getParameterTypes();
        for(int i = 0; i < paramTypes.length; i++){
            Class<?> clazz = paramTypes[i];
            if(clazz == HttpServletRequest.class || clazz == HttpServletResponse.class){
                paramIndexMapping.put(clazz.getName(),i);
            }
        }


        // 从request上获取对应url的参数
        Map<String,String[]> paramMap = req.getParameterMap();
        // 保存实参
        Object[] paramValues = new Object[paramTypes.length];
        for(Map.Entry<String,String[]> param : paramMap.entrySet()){
            String value = Arrays.toString(paramMap.get(param.getKey()))
                    .replaceAll("\\[|\\]","")
                    .replaceAll("\\s",",");
            if(!paramIndexMapping.containsKey(param.getKey())){
                continue;
            }
            Integer index = paramIndexMapping.get(param.getKey());
            paramValues[index] = caseStringValue(value,paramTypes[index]);
        }
        if(paramIndexMapping.containsKey(HttpServletRequest.class.getName())){
            int index = paramIndexMapping.get(HttpServletRequest.class.getName());
            paramValues[index] = req;
        }
        if(paramIndexMapping.containsKey(HttpServletResponse.class.getName())){
            int index = paramIndexMapping.get(HttpServletResponse.class.getName());
            paramValues[index] = resp;
        }
        Object res = handler.getMethod().invoke(handler.getController(),paramValues);
        if(res == null || res instanceof Void) {
            return null;
        }
        boolean isModelAndView = handler.getMethod().getReturnType() == PDModelAndView.class;
        if(isModelAndView){
            return (PDModelAndView)res;
        }
        return null;
    }

    private Object caseStringValue(String value, Class<?> paramType) {
        if(String.class == paramType){
            return value;
        }else if(Integer.class == paramType){
            return Integer.valueOf(value);
        }else if(Double.class == paramType){
            return Double.valueOf(value);
        }else {
            return value;
        }
    }
}
