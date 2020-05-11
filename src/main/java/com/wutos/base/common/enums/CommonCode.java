package com.wutos.base.common.enums;
/**
 * 封装通用返回码
 *
 * @author zc
 * @date 2018/6/05
 */
public enum CommonCode {

    /**
     * 成功
     */
    SUCCESS(0),

    /**
     * 参数错误
     */
    PARAM_ERROR(1,"参数错误"),

    /**
     * 内部错误
     */
    INNER_ERROR(2,"内部错误"),

    /**
     * 存储服务连接失败
     */
    FILE_SEAWEEDFS_CONNECT_FAILED(201, "存储服务连接失败"),

    /**
     * 存储服务连接失败
     */
    FILE_UPLOAD_SIZE_PERMITTED(202, "文件大小超过限制"),

    /**
     * 文件存储失败
     */
    FILE_UPLOAD_FAILED(203, "文件存储失败"),

    /**
     * 文件不存在
     */
    FILE_NOT_EXISTED(204, "文件不存在"),

    /**
     * 文件删除失败
     */
    FILE_DELETE_FAILED(205, "文件删除失败"),

    /**
     * 会话过期
     */
    SESSION_EXPIRE(3, "会话过期，请重新登录"),

    /**
     * 非法认证码
     */
    INVALID_TOKEN(401),

    /**
     * 权限不足
     */
    PERMISSION_DENIED(5,"权限不足");

    private Integer value;

    private String desc;

    CommonCode(Integer code) {
        this.value = code;
    }

    CommonCode(String desc) {
        this.desc = desc;
    }

    CommonCode(Integer code, String desc) {
        this.desc = desc;
        this.value = code;
    }

    public Integer getValue() {
        return value;
    }

    public String getDesc() {
        return desc;
    }

}
