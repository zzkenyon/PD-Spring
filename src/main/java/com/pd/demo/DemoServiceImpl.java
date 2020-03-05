package com.pd.demo;

import com.pd.annotations.PDService;

/**
 * @description: spring
 * @author: zhaozhengkang
 * @date: 2020-02-05 18:45
 */
@PDService
public class DemoServiceImpl implements DemoService {
    public String printName(String name) {
        return "hello " + name;
    }
}
