package com.pd.spring.framework.webmvc.servlet;

import lombok.Getter;

import java.util.Map;

/**
 * @author zhaozhengkang
 * @description
 * @date 2020-4-2 20:38
 */
@Getter
public class PDModelAndView {

    private String viewName;
    private Map<String,?> model;

    public PDModelAndView(String viewName) {
        this.viewName = viewName;
    }

    public PDModelAndView(String viewName, Map<String, ?> model) {
        this.viewName = viewName;
        this.model = model;
    }
}
