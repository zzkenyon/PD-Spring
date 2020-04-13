package com.pd.spring.framework.webmvc.servlet;

import com.pd.spring.framework.annotations.PDController;
import com.pd.spring.framework.annotations.PDRequestMapping;
import com.pd.spring.framework.context.PDApplicationContext;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 3.0版本 mvc的实现（HandlerMapping的 抽离）
 * @author zhaozhengkang
 * @description
 * @date 2020-4-2 20:23
 */
public class PDDispatcherServlet_3 extends HttpServlet {
    PDApplicationContext applicationContext;
    private List<PDHandlerMapping> handlerMappings = new ArrayList<>();
    private Map<PDHandlerMapping,PDHandlerAdapter> handlerAdapters = new HashMap<>();

    private List<PDViewResolver> viewResolvers = new ArrayList<>();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doPost(req,resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //6.调度执行
        try {
            doDispatch(req,resp);
        } catch (Exception e) {
            try{
                processDispatchResult(req,resp,new PDModelAndView("500"));
            }catch (Exception e1){
                e1.getStackTrace();
                resp.getWriter().write("500 Exception,Detail : " + Arrays.toString(e.getStackTrace()));
            }
        }
    }

    private void doDispatch(HttpServletRequest req, HttpServletResponse resp) throws Exception{
        //根据url获得handler
        PDHandlerMapping handler = getHandler(req);
        if(handler == null){
            processDispatchResult(req,resp,new PDModelAndView("404"));
            return;
        }
        //根据handlerMapping拿到对应的handlerAdapter
        PDHandlerAdapter ha = getHandlerAdapter(handler);

        //从handlerAdapter中拿到ModelAndView
        PDModelAndView mv = ha.handle(req,resp,handler);

        //通过viewResolver机械modelAndView，得到view或者json
        processDispatchResult(req,resp,mv);
    }

    private void processDispatchResult(HttpServletRequest req, HttpServletResponse resp, PDModelAndView modelAndView) throws Exception {
        if (null == modelAndView) {
            return;
        }
        if(this.viewResolvers.isEmpty()){
            return;
        }
        for(PDViewResolver vr:viewResolvers){
            PDView view = vr.resolveViewName(modelAndView.getViewName());
            view.render(modelAndView.getModel(),req,resp);
            return;
        }
    }

    private PDHandlerAdapter getHandlerAdapter(PDHandlerMapping handler) {
        if(this.handlerAdapters.isEmpty()){
            return null;
        }
        return this.handlerAdapters.get(handler);
    }

    private PDHandlerMapping getHandler(HttpServletRequest req) {
        if(this.handlerMappings.isEmpty()){
            return null;
        }
        String url = req.getRequestURI();
        String contextPath = req.getContextPath();
        url =url.replaceAll(contextPath,"").replaceAll("/+","/");
        for(PDHandlerMapping mapping : this.handlerMappings){
            Matcher matcher = mapping.getPattern().matcher(url);
            if(!matcher.matches()){
                continue;
            }
            return mapping;
        }
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
                handlerMappings.add(new PDHandlerMapping(instance,pattern,method));
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
        String templateRoot = applicationContext.getReader().getConfig().getProperty("templateRoot");
        String templateRootPath = this.getClass().getClassLoader().getResource(templateRoot).getFile();

        File TemplateRootDir = new File(templateRootPath);
        for(File file : TemplateRootDir.listFiles()){
            this.viewResolvers.add(new PDViewResolver(templateRoot));
        }


    }
}
