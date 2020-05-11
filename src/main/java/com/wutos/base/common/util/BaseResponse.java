package com.wutos.base.common.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;

/**
 * 封装基础出参，业务类继承该类
 * @author zc
 * @date 2018/6/05
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BaseResponse<T> implements Serializable {
    private static final long serialVersionUID = 479242972783266948L;

    /**
     * 响应码, 0: 正常, 否则有异常, 异常信息见msg
     */
    private int code;

    /**
     * 响应消息
     */
    private String msg;

    /**
     * 业务成功, 封装响应实体数据
     */
    private T data;

    public BaseResponse() {
        this.code = 0;
        this.msg = "success";
    }

    /**
     * 异常构造器
     *
     * @param code
     * @param msg
     */
    public BaseResponse(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public BaseResponse(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    /**
     * 业务执行成功构造器
     *
     * @param data
     */
    public BaseResponse(T data) {
        this.code = 0;
        this.msg = "success";
        this.data = data;
    }

    public static BaseResponse getInstance() {
        return new BaseResponse();
    }

    public static <T> BaseResponse getInstance(T data) {
        return new BaseResponse(data);
    }

    public static BaseResponse getInstance(int code, String msg) {
        return new BaseResponse(code, msg);
    }

    public static <T> BaseResponse getInstance(int code, String msg, T data) {
        return new BaseResponse(code, msg, data);
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this, SerializerFeature.DisableCircularReferenceDetect);
    }
}
