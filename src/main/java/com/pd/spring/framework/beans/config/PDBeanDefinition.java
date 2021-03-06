package com.pd.spring.framework.beans.config;

/**
 * @author zhaozhengkang
 * @description bean配置信息的封装类
 * @date 2020-3-8 21:06
 */
public class PDBeanDefinition {
    public PDBeanDefinition(){

    }

    public PDBeanDefinition(String factoryBeanName,String beanClassName){
        this.beanClassName = beanClassName;
        this.factoryBeanName = factoryBeanName;
    }
    /**
     * 在IOC容器中的key  clazz.getSimpleName()
     */
    private String factoryBeanName;

    /**
     * 全类名 clazz.getName()
     */
    private String beanClassName;



    public String getBeanClassName() {
        return beanClassName;
    }

    public void setBeanClassName(String beanClassName) {
        this.beanClassName = beanClassName;
    }

    public String getFactoryBeanName() {
        return factoryBeanName;
    }

    public void setFactoryBeanName(String factoryBeanName) {
        this.factoryBeanName = factoryBeanName;
    }
}
