package com.pd.demo;

import com.pd.annotations.PDAutowired;
import com.pd.annotations.PDController;
import com.pd.annotations.PDRequestParameter;
import com.pd.annotations.PDRequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @description: spring
 * @author: zhaozhengkang
 * @date: 2020-02-05 18:44
 */
@PDController
@PDRequestMapping(value = "/demo")
public class DemoController {
    @PDAutowired
    private DemoService demoService;

    @PDRequestMapping(value = "/query")
    public String query(HttpServletRequest request, HttpServletResponse response,
                        @PDRequestParameter String name){
        return demoService.printName(name);
    }
}
