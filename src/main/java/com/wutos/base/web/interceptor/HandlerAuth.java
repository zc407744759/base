package com.wutos.base.web.interceptor;

import com.alibaba.fastjson.JSONObject;
import com.wutos.base.domain.entity.Permission;
import com.wutos.base.common.enums.CommonCode;
import com.wutos.base.common.handler.WutosException;
import com.wutos.base.common.protocol.JsonReq;
import com.wutos.base.service.IPermissionService;
import com.wutos.base.common.util.AccessTokenParse;
import com.wutos.base.common.util.MD5Utils;
import com.wutos.base.common.util.ParamsUtil;
import com.wutos.base.common.util.URLConnectionUtil;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 用于认证apikey或者accessToken
 * @Author: ZouCong
 * @Date: 2018/12/19
 */
@SuppressWarnings("all")
public class HandlerAuth {
    public static Boolean  checkAccessToken(RedisTemplate redisTemplate, HttpServletRequest request, HttpServletResponse response, String accessToken) throws Exception{

        JSONObject payload = null;
        try {
            if(InterceptorConfiguration.redisEnable){
                payload = (JSONObject)redisTemplate.opsForValue().get(MD5Utils.getPwd(accessToken)+":payload");
            }
        } catch (Exception e) {
            InterceptorConfiguration.redisEnable = false;
            e.printStackTrace();
        }
        if(payload ==null){
            Map<String, String> headers = new HashMap<>();
            headers.put("Authorization","Bearer "+accessToken);
            JSONObject publicKey = URLConnectionUtil.sendGet(InterceptorConfiguration.authorityHost+InterceptorConfiguration.publicKeyUrl,headers);

            payload = AccessTokenParse.parse(accessToken, publicKey);
            if(payload==null){
                throw new WutosException("签名正常，负载为空,请检查token是否正常!");
            }
            Long expiresAt = payload.getLong("exp");
            Long deadLine = expiresAt-getSSOCurrentTime();
            if(deadLine>0){
                //验证通过  在有效期内
                //缓存到有效期
                JsonReq jsonReq = null;
                try {
                    if(InterceptorConfiguration.redisEnable){
                        redisTemplate.opsForValue().set(MD5Utils.getPwd(accessToken)+":payload",payload);
                        redisTemplate.expire(MD5Utils.getPwd(accessToken)+":payload",deadLine, TimeUnit.MILLISECONDS);
                        //获取用户数据
//                    JsonReq jsonReq = (JsonReq)Cache.get(accessToken);
                        jsonReq = (JsonReq)redisTemplate.opsForValue().get(MD5Utils.getPwd(accessToken)+":jsonReq");
                    }

                } catch (Exception e) {
                    InterceptorConfiguration.redisEnable = false;
                    e.printStackTrace();
                }
                return checkJsonReq(redisTemplate,request,response,accessToken,jsonReq,deadLine);
            }else {
                //清空缓存
                try {
                    if(InterceptorConfiguration.redisEnable){
                        redisTemplate.delete(MD5Utils.getPwd(accessToken)+":payload");
                        redisTemplate.delete(MD5Utils.getPwd(accessToken)+":jsonReq");
                    }
                } catch (Exception e) {
                    InterceptorConfiguration.redisEnable = false;
                    e.printStackTrace();
                }
                return false;
            }

        }else {
            //payload ！=null  在有效期内 直接获取用户信息
            Long expiresAt = payload.getLong("exp");
            Long deadLine = expiresAt-getSSOCurrentTime();
            if(deadLine>0){

                JsonReq jsonReq = null;
                try {
                    if(InterceptorConfiguration.redisEnable){
                        jsonReq = (JsonReq)redisTemplate.opsForValue().get(MD5Utils.getPwd(accessToken)+":jsonReq");
                    }

                } catch (Exception e) {
                    InterceptorConfiguration.redisEnable = false;
                    e.printStackTrace();
                }
                return checkJsonReq(redisTemplate,request,response,accessToken,jsonReq,deadLine);
            }else {
                //清空缓存
                try {
                    if(InterceptorConfiguration.redisEnable){
                        redisTemplate.delete(MD5Utils.getPwd(accessToken)+":payload");
                        redisTemplate.delete(MD5Utils.getPwd(accessToken)+":jsonReq");
                    }

                } catch (Exception e) {
                    InterceptorConfiguration.redisEnable = false;
                    e.printStackTrace();
                }
                return false;
            }



        }
    }

    public static Boolean checkApiKey(RedisTemplate redisTemplate,HttpServletRequest request, HttpServletResponse response,String apiKey) throws Exception{
        JSONObject result = URLConnectionUtil.send(InterceptorConfiguration.authorityHost + InterceptorConfiguration.apikeyAuthenticateUrl+"?apikey="+apiKey,
                URLConnectionUtil.METHOD_TYPE_GET, null, null);
        if (result!=null&&result.getJSONObject("result") != null && "true".equals(result.getString("success"))) {

            JsonReq jsonReq = null;
            try {
                if(InterceptorConfiguration.redisEnable){
                    jsonReq = (JsonReq) redisTemplate.opsForValue().get(MD5Utils.getPwd(apiKey)+":jsonReq");
                }

            } catch (Exception e) {
                InterceptorConfiguration.redisEnable = false;
                e.printStackTrace();
            }
            if(jsonReq!=null && jsonReq.getUserId()!=null){
                ParamsUtil.setJsonReq(jsonReq);
                return true;
            }else {
                ParamsUtil.getUserInfoByApiKey(request, response,InterceptorConfiguration.authorityHost + InterceptorConfiguration.apikeyGetUserInfo+"?apikey="+apiKey);

                jsonReq = ParamsUtil.getJsonReq();
                //apik的用户信息可以不需要清除 但这里一天清除一次
                try {
                    if(InterceptorConfiguration.redisEnable){
                        redisTemplate.opsForValue().set(MD5Utils.getPwd(apiKey)+":jsonReq",jsonReq);
                        redisTemplate.expire(MD5Utils.getPwd(apiKey)+":jsonReq",24, TimeUnit.HOURS);
                    }

                } catch (Exception e) {
                    InterceptorConfiguration.redisEnable = false;
                    e.printStackTrace();
                }
                return true;
            }
        }else {
            //验证失败
            try {
                if(InterceptorConfiguration.redisEnable){
                    redisTemplate.delete(MD5Utils.getPwd(apiKey)+":jsonReq");
                }

            } catch (Exception e) {
                InterceptorConfiguration.redisEnable = false;
                e.printStackTrace();
            }
            throw new WutosException(CommonCode.INVALID_TOKEN.getValue(),"未授权ApiKey");
        }
    }

    public static Boolean checkJsonReq(RedisTemplate redisTemplate,HttpServletRequest request, HttpServletResponse response,String jwt,JsonReq jsonReq,Long deadLine){
        ParamsUtil.setJsonReq(jsonReq);
        if(jsonReq!=null && jsonReq.getUserId()!=null){
            //获取权限
//            getPremission(redisTemplate,request,jwt,jsonReq.getRoleIds(),deadLine);
            return true;
        }else {
            String url = InterceptorConfiguration.authorityHost + InterceptorConfiguration.getUserInfoUrl;

            ParamsUtil.getUserInfoByToken(request,response,url);
            //缓存jsonReq
            jsonReq = ParamsUtil.getJsonReq();
            //获取权限
//            getPremission(redisTemplate,request,jwt,jsonReq.getRoleIds(),deadLine);
            try {
                if(InterceptorConfiguration.redisEnable){
                    redisTemplate.opsForValue().set(MD5Utils.getPwd(jwt)+":jsonReq",jsonReq);
                    redisTemplate.expire(MD5Utils.getPwd(jwt)+":jsonReq",deadLine, TimeUnit.MILLISECONDS);
                }

            } catch (Exception e) {
                InterceptorConfiguration.redisEnable = false;
                e.printStackTrace();
            }
            return true;
        }
    }

    //获取权限放在redis中
    public static List<Permission> getPremission(RedisTemplate redisTemplate,HttpServletRequest request,String jwt,List<Long> roleIds,Long deadline) {

        List<Permission> privileges = (List<Permission>)redisTemplate.opsForValue().get(MD5Utils.getPwd(jwt) + ":privileges");
        if(privileges==null||privileges.size()==0){
            BeanFactory factory = WebApplicationContextUtils.getRequiredWebApplicationContext(request.getServletContext());
            IPermissionService permissionService = (IPermissionService) factory.getBean("permissionService");
            privileges = permissionService.findPrivileges(roleIds);
            try {
                //在redis中缓存权限数据
                redisTemplate.opsForValue().set(MD5Utils.getPwd(jwt)+":privileges",privileges);
                redisTemplate.expire(MD5Utils.getPwd(jwt)+":privileges",deadline, TimeUnit.MILLISECONDS);

            } catch (Exception e) {
                InterceptorConfiguration.redisEnable = false;
                e.printStackTrace();
            }
            return privileges;
        }else {
            return privileges;
        }

    }
    public static Long getSSOCurrentTime(){
        JSONObject result = URLConnectionUtil.send(InterceptorConfiguration.authorityHost + InterceptorConfiguration.getCurrentTime,
                URLConnectionUtil.METHOD_TYPE_GET, null, null);
        if (result!=null&&result.getLong("timeStamp")!=null) {

            return result.getLong("timeStamp");
        }else {
            throw  new WutosException("获取当前sso服务器时间出错！");
        }
    }
}
