package com.pd.demo.controller;

import com.pd.demo.service.IDemoService;
import com.pd.spring.framework.annotations.PDAutowired;
import com.pd.spring.framework.annotations.PDController;
import com.pd.spring.framework.annotations.PDRequestParam;
import com.pd.spring.framework.annotations.PDRequestMapping;

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
    private IDemoService IDemoService;

    @PDRequestMapping(value = "/query")
    public String query(HttpServletRequest request, HttpServletResponse response,
                        @PDRequestParam String name){
        return IDemoService.printName(name);
    }
}
