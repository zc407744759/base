package com.wutos.base.common.protocol;


import com.alibaba.fastjson.JSONArray;
import com.wutos.base.common.handler.WutosException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsonReq{

    public static final long MAX_REQUEST_BODY_LENGTH = 8 * 1024 * 1024;

    public static final int PARSE_JSON = 0x00000001;

    public static final int PARSE_HTTP_PARAM = 0x00000002;

    public static final int PARSE_COOKIE = 0x00000004;

    public static final int BIN_RESPONSE = 0x00000008;

    public static final int DEFAULT_OPT = PARSE_HTTP_PARAM | PARSE_COOKIE | PARSE_JSON;

    public static final int BIN_OPTION = PARSE_COOKIE | PARSE_HTTP_PARAM | PARSE_JSON | BIN_RESPONSE;

    public static final String SECURITY_REQ = "SecurityReq";

    public static final String SECURITY_USER_INFO = "userInfo";

    /**
     * 请求环境对象
     */
//    private Env env;

    /**
     * 请求参数集合
     */
    private Map<String, Object> params = new HashMap<>();

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 用户昵称
     */
    private String surname;
    /**
     * 租户id
     */
    private Integer tenantId;

    /**
     * 用户角色Id列表：
     */
    private List<Long> roleIds;

    /**
     * 用户角色列表：
     */
    private List<Map<String, String>> roleList;


    /**
     * 用户组织机构列表：
     */
    private List<Map<String, String>> orgList;

//    public JsonReq(Env env) {
//        this.env = env;
//        this.params = new HashMap<String, Object>();
//        this.env.params = this.params;
//    }
    public JsonReq() {

    }

//    public Env getEnv() {
//        return env;
//    }
//
//    public void setEnv(Env env) {
//        this.env = env;
//    }

    public Map<String, Object> getParams() {
        return params;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public List<Map<String, String>> getRoleList() {
        return roleList;
    }

    public void setRoleList(List<Map<String, String>> roleList) {
        this.roleList = roleList;
    }

    public List<Long> getRoleIds() {
        return roleIds;
    }

    public void setRoleIds(List<Long> roleIds) {
        this.roleIds = roleIds;
    }


    public List<Map<String, String>> getOrgList() {
        return orgList;
    }

    public void setOrgList(List<Map<String, String>> orgList) {
        this.orgList = orgList;
    }

    public Integer getTenantId() {
        return tenantId;
    }

    public void setTenantId(Integer tenantId) {
        this.tenantId = tenantId;
    }

    /**
     * 获取客户端传递过来的Long类型参数 默认写到日志里
     *
     * @param name
     *            参数名
     * @param throwExceptionWhenValueIsEmpty
     *            是否为空
     * @param strConv
     *            是否从字符串中转换
     * @return
     * @throws WutosException
     */
    public Long paramGetNumber(String name, boolean throwExceptionWhenValueIsEmpty, boolean strConv) throws WutosException {
        return paramGetNumber(name, throwExceptionWhenValueIsEmpty, strConv, true);
    }

    /**
     * 获取客户端传递过来的Long类型参数
     *
     * @param name
     *            参数名
     * @param throwExceptionWhenValueIsEmpty
     *            是否为空
     * @param logParam
     *            是否写到日志里
     * @param strConv
     *            是否从字符串中转换
     * @return
     * @throws WutosException
     */
    public Long paramGetNumber(String name, boolean throwExceptionWhenValueIsEmpty, boolean strConv, boolean logParam) throws WutosException {
        Object o = this.params.get(name);
        if (logParam) {

        }
        if (o == null) {
            if (throwExceptionWhenValueIsEmpty) {
                throw new WutosException("Param '" + name + "' needed.");
            } else {
                return null;
            }
        }

        if ((o instanceof Long) || (o instanceof Integer) || (o instanceof Short)) {
            return Long.parseLong(String.valueOf(o));
        }

        if (strConv && (o instanceof String)) {
            try {
                return Long.parseLong((String) o);
            } catch (Exception e) {
                throw new WutosException("Param '" + name + "' should be number.");
            }
        }

        throw new WutosException("Param '" + name + "' should be number.");
    }

    /**
     * 获取客户端传递过来的整型参数 默认写到日志里
     *
     * @param name
     *            参数名
     * @param throwExceptionWhenValueIsEmpty
     *            是否为空
     * @return
     * @throws WutosException
     */
    public Integer paramGetInteger(String name, boolean throwExceptionWhenValueIsEmpty) throws WutosException {
        return paramGetInteger(name, throwExceptionWhenValueIsEmpty, true, Integer.class);
    }

    /**
     * 获取客户端传递过来的整型参数
     *
     * @param name
     *            参数名
     * @param throwExceptionWhenValueIsEmpty
     *            是否为空
     * @param logParam
     *            是否写到日志里
     * @param type
     *            指定参数类型
     * @return
     * @throws WutosException
     */
    public Integer paramGetInteger(String name, boolean throwExceptionWhenValueIsEmpty, boolean logParam, Class<?> type)
            throws WutosException {
        Object o = this.params.get(name);
        if (logParam) {

        }
        if (o == null) {
            if (throwExceptionWhenValueIsEmpty) {
                throw new WutosException("Param '" + name + "' needed.");
            } else {
                return null;
            }
        }

        if ((o instanceof Integer) || (o instanceof Short)) {
            return Integer.parseInt(o.toString());
        } else if (o instanceof String) {
            try {
                return Integer.parseInt(o.toString());
            } catch (Exception e) {
                throw new WutosException("Param '" + name + "' should be number.");
            }
        }

        throw new WutosException("Param '" + name + "' should be integer.");
    }

    /**
     * 获取客户端传递过来的Long类型参数
     *
     * @param name
     *            参数名
     * @param min
     *            允许的最大值
     * @param max
     *            允许的最小值
     * @param logParam
     *            是否写到日志里
     * @param strConv
     *            是否从字符串中转换
     * @return
     * @throws WutosException
     */
    public Long paramGetNumber(String name, long min, long max, boolean strConv, boolean logParam) throws WutosException {
        Long number = paramGetNumber(name, true, strConv, logParam);
        if (number < min || number > max) {
            throw new WutosException("Param '" + name + "' out of range");
        }
        return number;
    }

    /**
     * 获取double型参数 默认写日志
     *
     * @param name
     *            参数名
     * @param throwExceptionWhenValueIsEmpty
     *            是否允许为空
     * @param strConv
     *            是否从字符串中转换
     * @return
     * @throws WutosException
     */
    public Double paramGetDouble(String name, boolean throwExceptionWhenValueIsEmpty, boolean strConv) throws WutosException {
        return paramGetDouble(name, throwExceptionWhenValueIsEmpty, strConv, true);
    }

    /**
     * 获取double型参数
     *
     * @param name
     *            参数名
     * @param throwExceptionWhenValueIsEmpty
     *            是否允许为空
     * @param strConv
     *            是否从字符串中转换
     * @param logParam
     *            是否写日志
     * @return
     * @throws WutosException
     */
    public Double paramGetDouble(String name, boolean throwExceptionWhenValueIsEmpty, boolean strConv, boolean logParam)
            throws WutosException {
        Object o = this.params.get(name);
        if (logParam) {

        }
        if (o == null) {
            if (throwExceptionWhenValueIsEmpty) {
                throw new WutosException("Param '" + name + "' needed.");
            } else {
                return null;
            }
        }

        if ((o instanceof Double) || (o instanceof Float)) {
            return (Double) o;
        }

        if (strConv && (o instanceof String)) {
            try {
                return Double.parseDouble((String) o);
            } catch (Exception e) {
                throw new WutosException("Param '" + name + "' should be double.");
            }
        }

        throw new WutosException("Param '" + name + "' should be double.");
    }

    /**
     * 获取字符串型参数 默认写日志
     *
     * @param name
     *            参数名
     * @param throwExceptionWhenValueIsEmpty
     *            是否为空
     * @return
     * @throws WutosException
     */
    public String paramGetString(String name, boolean throwExceptionWhenValueIsEmpty) throws WutosException {
        return paramGetString(name, throwExceptionWhenValueIsEmpty, true);
    }

    /**
     * 获取字符串型参数
     *
     * @param name
     *            参数名
     * @param throwExceptionWhenValueIsEmpty
     *            是否为空
     * @param logParam
     *            是否写日志
     * @return
     * @throws WutosException
     */
    public String paramGetString(String name, boolean throwExceptionWhenValueIsEmpty, boolean logParam) throws WutosException {
        Object o = this.params.get(name);
        if (logParam) {

        }
        if (o == null) {
            if (throwExceptionWhenValueIsEmpty) {
                throw new WutosException("Param '" + name + "' needed.");
            } else {
                return null;
            }
        }

        if ((o instanceof String)) {
            String s = (String) o;
            if (throwExceptionWhenValueIsEmpty && s.length() == 0) {
                throw new WutosException("Param '" + name + "' should not be empty.");
            }
            return s;
        }

        throw new WutosException("Param '" + name + "' should be string.");
    }

    /**
     * 获取布尔类型参数 默认写日志
     *
     * @param name
     *            参数名
     * @param throwExceptionWhenValueIsEmpty
     *            是否为空
     * @param strConv
     *            是否从字符串转换
     * @return
     * @throws WutosException
     */
    public Boolean paramGetBoolean(String name, boolean throwExceptionWhenValueIsEmpty, boolean strConv) throws WutosException {
        return paramGetBoolean(name, throwExceptionWhenValueIsEmpty, strConv, true);
    }

    /**
     * 获取布尔类型参数
     *
     * @param name
     *            参数名
     * @param throwExceptionWhenValueIsEmpty
     *            是否为空
     * @param strConv
     *            是否从字符串转换
     * @param logParam
     *            是否写日志
     * @return
     * @throws WutosException
     */
    public Boolean paramGetBoolean(String name, boolean throwExceptionWhenValueIsEmpty, boolean strConv, boolean logParam)
            throws WutosException {
        Object o = this.params.get(name);
        if (logParam) {

        }
        if (o == null) {
            if (throwExceptionWhenValueIsEmpty) {
                throw new WutosException("Param '" + name + "' needed.");
            }

            return null;
        }

        if (o instanceof Boolean) {
            return (Boolean) o;
        }

        if (strConv && (o instanceof String)) {
            try {
                return Boolean.parseBoolean((String) o);
            } catch (Exception e) {
                throw new WutosException("Param '" + name + "' should be boolean.");
            }
        }

        throw new WutosException("Param '" + name + "' should be boolean.");
    }

    /**
     * 获取列表类型参数
     *
     * @param name
     *            参数名
     * @param throwExceptionWhenValueIsEmpty
     *            是否为空
     * @param type
     *            参数类型
     * @param logParam
     *            是否写日志
     * @return
     * @throws WutosException
     */
    public <T> List<T> paramGetList(String name, boolean throwExceptionWhenValueIsEmpty, Class<?> type, boolean logParam)
            throws WutosException {
        return paramGetList(name, throwExceptionWhenValueIsEmpty, throwExceptionWhenValueIsEmpty, type, logParam);
    }

    public <T> List<T> paramGetList(String name, boolean throwExceptionWhenValueIsNull, boolean throwExceptionWhenValueIsEmpty,
                                    Class<?> type, boolean logParam) throws WutosException {
        Object o = this.params.get(name);
        if (logParam) {

        }
        if (o == null) {
            if (throwExceptionWhenValueIsNull) {
                throw new WutosException("Param '" + name + "' needed.");
            } else {
                return new ArrayList<T>();
            }
        }

        if ((o instanceof List)) {
            String str = o.toString();

            try {
                @SuppressWarnings("unchecked")
                List<T> list = (List<T>) JSONArray.parseArray(str, type);
                if (throwExceptionWhenValueIsEmpty && list.size() == 0) {
                    throw new WutosException("Param '" + name + "', list should not be empty.");
                }

                for (@SuppressWarnings("unused")
                        T item : list)
                    ;

                return list;
            } catch (Exception e) {
                throw new WutosException("Param '" + name + "', list should contains " + type.getName() + " only.");
            }

        }

        throw new WutosException("Param '" + name + "' should be a list.");
    }

    /**
     * 获取列表类型参数，其元素为Long类型
     *
     * @param name
     *            参数名
     * @param throwExceptionWhenValueIsEmpty
     *            是否为空
     * @return
     * @throws WutosException
     */
    public List<Long> paramGetNumList(String name, boolean throwExceptionWhenValueIsEmpty) throws WutosException {
        return paramGetList(name, throwExceptionWhenValueIsEmpty, Long.class, true);
    }

    /**
     * 获取列表类型参数，其元素为Long类型 默认写日志
     *
     * @param name
     *            参数名
     * @param throwExceptionWhenValueIsEmpty
     *            是否为空
     * @param logParam
     *            是否写日志
     * @return
     * @throws WutosException
     */
    public List<Long> paramGetNumList(String name, boolean throwExceptionWhenValueIsNull, boolean throwExceptionWhenValueIsEmpty,
                                      boolean logParam) throws WutosException {
        return paramGetList(name, throwExceptionWhenValueIsNull, throwExceptionWhenValueIsEmpty, Long.class, logParam);
    }

    /**
     * 获取列表类型参数，其元素为String类型
     *
     * @param name
     *            参数名
     * @param throwExceptionWhenValueIsEmpty
     *            是否为空
     * @return
     * @throws WutosException
     */
    public List<String> paramGetStrList(String name, boolean throwExceptionWhenValueIsEmpty) throws WutosException {
        return paramGetList(name, throwExceptionWhenValueIsEmpty, String.class, true);
    }

    /**
     * 获取列表类型参数，其元素为String类型 默认写日志
     *
     * @param name
     *            参数名
     * @param throwExceptionWhenValueIsEmpty
     *            是否为空
     * @param logParam
     *            是否写日志
     * @return
     * @throws WutosException
     */
    public List<String> paramGetStrList(String name, boolean throwExceptionWhenValueIsNull, boolean throwExceptionWhenValueIsEmpty,
                                        boolean logParam) throws WutosException {
        return paramGetList(name, throwExceptionWhenValueIsNull, throwExceptionWhenValueIsEmpty, String.class, logParam);
    }

    /**
     * 获取列表类型参数，其元素为Double类型
     *
     * @param name
     *            参数名
     * @param throwExceptionWhenValueIsEmpty
     *            是否为空
     * @return
     * @throws WutosException
     */
    public List<Double> paramGetDblList(String name, boolean throwExceptionWhenValueIsEmpty) throws WutosException {
        return paramGetList(name, throwExceptionWhenValueIsEmpty, Double.class, true);
    }

    /**
     * 获取列表类型参数，其元素为Double类型 默认写日志
     *
     * @param name
     *            参数名
     * @param throwExceptionWhenValueIsEmpty 参数名
     * @param logParam
     *            是否写日志
     * @return
     * @throws WutosException
     */
    public List<Double> paramGetDblList(String name, boolean throwExceptionWhenValueIsNull, boolean throwExceptionWhenValueIsEmpty,
                                        boolean logParam) throws WutosException {
        return paramGetList(name, throwExceptionWhenValueIsNull, throwExceptionWhenValueIsEmpty, Double.class, logParam);
    }

    /**
     * 获取Map类型参数
     *
     * @param name
     *            参数名
     * @param throwExceptionWhenValueIsEmpty
     *            参数名
     * @param kt
     *            Map中键的类型
     * @param vt
     *            Map中值的类型
     * @param logParam
     *            是否写日志
     * @return
     * @throws WutosException
     */
    public <K, V> Map<K, V> paramGetMap(String name, boolean throwExceptionWhenValueIsEmpty, Class<K> kt, Class<V> vt, boolean logParam)
            throws WutosException {
        Object o = this.params.get(name);
        if (logParam) {

        }
        if (o == null) {
            if (throwExceptionWhenValueIsEmpty) {
                throw new WutosException("Param '" + name + "' needed.");
            } else {
                return new HashMap<K, V>();
            }
        }

        if ((o instanceof Map)) {
            @SuppressWarnings("unchecked")
            Map<K, V> map = (Map<K, V>) o;
            if (throwExceptionWhenValueIsEmpty && map.size() == 0) {
                throw new WutosException("Param '" + name + "', map should not be empty.");
            }

            return map;
        }

        throw new WutosException("Param '" + name + "' should be a map.");
    }

    /**
     * 获取Map类型参数，其中键值对为String类型
     *
     * @param name
     *            参数名
     * @param throwExceptionWhenValueIsEmpty
     *            参数名
     * @return
     * @throws WutosException
     */
    public Map<String, Object> paramGetStrMap(String name, boolean throwExceptionWhenValueIsEmpty) throws WutosException {
        return paramGetMap(name, throwExceptionWhenValueIsEmpty, String.class, Object.class, true);
    }

    /**
     * 获取Map类型参数，其中键值对为String类型
     *
     * @param name
     *            参数名
     * @param throwExceptionWhenValueIsEmpty
     *            参数名
     * @param logParam
     *            是否写日志
     * @return
     * @throws WutosException
     */
    public Map<String, Object> paramGetStrMap(String name, boolean throwExceptionWhenValueIsEmpty, boolean logParam) throws WutosException {
        return paramGetMap(name, throwExceptionWhenValueIsEmpty, String.class, Object.class, logParam);
    }

    /**
     * 获取参数
     *
     * @param name
     *            参数名
     * @param throwExceptionWhenValueIsEmpty
     *            是否允许为空
     * @param type
     *            参数的类型
     * @param logParam
     *            是否写日志
     * @return
     * @throws WutosException
     */
    public <T> T paramGetObject(String name, boolean throwExceptionWhenValueIsEmpty, Class<?> type, boolean logParam) throws WutosException {
        Object o = this.params.get(name);
        if (logParam) {

        }
        if (o == null) {
            if (throwExceptionWhenValueIsEmpty) {
                throw new WutosException("Param '" + name + "' needed.");
            } else {
                return null;
            }
        }

        @SuppressWarnings("unchecked")
        T obj = (T) JSONArray.parseObject(o.toString(), type);

        return obj;

    }
}
