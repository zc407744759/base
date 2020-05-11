package com.wutos.base;

import com.github.pagehelper.PageHelper;
import com.wutos.base.common.util.ConfigPath;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.core.convert.converter.Converter;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import javax.servlet.MultipartConfigElement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Properties;

/**
 * 将加载配置文件的方法封装
 *
 */
public abstract class App extends SpringBootServletInitializer
{
    public static ConfigPath configPath;
    public static void loadConfig(String[] args) {
        for (String s: args) {
            String[] split = s.split("=");
            if ("-h".equals(split[0])) {
                System.out.println("-d:是否使用默认配置路径【默认值true】");
                System.out.println("-db:指定公共配置中具体的数据库关键字【默认值：default】");
                System.out.println("-p:公共配置文件路径【全路径】");
                System.out.println("-s:项目私有配置文件路径【全路径】");
                return;
            }
            if("-db".equals(split[0])){
                if(!"".equals(split[1])&&split[1]!=null){
                    configPath.setDataSourceDB(split[1]);
                }
            }
            if("-d".equals(split[0])){
                if(!"".equals(split[1])&&split[1]!=null){
                    if("true".equals(split[1])){
                        configPath.setDefault(true);
                    }else {
                        configPath.setDefault(false);
                    }
                }else {
                    throw new SecurityException("请输入正确的默认值");
                }
            }
            if("-p".equals(split[0])){
                if(!"".equals(split[1])&&split[1]!=null){
                    configPath.setDefault(false);
                    configPath.setPubPath(split[1]);
                    continue;
                }else {
                    throw new SecurityException("请输入对应的配置文件路径：publicPath");
                }
            }
            if("-s".equals(split[0])){
                if(!"".equals(split[1])&&split[1]!=null){
                    configPath.setDefault(false);
                    configPath.setSelfPath(split[1]);
                    continue;
                }else {
                    throw new SecurityException("请输入对应的配置文件路径：selfPath");
                }
            }
        }
    }
    @Bean
    public PageHelper pageHelper(){
        PageHelper pageHelper = new PageHelper();
        Properties properties = new Properties();
        properties.setProperty("offsetAsPageNum","true");
        properties.setProperty("rowBoundsWithCount","true");
        properties.setProperty("reasonable","false");
        properties.setProperty("dialect","mysql");    //配置mysql数据库的方言
        pageHelper.setProperties(properties);
        return pageHelper;
    }
    /**
     * 跨域过滤器
     */
    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", buildConfig());
        return new CorsFilter(source);
    }

    private CorsConfiguration buildConfig() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
//        corsConfiguration.addAllowedOrigin("http://localhost:8001");
        corsConfiguration.addAllowedOrigin("*");
        corsConfiguration.addAllowedHeader("*");
        corsConfiguration.addAllowedMethod("*");
        return corsConfiguration;
    }

    /**
     * 文件上传配置
     */
    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        //设置临时上传路径问题
        factory.setLocation(System.getProperty("user.home"));
        //文件最大
        factory.setMaxFileSize("102400KB"); //KB,MB
        /// 设置总上传数据总大小
        factory.setMaxRequestSize("1024000KB");
        return factory.createMultipartConfig();
    }

    /**
     * ymk 2019/6/12
     * 前端日期格式转化
     */
//    @Bean
    public Converter<String, LocalDateTime> LocalDateTimeConvert() {

        return new Converter<String, LocalDateTime>() {

            private final DateTimeFormatter FORMATTER_16 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            private final DateTimeFormatter FORMATTER_19 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            @Override
            public LocalDateTime convert(String source) {

                if (source.length() == 0) {
                    return null;
                }

                if(source.contains("T")){
                    source = source.replace("T"," ");
                }

                LocalDateTime dateTime = null;

                if(source.length() == 16) {
                    dateTime = LocalDateTime.parse(source,FORMATTER_16);
                } else if(source.length() == 19) {
                    dateTime = LocalDateTime.parse(source,FORMATTER_19);
                }
                return dateTime;
            }
        };
    }
}
