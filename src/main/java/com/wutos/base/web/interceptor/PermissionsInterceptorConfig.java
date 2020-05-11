package com.wutos.base.web.interceptor;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.wutos.base.common.Annotation.PrivilegeInfo;
import com.wutos.base.domain.entity.Permission;
import com.wutos.base.common.enums.CommonCode;
import com.wutos.base.common.handler.WutosException;
import com.wutos.base.common.util.ParamsUtil;
import com.wutos.base.common.util.URLConnectionUtil;
import com.wutos.base.service.IPermissionService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.lang.Nullable;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.List;

/**
 * 权限控制的拦截器
 * @Author: ZouCong
 * @Date: 2018/6/29
 */
@SuppressWarnings("all")
public class PermissionsInterceptorConfig implements HandlerInterceptor {
    private static final Log logger = LogFactory.getLog(PermissionsInterceptorConfig.class);

    @Autowired
    private IPermissionService permissionService;
    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        response.setCharacterEncoding("UTF-8");
        String requestMethod = request.getMethod();
        if (requestMethod.equalsIgnoreCase(URLConnectionUtil.METHOD_TYPE_OPINION)) {
            return true;
        }
        String accessToken = null;
        if (request.getHeader("Authorization")!=null) {
            accessToken = request.getHeader("Authorization").substring(7);
        }
        if(accessToken==null){
            //说明accessToken在参数里面
            if (request.getParameter("Authorization")!=null) {
                accessToken = request.getParameter("Authorization").substring(7);
            }
        }
        String apiKey = request.getHeader("ApiKey");
        if(apiKey==null){
            apiKey = request.getParameter("ApiKey");
        }
        if((accessToken==null||"".equals(accessToken))&&(apiKey==null||"".equals(apiKey))){

           return false;
        }else if(apiKey!=null&&(accessToken==null||"".equals(accessToken))){

            //只要可以执行到这里  apikey都是合法的直接返回true
            return true;

        }

        if(handler instanceof HandlerMethod){
            logger.debug("获取到handler");
            HandlerMethod hm = (HandlerMethod) handler;
            //获得controller字节码对象
            Class<?> clazz = Class.forName(hm.getBeanType().getName());
            //获取方法对象
            Method method = hm.getMethod();
            if(clazz.getAnnotation(PrivilegeInfo.class)!=null){
                //获取class上的注解
                PrivilegeInfo annotationClass = clazz.getAnnotation(PrivilegeInfo.class);
                String[] privilege = annotationClass.name();
                String[] rely = {};
                //方法上有权限注解
                if(method.getAnnotation(PrivilegeInfo.class)!=null){
                    PrivilegeInfo annotationMethod = method.getAnnotation(PrivilegeInfo.class);
                    String[] privilegeMethod = annotationMethod.name();
                    rely = annotationMethod.relyOn();
                    for(int i=0; i < privilege.length; i++){
                        privilege[i] = privilege[i]+"."+privilegeMethod;
                    }
                }else {
                    // //方法上没有权限注解类上面有注解
                }

                //accesstoken的情况
                DecodedJWT jwt = JWT.decode(accessToken);
                Long deadline = jwt.getExpiresAt().getTime()-HandlerAuth.getSSOCurrentTime();
                List<Permission> privileges = HandlerAuth.getPremission(redisTemplate,request,accessToken,ParamsUtil.getJsonReq().getRoleIds(),deadline);
                    for (Permission permission:privileges) {
                        String permissionName = permission.getName();
                        if(privilege.length > 0){
                            for (String privi: privilege){
                                if(permissionName.contains(privi)){
                                    return true;
                                }
                            }
                        }
                        if(rely.length > 0){
                            for (String relyModal: rely){
                                if(permissionName.contains(relyModal)){
                                    return true;
                                }
                            }
                        }
                    }

                throw new WutosException(CommonCode.PERMISSION_DENIED.getValue(),CommonCode.PERMISSION_DENIED.getDesc());
            }

        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) throws Exception {

    }
}
