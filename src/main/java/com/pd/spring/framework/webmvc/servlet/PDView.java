package com.pd.spring.framework.webmvc.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.RandomAccessFile;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author zhaozhengkang
 * @description
 * @date 2020/4/13 14:55
 */
public class PDView {

    private File viewFile;
    private static final Pattern PATTERN = Pattern.compile("￥\\{[^\\}]+\\}",Pattern.CASE_INSENSITIVE);

    public PDView(File templateFile) {
        this.viewFile = templateFile;
    }

    /**
     * 渲染
     * @param model
     * @param req
     * @param resp
     * @throws Exception
     */
    public void render(Map<String,?> model, HttpServletRequest req, HttpServletResponse resp) throws Exception{
        StringBuffer sb = new StringBuffer();
        RandomAccessFile ra = new RandomAccessFile(this.viewFile,"r");
        String line;
        while(null != (line = ra.readLine())){
            line = new String(line.getBytes("ISO-8859-1"),"UTF-8");
            Matcher matcher = PATTERN.matcher(line);
            while(matcher.find()){
                String paramName = matcher.group();
                paramName = paramName.replaceAll("￥\\{|\\}","");
                Object paramValue = model.get(paramName);
                line = matcher.replaceFirst(makeStringForRegExp(paramValue.toString()));
                matcher = PATTERN.matcher(line);
            }
            sb.append(line);
        }
        resp.setCharacterEncoding("utf-8");
        resp.getWriter().write(sb.toString());
    }

    public static String makeStringForRegExp(String str) {
        return str.replace("\\", "\\\\").replace("*", "\\*")
                .replace("+", "\\+").replace("|", "\\|")
                .replace("{", "\\{").replace("}", "\\}")
                .replace("(", "\\(").replace(")", "\\)")
                .replace("^", "\\^").replace("$", "\\$")
                .replace("[", "\\[").replace("]", "\\]")
                .replace("?", "\\?").replace(",", "\\,")
                .replace(".", "\\.").replace("&", "\\&");
    }
}
