//package com.wutos.base.web.config;
//
//import com.wutos.base.App;
//import com.wutos.base.common.handler.WutosException;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.boot.SpringApplication;
//import org.springframework.boot.env.EnvironmentPostProcessor;
//import org.springframework.core.Ordered;
//import org.springframework.core.env.ConfigurableEnvironment;
//import org.springframework.core.env.MutablePropertySources;
//import org.springframework.core.env.PropertiesPropertySource;
//
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.IOException;
//import java.util.Properties;
//
////@Component
//public class LoadPropertiesConfig implements EnvironmentPostProcessor, Ordered {
//    private static final Logger LOG = LoggerFactory.getLogger(LoadPropertiesConfig.class);
//    @Override
//    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
//        //此处可以http方式 到配置服务器拉取一堆公共配置+本项目个性配置的json串,拼到Properties里
//        //获取公共资源中的配置
////        Properties configProperties = new Properties();
//        //本地需要修改的配置
//        Properties selfProperties = new Properties();
//
//        Properties publicProperties = new Properties();
//
//        Properties localProperties = new Properties();
//
//        try {
//            File selfConfig = new File(App.configPath.getSelfPath());
//            File publicConfig = new File(App.configPath.getPubPath());
////            properties.load(application.getClass().getClassLoader().getResourceAsStream("publicConfige.properties"));
//            //先加载外部的自有配置文件
//            selfProperties.load(new FileInputStream(selfConfig));
//            for (String key : selfProperties.stringPropertyNames()) {
//                if (selfProperties.get(key) == null) {
//                    selfProperties.setProperty(key, "");
//                }
//            }
//            //存储私有配置文件的根目录
//            selfProperties.setProperty("selfRootPath",App.configPath.getSelfPath().replace("application.properties",""));
//            //加载公共配置到selfProperties
//            publicProperties.load(new FileInputStream(publicConfig));
//            localProperties.load(application.getClass().getClassLoader().getResourceAsStream("publicConfige.properties"));
//            for (String key : localProperties.stringPropertyNames()) {
//                if (localProperties.get(key) != null) {
//                    String db = App.configPath.getDataSourceDB();
//                    if ("spring.datasource.primary.jdbc-url".equals(key)) {
//                        String ip = publicProperties.getProperty(localProperties.getProperty(key));
//                        String port = publicProperties.getProperty(db+".rdb.port");
//                        String database = publicProperties.getProperty(db+".rdb.database");
//                        String url = "jdbc:mysql://" + ip + ":" + port + "/" + database + "?useUnicode=true&characterEncoding=utf-8&useSSL=false&allowMultiQueries=true";
//                        //将最终的key设置进selfProperties
//                        selfProperties.setProperty(key, url);
//                    }else if ("spring.datasource.primary.username".equals(key)) {
//                        String userName = publicProperties.getProperty(db+".rdb.username");
//                        selfProperties.setProperty(key, userName);
//                    }else if ("spring.datasource.primary.password".equals(key)) {
//                        String password = publicProperties.getProperty(db+".rdb.password");
//                        selfProperties.setProperty(key, password);
//                    }else if ("spring.datasource.primary.driver-class-name".equals(key)) {
//                        String password = publicProperties.getProperty(db+".rdb.driver-class-name");
//                        selfProperties.setProperty(key, password);
//                    }else if ("spring.datasource.alarm.jdbc-url".equals(key)) {
//                        String ip = publicProperties.getProperty(localProperties.getProperty(key));
//                        String port = publicProperties.getProperty("alarm.rdb.port");
//                        String database = publicProperties.getProperty("alarm.rdb.database");
//                        String url = "jdbc:mysql://" + ip + ":" + port + "/" + database + "?useUnicode=true&characterEncoding=utf-8&useSSL=false&allowMultiQueries=true";
//                        //将最终的key设置进selfProperties
//                        selfProperties.setProperty(key, url);
//                    }else if("seaweedFs.host".equals(key)){
//                        String host = publicProperties.getProperty(localProperties.getProperty(key));
//                        selfProperties.setProperty(key, "http://" + host);
//                    } else{
//                        selfProperties.setProperty(key, publicProperties.getProperty(localProperties.getProperty(key)));
//                    }
//                }
//            }
//        } catch (IOException e) {
//            LOG.error("error message", e);
//        }
//        //加载自有模块组件信息
//        //获取sso信息
//        String ssoIp = publicProperties.getProperty("sso.ip");
//        String ssoPort = publicProperties.getProperty("sso.port");
//        loadSsoInfo(ssoIp,ssoPort,selfProperties);
//
//        for (String key : selfProperties.stringPropertyNames()) {
//            System.out.println("####################" + key + "=" + selfProperties.getProperty(key));
//        }
//
//        MutablePropertySources propertySources = environment.getPropertySources();
//        //addLast 结合下面的 getOrder() 保证顺序
//        propertySources.addFirst(new PropertiesPropertySource("thirdEnv", selfProperties));
//    }
//
//    private void loadSsoInfo(String ssoIp, String ssoPort, Properties selfProperties){
//        selfProperties.setProperty("auth.authorityHost","http://" + ssoIp + ":" + ssoPort);
////        selfProperties.setProperty("wechat.loginUrl","http://" + ssoIp + ":" + ssoPort);
//        try {
////            GetSsoInfoUtils getSpringInitConfigUtils = new GetSsoInfoUtils(ssoIp,ssoPort);
////            selfProperties.setProperty("activiti.restUrl",getSpringInitConfigUtils.getValue("workflow"));
////            selfProperties.setProperty("ehs-front",getSpringInitConfigUtils.getValue("ehs"));
////            selfProperties.setProperty("wesafeWeb",getSpringInitConfigUtils.getValue("wesafewebapi"));
////            selfProperties.setProperty("dingtalkUrl",getSpringInitConfigUtils.getValue("dingtalkapi"));
////            selfProperties.setProperty("ehsweb",getSpringInitConfigUtils.getValue("ehswebapi"));
////            selfProperties.setProperty("eureka.client.service-url.defaultZone",getSpringInitConfigUtils.getValue("eurekaService"));
////            selfProperties.setProperty("seaweedFs.filerModel",getSpringInitConfigUtils.getValue("fileServerInnerHost"));
//        } catch (Exception e) {
//            e.printStackTrace();
//            throw new WutosException("读取sso信息失败！");
//        }
//    }
//
//    @Override
//    public int getOrder() {
//        //  +1 保证application.propertie里的内容能覆盖掉本配置文件中默认的
//        // 如果不想被覆盖 可以去掉 +1  或者 -1  试试
//        return Integer.MIN_VALUE;
//    }
//}
//
//
