package com.wutos.base.web.interceptor;

import com.wutos.base.common.enums.CommonCode;
import com.wutos.base.common.handler.WutosException;
import com.wutos.base.common.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@SuppressWarnings("all")
public class AuthInterceptorConfig implements HandlerInterceptor, Ordered {

    @Autowired
    private RedisTemplate redisTemplate;
    @Override
    public int getOrder() {
        return 0;
    }

    /**
     * 进入controller层之前拦截请求
     * 
     * @param request
     * @param response
     * @param o
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object o) throws Exception {
        String method = request.getMethod();
        if (method.equalsIgnoreCase(URLConnectionUtil.METHOD_TYPE_OPINION)) {
            return true;
        }
        if (tokenAuthority(request, response)) {

            return true;
        }
        sendResponse(response, BaseResponse.getInstance(CommonCode.SESSION_EXPIRE.getValue(), CommonCode.SESSION_EXPIRE.getDesc()));
        return false;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object o, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object o, Exception e) throws Exception {
        ParamsUtil.setJsonReq(null);
    }

    protected  void sendResponse(HttpServletResponse response, BaseResponse<?> baseResponse) {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=utf-8");
        PrintWriter out = null;
        try {
            out = response.getWriter();
            out.append(baseResponse.toString());
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }


    public boolean tokenAuthority(HttpServletRequest request, HttpServletResponse response) throws Exception{

        String accessToken = null;
        if (request.getHeader("Authorization")!=null &&!"".equals(request.getHeader("Authorization"))) {
            accessToken = request.getHeader("Authorization").substring(7);
        }
        if(accessToken==null){
            //说明accessToken在参数里面
            if (request.getParameter("Authorization")!=null &&!"".equals(request.getParameter("Authorization"))) {
                accessToken = request.getParameter("Authorization").substring(7);
            }
        }
        String apiKey = request.getHeader("ApiKey");
        if(apiKey==null){
            apiKey = request.getParameter("ApiKey");
        }
        if((accessToken==null||"".equals(accessToken))&&(apiKey==null||"".equals(apiKey))){

            throw new WutosException(CommonCode.INVALID_TOKEN.getValue(),"未授权用户");

        }else if(apiKey!=null){
            //其他系统的接口请求
            //1  验证apikey是否有效
            return HandlerAuth.checkApiKey(redisTemplate,request,response,apiKey);

        }else if(accessToken!=null&&(apiKey==null||"".equals(apiKey))){
            //sigraph系统这边的登录用户
            return  HandlerAuth.checkAccessToken(redisTemplate,request,response,accessToken);

        }else {
            return HandlerAuth.checkApiKey(redisTemplate,request,response,apiKey);
        }

    }

}
