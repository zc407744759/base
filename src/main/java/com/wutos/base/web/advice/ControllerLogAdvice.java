package com.wutos.base.web.advice;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Description: Controller的日志打印
 * @Author: ZouCong
 * @Date: 2018/9/27
 */
@Aspect
@Component
public class ControllerLogAdvice {
    private final static Logger logger = LoggerFactory.getLogger(ControllerLogAdvice.class);

    /**
     * @Description: 请求日志
     * @Param: []
     * @Return: void
     * @Author: ZouCong
     * @date: 2018/5/29 10:57
     */
    @Pointcut("execution(public * com.wutos.*.web.controller.*.*(..))")
    public void controllerLog() {
    }

    /**
     * @Description: 前置
     * @Param: []
     * @Return: void
     * @Author: ZouCong
     * @date: 2018/5/29 10:57
     */
    @Before("controllerLog()")
    public void doHttpBeforeLog(JoinPoint joinPoint) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        //记录请求信息(使用时候取消注释)
        logger.info("RequestTime={} IP={} Method={} Url={} Args={} Class_Method={}", formatDate(System.currentTimeMillis()), request.getRemoteAddr(), request.getMethod(), request.getRequestURL(), joinPoint.getArgs(), joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName());
    }

    /**
     * @Description: 相应记录, 在正常返回后记录数据
     * @Param: [object]
     * @Return: void
     * @Author: ZouCong
     * @date: 2018/6/12 13:44
     */
//    @AfterReturning(returning = "object", pointcut = "controllerLog()")
//    public void doHttpAterReturning(Object object) {
//        //防止object为null,调用tostring方法时空指针;
//        if (StringUtils.isEmpty(object)) {
//            logger.info("ResponseTime={} Response={}", formatDate(System.currentTimeMillis()), "Return object is empty!");
//        } else {
//            logger.info("ResponseTime={} Response={}", formatDate(System.currentTimeMillis()), object.toString());
//        }
//    }

    @AfterThrowing(pointcut = "controllerLog()", throwing = "e")
    public void afterThrowing(Throwable e) {
        logger.error("异常打印", e);
    }


    private String formatDate(Long currentTimeMillis){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS)");

        return  simpleDateFormat.format(new Date(currentTimeMillis));
    }

}
