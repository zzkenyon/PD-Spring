package com.pd.servlet;

import com.pd.annotations.PDController;
import com.pd.annotations.PDRequestMapping;
import com.pd.context.PDApplicationContext;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * @author zhaozhengkang
 * @description
 * @date 2020-3-8 21:13
 */
public class PDDispatcherServlet_2 extends HttpServlet {
    PDApplicationContext applicationContext;
    private Map<String, Method> handlerMapping = new HashMap<>();
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            doDispatch(req,resp);
        } catch (Exception e) {
            e.getStackTrace();
            resp.getWriter().write("500 exception");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //6.调度执行
        try {
            doDispatch(req,resp);
        } catch (Exception e) {
            e.getStackTrace();
            resp.getWriter().write("500 exception");
        }
    }

    private void doDispatch(HttpServletRequest req, HttpServletResponse resp) throws Exception{
        String uri = req.getRequestURI();
        String contextPath = req.getContextPath();
        uri = uri.replaceAll(contextPath,"").replaceAll("/+","/");

        if(!this.handlerMapping.containsKey(uri)){

            resp.getWriter().write("404 not found");
            return;
        }
        Map<String,String[]> paramsMap = req.getParameterMap();
        Method method = handlerMapping.get(uri);
        String beanName = toFirstLowerCase(method.getDeclaringClass().getSimpleName());
        Object res = method.invoke(beanName,new Object[]{req,resp,paramsMap.get("name")[0]});
        resp.getWriter().write(res.toString());
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        applicationContext = new PDApplicationContext(config.getInitParameter("contextConfigLocation"));

        //5.生成handlerMapping
        initHandlerMapping();

        System.out.println("init finish");
    }

    private String toFirstLowerCase(String simpleName) {
        char[] chars = simpleName.toCharArray();
        chars[0] += 32;
        return String.valueOf(chars);
    }

    private void initHandlerMapping() {
        if(applicationContext.getBeanDefinitionCount()==0) {
            return;
        }
        String[] beanNames = applicationContext.getBeanDefinitionNames();
        for(String beanName : beanNames){
            Object instance = applicationContext.getBean(beanName);
            Class<?> clazz = instance.getClass();
            if(!clazz.isAnnotationPresent(PDController.class)) {
                continue;
            }
            String url = "/" + clazz.getAnnotation(PDRequestMapping.class).value();
            for(Method method : clazz.getMethods()){
                if(!method.isAnnotationPresent(PDRequestMapping.class)){continue;}
                PDRequestMapping requestMapping = method.getAnnotation(PDRequestMapping.class);
                url = url + "/" + requestMapping.value();
                handlerMapping.put(url.replaceAll("/+","/"),method);
                System.out.println("Mapped: " + url + "," + method);
            }
        }
    }
}
