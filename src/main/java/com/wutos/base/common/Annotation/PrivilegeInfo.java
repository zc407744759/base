package com.wutos.base.common.Annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于做权限控制的注解
 * @Author: ZouCong
 * @Date: 2018/6/29
 */


@Target({ElementType.TYPE,ElementType.METHOD})//注解的作用目标：类、方法
@Retention(RetentionPolicy.RUNTIME)//注解会在class字节码文件中存在，在运行时可以通过反射获取到
public @interface PrivilegeInfo {
    String[] name() default {};  //访问权限名称
    //如果访问当前类上的访问权限为boder1 但是当前用户只有boder2的权限，
    // 且用户是通过boder2的页面权限访问的boder1上的方法list，这个时候就需要标注list上的relyOn = boder2  这样就能访问list接口
    String[] relyOn() default {};
}
