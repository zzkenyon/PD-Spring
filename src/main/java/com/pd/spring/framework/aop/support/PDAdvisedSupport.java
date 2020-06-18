package com.pd.spring.framework.aop.support;

import com.pd.spring.framework.aop.aspect.PDAdvice;
import com.pd.spring.framework.aop.config.PDAopConfig;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 解析aop配置的工具类
 * @author zhaozhengkang
 * @description
 * @date 2020/4/13 9:24
 */
public class PDAdvisedSupport {
    private PDAopConfig config;
    private Object target;
    private Class targetClass;
    private Pattern pointCutClassPattern;
    /**
     * Method 为目标类方法，Map<String, PDAdvice>为aop配置中解析出来的切面信息
     */
    private Map<Method, Map<String, PDAdvice>> methodCache;

    public PDAdvisedSupport(PDAopConfig config) {
        this.config = config;
    }

    /**
     * 根据一个目标代理类的方法，获得对应的通知
     *
     * @param method
     * @param o
     * @return
     * @throws Exception
     */
    public Map<String, PDAdvice> getAdvices(Method method, Object o) throws Exception {
        Map<String,PDAdvice> cache = methodCache.get(method);
        if(null == cache){
            Method m = targetClass.getMethod(method.getName(),method.getParameterTypes());
            cache = methodCache.get(m);
            this.methodCache.put(m,cache);
        }
        return cache;
    }

    /**
     * 判断目标类需不需要生成代理对象（是否匹配aop切面表达式）
     * @return
     */
    public boolean pointCutMatch() {
        return pointCutClassPattern.matcher(this.targetClass.toString()).matches();
    }


    public Object getTarget() {
        return target;
    }

    public void setTarget(Object target) {
        this.target = target;
    }

    public Class getTargetClass() {
        return targetClass;
    }

    /**
     * 拿到目标类，进行aop解析
     * @param targetClass
     */
    public void setTargetClass(Class targetClass) {
        this.targetClass = targetClass;
        parse();
    }

    /**
     * aop配置信息的解析逻辑
     */
    private void parse() {
        String pointCut = config.getPointCut()
                .replaceAll("\\.", "\\\\.")
                .replaceAll("\\\\.\\*", ".*")
                .replaceAll("\\(", "\\\\(")
                .replaceAll("\\)", "\\\\)");
        //切面的正则表达式为 public .* com\.gupaoedu\.vip\.demo\.service\..*Service\..*\(.*\)
        String pointCutForClassRegex = pointCut.substring(0, pointCut.lastIndexOf("\\("));
        pointCutForClassRegex = pointCutForClassRegex.substring(0,pointCutForClassRegex.lastIndexOf("\\."));
        //拿到类的匹配模式
        pointCutClassPattern = Pattern.compile("class " + pointCutForClassRegex.substring(pointCut.lastIndexOf(" ") + 1));

        //创建享元的共享池
        methodCache = new HashMap<>();
        //匹配方法的正则
        Pattern pointCutMethodPattern = Pattern.compile(pointCut);
        try {
            Class aspectClass = Class.forName(this.config.getAspectClass());
            Map<String, Method> aspectMethods = new HashMap<>();
            for (Method m : aspectClass.getMethods()) {
                aspectMethods.put(m.getName(), m);
            }
            //以下代码，逐个将目标类中的方法与aop配置的通知进行映射
            for (Method method : targetClass.getMethods()) {
                String methodString = method.toString();
                if (methodString.contains("throws")) {
                    methodString = methodString.substring(0, methodString.lastIndexOf("throws")).trim();
                }

                Matcher matcher = pointCutMethodPattern.matcher(methodString);
                if (matcher.matches()) {
                    Map<String, PDAdvice> advices = new HashMap<>();
                    if (!(null == config.getAspectBefore() || "".equals(config.getAspectBefore()))) {
                        advices.put("before", new PDAdvice(aspectClass.newInstance(), aspectMethods.get(config.getAspectBefore())));
                    }
                    if (!(null == config.getAspectAfter() || "".equals(config.getAspectAfter()))) {
                        advices.put("after", new PDAdvice(aspectClass.newInstance(), aspectMethods.get(config.getAspectAfter())));
                    }
                    if (!(null == config.getAspectAfterThrow() || "".equals(config.getAspectAfterThrow()))) {
                        PDAdvice advice = new PDAdvice(aspectClass.newInstance(), aspectMethods.get(config.getAspectAfterThrow()));
                        advice.setThrownName(config.getAspectAfterThrowingName());
                        advices.put("afterThrow", advice);
                    }
                    methodCache.put(method, advices);
                }
            }
        } catch (Exception e) {
            e.getStackTrace();
        }
    }
}

