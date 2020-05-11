package com.wutos.base.common.util;

import java.util.UUID;

/**
 * 生成uuid的工具类
 *
 * @Author: ZouCong
 * @Date: 2018/4/4
 */
public class UUIDUtils {

    public static String getUuidCode(){

        return UUID.randomUUID().toString().replace("-","");
    }
}
