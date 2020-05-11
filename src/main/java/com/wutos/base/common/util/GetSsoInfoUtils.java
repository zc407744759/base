package com.wutos.base.common.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wutos.base.common.handler.WutosException;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author wangzhe
 * @date 2018/11/26.
 */
public class GetSsoInfoUtils {
    //sso地址
    private String ssoIp;

    //sso端口
    private String ssoPort;

    //sso返回json对象
    private JSONObject jsonObject;

    //请求sso的路由
    private final String ssoRouter = "/AppCenter/ConfigInfo/";

    //sso转json对象的一级key名
    private final String s1 = "package";

    //sso转json对象的二级标识位key
    private final String s2 = "id";

    //sso转json对象的二级资源key
    private final String s3 = "resourceUrl";

    /**
     * 初始化sso配置
     * @param ssoIp
     * @param ssoPort
     * @throws Exception
     */
    public GetSsoInfoUtils(String ssoIp, String ssoPort) throws Exception {
        this.setSsoIp(ssoIp);
        this.setSsoPort(ssoPort);
        init();
    }

    private void init() throws Exception {
        String url = "http://" + ssoIp + ":" + ssoPort + ssoRouter;
        String result = requestMethod("GET",url);
        if (result.length() > 0){
            setJsonObject(JSON.parseObject(result));
        }else {
            throw new WutosException("获取sso数据失败！");
        }
    }

    /**
     * 获取package下诸如workflow、wesafe、dingding地址
     */
    public String getValue(String key){
        String result = null;
        if (key == null || key.length() < 1){
            throw new WutosException("读取sso参数错误！");
        }
        JSONArray packageArray = jsonObject.getJSONArray(s1);
        for (int i = 0; i < packageArray.size(); i++) {
            JSONObject obj = packageArray.getJSONObject(i);
            if (key.equals(obj.getString(s2))){
                result = obj.getString(s3);
                break;
            }
        }
        return result;
    }

    /**
     * 请求接口方法
     * @param type  请求类型 post get put delete
     * @param url   请求url
     * @return  response字符串
     * @throws Exception
     */
    private String requestMethod(String type,String url) throws Exception {
        URL restServiceURL = new URL(url);
        HttpURLConnection httpConnection = (HttpURLConnection) restServiceURL
                .openConnection();
        execRequest(httpConnection,type);
        BufferedReader responseBuffer = new BufferedReader(
                new InputStreamReader((httpConnection.getInputStream())));
        String output;
        StringBuffer result = new StringBuffer();
        while ((output = responseBuffer.readLine()) != null) {
            result.append(output);
        }
        httpConnection.disconnect();
        return result.toString();
    }

    /**
     * 判断返回结果
     * @param httpConnection
     * @param type
     * @throws Exception
     */
    private void execRequest(HttpURLConnection httpConnection,String type) throws Exception{
        httpConnection.setRequestMethod(type.toUpperCase());
        httpConnection.setRequestProperty("Content-Type", "application/json");
        if (httpConnection.getResponseCode() != 200 && httpConnection.getResponseCode() != 201) {
            throw new WutosException("请求sso信息失败！");
        }
    }

    public String getSsoIp() {
        return ssoIp;
    }

    public void setSsoIp(String ssoIp) {
        this.ssoIp = ssoIp;
    }

    public String getSsoPort() {
        return ssoPort;
    }

    public void setSsoPort(String ssoPort) {
        this.ssoPort = ssoPort;
    }

    public JSONObject getJsonObject() {
        return jsonObject;
    }

    public void setJsonObject(JSONObject jsonObject) {
        this.jsonObject = jsonObject;
    }
}
