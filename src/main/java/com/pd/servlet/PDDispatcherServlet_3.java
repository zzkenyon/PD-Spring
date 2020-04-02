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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @author zhaozhengkang
 * @description
 * @date 2020-4-2 20:23
 */
public class PDDispatcherServlet_3 extends HttpServlet {
    PDApplicationContext applicationContext;
    private List<PDHandlerMapping> handlerMappings = new ArrayList<>();
    private Map<PDHandlerMapping,PDHandlerAdapter> handlerAdapters = new HashMap<>();

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
        //根据url获得handler
        PDHandlerMapping handler = getHandler(req);

        //根据handlerMapping拿到对应的handlerAdapter
        PDHandlerAdapter ha = getHandlerAdapter(handler);

        //从handlerAdapter中拿到ModelAndView
        PDModelAndView mv = ha.handle(req,resp,handler);

        //通过viewResolver机械modelAndView，得到view或者json
        processDispatchResult(req,resp,mv);
    }

    private void processDispatchResult(HttpServletRequest req, HttpServletResponse resp, PDModelAndView modelAndView) {

    }

    private PDHandlerAdapter getHandlerAdapter(PDHandlerMapping handler) {
        return null;
    }

    private PDHandlerMapping getHandler(HttpServletRequest req) {
        return null;
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        applicationContext = new PDApplicationContext(config.getInitParameter("contextConfigLocation"));

        //5.生成handlerMapping
        //initHandlerMapping();
        initStrategies(applicationContext);

        System.out.println("init finish");
    }

    private void initStrategies(PDApplicationContext applicationContext) {
        //通过handlerMapping，将请求映射到处理器
        initHandlerMapping(applicationContext);
        //通过handlerAdapter，进行多类型的参数动态匹配
        initHandlerAdapter(applicationContext);
        //解析逻辑视图到具体试图实现
        initViewResolvers(applicationContext);
    }

    private void initHandlerMapping(PDApplicationContext applicationContext) {
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
                String regex = (url + "/" + requestMapping.value().replaceAll("\\*",".*")).replaceAll("/+","/");
                Pattern pattern = Pattern.compile(regex);
                handlerMappings.add(new PDHandlerMapping(pattern,method,instance));
                System.out.println("Mapped: " + regex + "," + method);
            }
        }
    }

    private void initHandlerAdapter(PDApplicationContext applicationContext) {
        for(PDHandlerMapping handler : handlerMappings){
            this.handlerAdapters.put(handler,new PDHandlerAdapter());
        }

    }
    private void initViewResolvers(PDApplicationContext applicationContext) {
        String templateRoot = applicationContext.getConfig().getProperty("templateRoot");
    }
}
