package com.pd.demo.action;


import com.pd.demo.service.IModifyService;
import com.pd.demo.service.IQueryService;
import com.pd.spring.framework.annotations.PDAutowired;
import com.pd.spring.framework.annotations.PDController;
import com.pd.spring.framework.annotations.PDRequestMapping;
import com.pd.spring.framework.annotations.PDRequestParam;
import com.pd.spring.framework.webmvc.servlet.PDModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * 公布接口url
 * @author Tom
 *
 */
@PDController
@PDRequestMapping("/web")
public class MyAction {

	@PDAutowired
	IQueryService queryService;
	@PDAutowired
	IModifyService modifyService;

	@PDRequestMapping("/query.json")
	public PDModelAndView query(HttpServletRequest request, HttpServletResponse response,
								@PDRequestParam("name") String name){
		String result = queryService.query(name);
		return out(response,result);
	}
	
	@PDRequestMapping("/add*.json")
	public PDModelAndView add(HttpServletRequest request, HttpServletResponse response,
							  @PDRequestParam("name") String name, @PDRequestParam("addr") String addr){
		try {
			String result = modifyService.add(name, addr);
			return out(response,result);
		}catch (Throwable e){
			Map<String, String> model = new HashMap<>();
			model.put("detail",e.getCause().getMessage());
			model.put("stackTrace", Arrays.toString(e.getStackTrace()));
			return new PDModelAndView("500",model);
		}
	}
	
	@PDRequestMapping("/remove.json")
	public PDModelAndView remove(HttpServletRequest request, HttpServletResponse response,
                                 @PDRequestParam("id") Integer id){
		String result = modifyService.remove(id);
		return out(response,result);
	}
	
	@PDRequestMapping("/edit.json")
	public PDModelAndView edit(HttpServletRequest request, HttpServletResponse response,
                               @PDRequestParam("id") Integer id,
                               @PDRequestParam("name") String name){
		String result = modifyService.edit(id,name);
		return out(response,result);
	}
	
	
	
	private PDModelAndView out(HttpServletResponse resp, String str){
		try {
			resp.getWriter().write(str);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
