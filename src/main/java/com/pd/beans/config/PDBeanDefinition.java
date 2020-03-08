package com.pd.beans.config;

import com.pd.beans.PDBeanWrapper;

/**
 * @author zhaozhengkang
 * @description
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
     * 在IOC容器中的key
     */
    private String factoryBeanName;

    /**
     * 全类名
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
