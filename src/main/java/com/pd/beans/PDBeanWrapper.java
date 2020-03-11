package com.pd.beans;

/**
 * @author zhaozhengkang
 * @description
 * @date 2020-3-8 21:05
 */
public class PDBeanWrapper {

    private Object wrapperInstance;
    private Class<?> wrapperClass;

    public PDBeanWrapper(Object instance) {
        this.wrapperInstance = instance;
        this.wrapperClass = instance.getClass();
    }

    public Object getWrapperInstance() {
        return wrapperInstance;
    }

    public Class<?> getWrapperClass() {
        return wrapperClass;
    }
}
