package com.wutos.base.web.advice;

import com.wutos.base.service.dto.RejectedEntityDeleting;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class InitValueAspect {

    Logger logger = LoggerFactory.getLogger(InitValueAspect.class);

    @Pointcut("execution(* com.wutos.*.web.controller.*.*(..))")
    public void pointcutName() {
    }

    /**
     * 初始化提示信息
     */
    @Before("pointcutName()")
    public void performance() {
        RejectedEntityDeleting.ClearValue();
    }

    /**
     * 删除校验失败抛异常。
     */
    @After("pointcutName()")
    public void doAfter() {
        if(!RejectedEntityDeleting.isValidated()){
            throw RejectedEntityDeleting.getCurrentInstance();
        }
    }
}
