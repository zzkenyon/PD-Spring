package com.pd.spring.framework.context;


import com.pd.spring.framework.annotations.PDAutowired;
import com.pd.spring.framework.annotations.PDController;
import com.pd.spring.framework.annotations.PDService;
import com.pd.spring.framework.aop.PDJdkDynamicAopProxy;
import com.pd.spring.framework.aop.config.PDAopConfig;
import com.pd.spring.framework.aop.support.PDAdvisedSupport;
import com.pd.spring.framework.beans.PDBeanWrapper;
import com.pd.spring.framework.beans.config.PDBeanDefinition;
import com.pd.spring.framework.beans.support.PDBeanDefinitionReader;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 职责：完成bean创建和di
 * @author zhaozhengkang
 * @description
 * @date 2020-3-8 21:03
 */
public class PDApplicationContext {

    /**
     * 配置文件解析器
     */
    private PDBeanDefinitionReader reader;
    private Map<String, PDBeanDefinition> beanDefinitionMap = new HashMap<>();
    private Map<String, PDBeanWrapper>  factoryBeanInstanceCache = new HashMap<>();
    private Map<String,Object> factoryBeanObjectCache = new HashMap<>();

    public PDBeanDefinitionReader getReader() {
        return reader;
    }

    public PDApplicationContext(String ... configLocations){
        try {
            //1 读取配置文件
            reader = new PDBeanDefinitionReader(configLocations);
            //2 解析配置文件，将配置信息封装成BeanDefinition
            List<PDBeanDefinition> beanDefinitions = reader.loadBeanDefinitions();
            //3 将BeanDefinition 保存到一个缓存配置的容器，key应该是和IOC容器的beanName的Key相对应
            doRegisterBeanDefinition(beanDefinitions);
            //4 完成依赖注入，如果延时加载，这一步不会发生
            doAutowire();
        }catch(Exception e){
            e.getStackTrace();
        }
    }

    private void doAutowire() {
        //调用getbean
        for(Map.Entry<String,PDBeanDefinition> beanDefinitionMapEntry : this.beanDefinitionMap.entrySet()){
            String beanName = beanDefinitionMapEntry.getKey();
            getBean(beanName);
        }

    }

    private void doRegisterBeanDefinition(List<PDBeanDefinition> definitions) throws Exception{
        for(PDBeanDefinition beanDefinition : definitions){
            if(this.beanDefinitionMap.containsKey(beanDefinition.getFactoryBeanName()) ){
                continue;
                //throw new Exception("The" + beanDefinition.getFactoryBeanName() + "is exist!!");
            }
            this.beanDefinitionMap.put(beanDefinition.getFactoryBeanName(),beanDefinition);
            this.beanDefinitionMap.put(beanDefinition.getBeanClassName(),beanDefinition);
        }
    }

    public Object getBean(String beanName){
        //1 拿到BeanDefinition
        PDBeanDefinition beanDefinition = this.beanDefinitionMap.get(beanName);

        //2 创建出真正的实例对象
        Object instance = instantBean(beanName,beanDefinition);

        //3 把创建出来的对象实例封装成BeanWrapper
        PDBeanWrapper wrapper = new PDBeanWrapper(instance);

        //4 把wrapper对象放到IOC容器
        this.factoryBeanInstanceCache.put(beanName,wrapper);

        //5 依赖注入
        populateBean(beanName,new PDBeanDefinition(),wrapper);
        return this.factoryBeanInstanceCache.get(beanName).getWrapperInstance();
    }

    private Object instantBean(String beanName, PDBeanDefinition beanDefinition) {
        String className = beanDefinition.getBeanClassName();
        Object instance = null;
        try {
            Class<?> clazz = Class.forName(className);
            instance = clazz.getConstructor().newInstance();
            //==================AOP开始====================
            //1、加载AOP配置文件
            PDAdvisedSupport config = instantiationAopConfig(beanDefinition);
            config.setTargetClass(clazz);
            config.setTarget(instance);
            //2、如果当前实例化的类符合匹配切面表达式，生成代理类对象替换。
            if(config.pointCutMatch()){
                instance = new PDJdkDynamicAopProxy(config).getProxy();
            }
            //==================AOP结束====================
            this.factoryBeanObjectCache.put(beanName,instance);
        }catch (Exception e){
            e.getStackTrace();
        }
        return instance;
    }

    private PDAdvisedSupport instantiationAopConfig(PDBeanDefinition beanDefinition) {
        PDAopConfig config = new PDAopConfig();
        config.setPointCut(this.reader.getConfig().getProperty("pointCut"));
        config.setAspectClass(this.reader.getConfig().getProperty("aspectClass"));
        config.setAspectBefore(this.reader.getConfig().getProperty("aspectBefore"));
        config.setAspectAfter(this.reader.getConfig().getProperty("aspectAfter"));
        config.setAspectAfterThrow(this.reader.getConfig().getProperty("aspectAfterThrow"));
        config.setAspectAfterThrowingName(this.reader.getConfig().getProperty("aspectAfterThrowingName"));
        return new PDAdvisedSupport(config);
    }

    private void populateBean(String beanName, PDBeanDefinition pdBeanDefinition, PDBeanWrapper wrapper) {
        Object instance = wrapper.getWrapperInstance();
        Class<?> clazz = wrapper.getWrapperClass();

        if(!(clazz.isAnnotationPresent(PDController.class) || clazz.isAnnotationPresent(PDService.class))){
            return;
        }
        for(Field field : clazz.getDeclaredFields()){
            if(!field.isAnnotationPresent(PDAutowired.class)){
                continue;
            }
            PDAutowired autowired = field.getAnnotation(PDAutowired.class);
            String autowiredBeanName = autowired.value().trim();
            if("".equals(autowiredBeanName)){
                autowiredBeanName = field.getType().getName();
                field.setAccessible(true);
                try {
                    if(this.factoryBeanInstanceCache.get(autowiredBeanName)==null){
                        continue;
                    }
                    field.set(instance,this.factoryBeanInstanceCache.get(autowiredBeanName).getWrapperInstance());
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }



    public Object getBean(Class beanClass){
        return getBean(beanClass.getName());
    }

    public String[] getBeanDefinitionNames(){
        return beanDefinitionMap.keySet().toArray(new String[this.beanDefinitionMap.size()]);
    }
    public int getBeanDefinitionCount(){
        return beanDefinitionMap.size();
    }

}
