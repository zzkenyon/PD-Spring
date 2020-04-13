package com.pd.demo.service.impl;

import com.pd.demo.service.IDemoService;
import com.pd.spring.framework.annotations.PDService;

/**
 * @description: spring
 * @author: zhaozhengkang
 * @date: 2020-02-05 18:45
 */
@PDService
public class DemoService implements IDemoService {
    @Override
    public String printName(String name) {

        return "hello " + name;
    }
}
