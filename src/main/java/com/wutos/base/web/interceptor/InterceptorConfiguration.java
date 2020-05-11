package com.wutos.base.web.interceptor;

import com.wutos.base.common.util.ParsePaths;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class InterceptorConfiguration implements WebMvcConfigurer {

    @Value("${interceptor.cors.allowedOrigins}")
    private String allowedOrigins;

    @Value("${interceptor.cors.allowedMethods}")
    private String allowedMethods;

    public static Boolean redisEnable = true;

    public static String authorityHost;

    public static String tokenAuthUrl;

    public static String getUserInfoUrl;

    public static String getCurrentTime;

    public static String publicKeyUrl;


    public static String apikeyAuthenticateUrl;

    public static String apikeyGetUserInfo;

    @Value("${auth.authorityHost}")
    public void setAuthorityHost(String authorityHost) {
        InterceptorConfiguration.authorityHost = authorityHost;
    }

    @Value("${auth.tokenAuthUrl}")
    public void setTokenAuthUrl(String tokenAuthUrl) {
        InterceptorConfiguration.tokenAuthUrl = tokenAuthUrl;
    }

    @Value("${auth.getUserInfoUrl}")
    public void setGetUserInfoUrl(String getUserInfoUrl) {
        InterceptorConfiguration.getUserInfoUrl = getUserInfoUrl;
    }
    @Value("${auth.getCurrentTime}")
    public  void setGetCurrentTime(String getCurrentTime) {
        InterceptorConfiguration.getCurrentTime = getCurrentTime;
    }

    @Value("${auth.publicKeyUrl}")
    public void setPublicKeyUrl(String publicKeyUrl) {
        InterceptorConfiguration.publicKeyUrl = publicKeyUrl;
    }


    @Value("${apiKey.authenticate}")
    public void setApikeyAuthenticateUrl(String apikeyAuthenticateUrl) {
        InterceptorConfiguration.apikeyAuthenticateUrl = apikeyAuthenticateUrl;
    }

    @Value("${apiKey.userInfo}")
    public void setApikeyGetUserInfo(String apikeyGetUserInfo) {
        InterceptorConfiguration.apikeyGetUserInfo = apikeyGetUserInfo;
    }
    @Value("${interceptor.RedisTemplate.enable}")
    public static void setRedisEnable(Boolean redisEnable) {
        InterceptorConfiguration.redisEnable = redisEnable;
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // 设置跨域
        CorsRegistration cr = registry.addMapping("/**").maxAge(3600);
        String[] origins = ParsePaths.parsePathsToArray(allowedOrigins);
        if (origins != null && origins.length > 0) {
            cr.allowedOrigins(origins);
        } else {
            cr.allowedOrigins("*");
        }
        String[] methods = ParsePaths.parsePathsToArray(allowedMethods);
        if (methods != null && methods.length > 0) {
            cr.allowedMethods(methods);
        } else {
            cr.allowedMethods("*");
        }
    }
}
