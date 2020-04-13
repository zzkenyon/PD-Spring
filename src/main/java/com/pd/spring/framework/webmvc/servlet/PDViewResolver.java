package com.pd.spring.framework.webmvc.servlet;

import java.io.File;

/**
 * @author zhaozhengkang
 * @description
 * @date 2020/4/13 14:51
 */
public class PDViewResolver {
    private File templateRootDir;
    private final String DEFAULT_TEMPLATE_SUFFIX = ".html";

    public PDViewResolver(String templateRoot) {
        String templateRootPath = this.getClass().getClassLoader().getResource(templateRoot).getFile();
        templateRootDir = new File(templateRootPath);
    }

    public PDView resolveViewName(String viewName) {
        if(null == viewName || "".equals(viewName.trim())){
            return null;
        }
        viewName = viewName.endsWith(DEFAULT_TEMPLATE_SUFFIX)?viewName : (viewName + DEFAULT_TEMPLATE_SUFFIX);
        File templateFile = new File((templateRootDir.getPath() + "/" + viewName).replaceAll("/+","/"));
        return new PDView(templateFile);
    }
}
