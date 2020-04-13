package com.pd.demo.action;


import com.pd.demo.service.IQueryService;
import com.pd.spring.framework.annotations.PDAutowired;
import com.pd.spring.framework.annotations.PDController;
import com.pd.spring.framework.annotations.PDRequestMapping;
import com.pd.spring.framework.annotations.PDRequestParam;
import com.pd.spring.framework.webmvc.servlet.PDModelAndView;

import java.util.HashMap;
import java.util.Map;

/**
 * 公布接口url
 * @author Tom
 *
 */
@PDController
@PDRequestMapping("/web")
public class PageAction {

    @PDAutowired
    IQueryService queryService;

    @PDRequestMapping("/first.html")
    public PDModelAndView query(@PDRequestParam("teacher") String teacher){
        String result = queryService.query(teacher);
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("teacher", teacher);
        model.put("data", result);
        model.put("token", "123456");
        return new PDModelAndView("first.html",model);
    }

}
