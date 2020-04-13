package com.pd.spring.framework.beans.support;

import com.pd.spring.framework.beans.config.PDBeanDefinition;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * @author zhaozhengkang
 * @description 负责扫描类路径上的bean配置信息
 * @date 2020-3-8 21:06
 */
public class PDBeanDefinitionReader {
    /**
     * 配置文件位置
     */
    private Properties contextConfig = new Properties();
    private List<String> registryBeanClasses = new ArrayList<>();

    public Properties getConfig(){
        return this.contextConfig;
    }

    public PDBeanDefinitionReader(String ... configLocations){
        //1 定位并加载配置文件
        doLoadConfig(configLocations[0]);
        //2 扫描指定路径上的类，保存beanName
        doScanner(contextConfig.getProperty("scanPackage"));
    }

    private void doLoadConfig(String contextConfigLocation){
        try(InputStream is = this.getClass().getClassLoader().
                getResourceAsStream(contextConfigLocation)){
            contextConfig.load(is);
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
            for(String beanClassName : registryBeanClasses){
                Class<?> beanClazz = Class.forName(beanClassName);
                if(beanClazz.isInterface()){
                    continue;
                }
                // 1、默认名 类名首字母小写
                res.add(doCreateBeanDefinition(toFirstLowerCase(beanClazz.getSimpleName()),beanClazz.getName()));
                // 2、自定义

                // 3、接口
                for (Class<?> i : beanClazz.getInterfaces()){
                    res.add(doCreateBeanDefinition(i.getName(),beanClazz.getName()));
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
