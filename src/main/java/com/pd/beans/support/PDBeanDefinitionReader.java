package com.pd.beans.support;

import com.pd.beans.config.PDBeanDefinition;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * @author zhaozhengkang
 * @description
 * @date 2020-3-8 21:06
 */
public class PDBeanDefinitionReader {
    private Properties configContext = new Properties();
    private List<String> registryBeanClasses = new ArrayList<>();

    public PDBeanDefinitionReader(String locations){
        //1 定位并加载配置文件
        doLoadConfig(locations);
        //2 扫描
        doScanner(configContext.getProperty("scanPackage"));
        //3 解析配置，将配置文件内容解析为BeanDefinition

    }
    private void doLoadConfig(String contextConfigLocation){
        try(InputStream is = this.getClass().getClassLoader().
                getResourceAsStream(contextConfigLocation)){
            configContext.load(is);
        }catch (IOException e){
            e.getStackTrace();
        }
    }

    private void doScanner(String scanPackage) {
        URL url = this.getClass().getClassLoader().
                getResource("/" + scanPackage.replaceAll("\\.","/"));
        File classPath = new File(url.getFile());
        for (File f : classPath.listFiles()){
            //递归
            if(f.isDirectory()){
                doScanner(scanPackage + "." + f.getName());
            }else {
                if(!f.getName().endsWith(".class")){continue;}
                String className = scanPackage + "." + f.getName().replace(".class", "");
                registryBeanClasses.add(className);
            }
        }
    }

    public List<PDBeanDefinition> loadBeanDefinitions(){
        List<PDBeanDefinition> res = new ArrayList<>();
        try{
            for(String className : registryBeanClasses){
                Class<?> clazz = Class.forName(className);
                if(clazz.isInterface()){
                    continue;
                }
                // 默认名 类名首字母小写
                res.add(doCreateBeanDefinition(toFirstLowerCase(clazz.getSimpleName()),clazz.getName()));
                // 接口
                for (Class<?> iClazz : clazz.getInterfaces()){
                    res.add(doCreateBeanDefinition(iClazz.getName(),clazz.getName()));
                }
            }
        }catch (Exception e){
            e.getStackTrace();
        }
        return res;
    }
    private String toFirstLowerCase(String simpleName) {
        char[] chars = simpleName.toCharArray();
        chars[0] += 32;
        return String.valueOf(chars);
    }
    private PDBeanDefinition doCreateBeanDefinition(String factoryBeanName,String beanClassName){
        return new PDBeanDefinition(factoryBeanName,beanClassName);
    }
}
