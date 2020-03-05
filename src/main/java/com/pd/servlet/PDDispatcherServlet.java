package com.pd.servlet;

import com.pd.annotations.PDAutowired;
import com.pd.annotations.PDController;
import com.pd.annotations.PDRequestMapping;
import com.pd.annotations.PDService;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.*;

/**
 * @description: spring
 * @author: zhaozhengkang
 * @date: 2020-02-06 06:16
 */
public class PDDispatcherServlet extends HttpServlet {
    Properties configContext = new Properties();
    private List<String> classNames = new ArrayList<>();
    private Map<String,Object> ioc = new HashMap<>();
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
        method.invoke(ioc.get(beanName),new Object[]{req,resp,paramsMap.get("name")[0]});
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        //1.加载配置文件
        doLoadConfig(config.getInitParameter("contextConfigLocation"));
        //2.扫描
        doScanner(configContext.getProperty("scanPackage"));
        //3.实例化
        doInstance();
        //4.依赖注入
        doAutoWired();
        //5.生成handlerMapping
        initHandlerMapping();

        System.out.println("init finish");
    }

    private void doLoadConfig(String contextConfigLocation){
        try(InputStream is = this.getClass().getClassLoader().getResourceAsStream(contextConfigLocation)){
            configContext.load(is);
        }catch (IOException e){
            e.getStackTrace();
        }
    }
    private void doScanner(String scanPackage) {
        URL url = this.getClass().getClassLoader().getResource("/" + scanPackage.replaceAll("\\.","/"));
        File classPath = new File(url.getFile());
        for (File f : classPath.listFiles()){
            //递归
            if(f.isDirectory()){
                doScanner(scanPackage + "." + f.getName());
            }else {
                if(!f.getName().endsWith(".class")){continue;}
                String className = scanPackage + "." + f.getName().replace(".class", "");
                classNames.add(className);
            }
        }
    }

    private void doInstance() {
        if(classNames.isEmpty()) return;
        try {
            for (String className : classNames) {
                Class<?> clazz = Class.forName(className);
                if(clazz.isAnnotationPresent(PDController.class)) {
                    String beanName = toFirstLowerCase(clazz.getSimpleName());
                    ioc.put(beanName, clazz.newInstance());
                }else if(clazz.isAnnotationPresent(PDService.class)){

                    //1.默认
                    String beanName = toFirstLowerCase(clazz.getSimpleName());
                    //2.自定义服务名
                    PDService service = clazz.getAnnotation(PDService.class);
                    if(!"".equals(service.value())){
                        beanName = service.value();
                    }
                    Object instance = clazz.newInstance();
                    ioc.put(beanName,instance);
                    //3.以接口名称
                    for(Class<?> c : clazz.getInterfaces()){
                        if(ioc.containsKey(c.getName()))
                            throw new Exception("The beanName is exists!");
                        ioc.put(c.getName(),instance);
                    }
                }
            }
        }catch (Exception e){
            e.getStackTrace();
        }
    }

    private String toFirstLowerCase(String simpleName) {
        char[] chars = simpleName.toCharArray();
        chars[0] += 32;
        return String.valueOf(chars);
    }

    private void doAutoWired() {
        if(ioc.isEmpty()) {return;}

        for(Map.Entry<String,Object> entry : ioc.entrySet()){
            for(Field field : entry.getValue().getClass().getDeclaredFields()){
                if(!field.isAnnotationPresent(PDAutowired.class)){continue;}
                PDAutowired autowired = field.getAnnotation(PDAutowired.class);
                String beanName = autowired.value().trim();
                if("".equals(beanName)){
                    beanName = field.getType().getName();
                    field.setAccessible(true);
                    try {
                        field.set(entry.getValue(),ioc.get(beanName));
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private void initHandlerMapping() {
        if(ioc.isEmpty()) {return;}
        for(Map.Entry<String,Object> entry : ioc.entrySet()){
            Class<?> clazz = entry.getValue().getClass();
            if(!clazz.isAnnotationPresent(PDController.class)){continue;}
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
