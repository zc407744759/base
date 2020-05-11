package com.wutos.base.common.handler;

/**
 * 自定义异常
 * @author zc
 * @date 2018/6/05
 */
public class WutosException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    /**
     * Exception响应码, 500: 服务器端错误, Exception信息见msg
     */
    private int code = 500;

    /**
     * Exception消息
     */
    private String msg;

    public WutosException(String msg) {
        super(msg);
        this.msg = msg;
    }

    public WutosException(String msg, Throwable e) {
        super(msg, e);
        this.msg = msg;
    }

    public WutosException(int code, String msg) {
        super(msg);
        this.code = code;
        this.msg = msg;
    }

    public WutosException(int code, String msg, Throwable e) {
        super(msg, e);
        this.code = code;
        this.msg = msg;
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
}
