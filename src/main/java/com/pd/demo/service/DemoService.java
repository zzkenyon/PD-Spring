package com.pd.demo.service;

import com.pd.spring.framework.annotations.PDService;

/**
 * @description: spring
 * @author: zhaozhengkang
 * @date: 2020-02-05 18:45
 */
@PDService
public class DemoService implements IDemoService {
    @Override
    public String printName(String name,int age) {

        return "hello " + name;
    }

    @Override
    public String printName_2(String name){
        return printName(name,20);
    }
}
