package com.wutos.base.common.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wutos.base.common.handler.WutosException;
import com.wutos.base.common.protocol.JsonReq;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParamsUtil {

    static Logger logger = LoggerFactory.getLogger(ParamsUtil.class);
    private  static final ThreadLocal<JsonReq> threadLocalMap = new ThreadLocal<JsonReq>();

    /**
     * 缓冲数组大小
     */
    public static final Integer BUFFER_BYTE_SIZE = 1024;

    static final String digits = "0123456789ABCDEF";

    public static JsonReq getJsonReq(){
        JsonReq jsonReq = threadLocalMap.get();
        if(jsonReq==null){
            jsonReq = new JsonReq();
            setJsonReq(jsonReq);
        }
        return jsonReq;
    }

    public static void setJsonReq(JsonReq jsonReq){

        threadLocalMap.set(jsonReq);
    }

    public static HttpServletRequest getHttpServletRequest() {
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
    }

    public static HttpServletResponse getHttpServletResponse() {
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
    }

    /**
     * 跟据 UserAgent 获取 Content-Disposition 响应头。 浏览器下载文件时，需要设置 Content-Disposition 响应头，才能正确显示文件名，但是， 不同的浏览器对 Content-Disposition
     * 的解释、编码不同，所以需要对 UserAgent 进行适配。
     *
     * @param userAgent
     *            浏览器 UserAgent
     * @param fileName
     *            UTF-8 编码的文件名
     * @return
     */
    public static String makeContDisp(String userAgent, String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return "attachment";
        }

        try {
            userAgent = userAgent.toLowerCase();
            // 部分android 加UTF-8 会出现乱码
            if (userAgent.indexOf("android") != -1) {
                return String.format("attachment; filename=\"%s\"", fileName);
            }
            if (userAgent.indexOf("msie") != -1 || userAgent.indexOf("trident") != -1) {
                // IE 6.7.8.9.10.11 Tested
                return String.format("attachment; filename=%s;", urlEncode(fileName));
            }
            if (userAgent.indexOf("chrome") != -1 || userAgent.indexOf("firefox") != -1) {
                // Chrome/Firefox Tested
                return String.format("attachment; filename*=UTF-8''%s", urlEncode(fileName));
            } else {
                // Safari/Android Tested
                return String.format("attachment; filename=\"%s\"", fileName);
            }
        } catch (Exception e) {
            return "attachment";
        }
    }
    /**
     * RFC 3986 URL Encode. 注意，这个不是 x-www-form-urlencoded，空格将编码成 %20，而不是 + 号。 "abc def" -> "abc%20def"
     *
     * @param s
     *            包含非ASCII字符、原始未编码的字符串
     * @return 编码后的字符串
     */
    public static String urlEncode(String s) {

        StringBuilder buf = new StringBuilder(s.length() + 16);
        for (int i = 0; i < s.length(); i++) {
            char ch = s.charAt(i);
            if ((ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z') || (ch >= '0' && ch <= '9') || ".-*_+".indexOf(ch) > -1) {
                buf.append(ch);
            } else {
                byte[] bytes = new String(new char[] { ch }).getBytes();
                for (int j = 0; j < bytes.length; j++) {
                    buf.append('%');
                    buf.append(digits.charAt((bytes[j] & 0xf0) >> 4));
                    buf.append(digits.charAt(bytes[j] & 0xf));
                }
            }
        }
        return buf.toString();
    }
    /**
     * 从HttpServletRequest对象中获取JsonReq对象，没有则返回null
     *
     * @Description
     * @author tiger
     * @return
     */

    public static JsonReq getJsonReqFromRequest() {

        JsonReq req = getJsonReq();

        return req;
    }

    /**
     * 根据输入流，获取byte数据
     *
     * @Description
     * @author tiger
     * @param in
     * @return
     * @throws IOException
     */
    public static byte[] getByteFromInputStream(InputStream in) throws IOException {
        byte[] buffer = new byte[BUFFER_BYTE_SIZE];
        int length = -1;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        while ((length = in.read(buffer)) != -1) {
            byteArrayOutputStream.write(buffer, 0, length);
        }
        return byteArrayOutputStream.toByteArray();
    }

    public static void getUserInfoByToken(HttpServletRequest request, HttpServletResponse response,String url) {
        JsonReq req = getJsonReq();
        if (req!=null&&req.getUserId()!=null) {
            return;
        }
        String authorization = (String)request.getHeader("Authorization");
        if(authorization == null){
            authorization =  request.getParameter("Authorization");
        }
        if (authorization != null) {
            Map<String, String> header = new HashMap<>();
            header.put("Authorization", authorization);
            JSONObject result = URLConnectionUtil.send(url,
                    URLConnectionUtil.METHOD_TYPE_GET, null, header);
            if (result != null && "true".equals(result.getString("success"))) {
                JSONObject userInfo = result.getJSONObject("result").getJSONObject("userInfo");
                if (userInfo != null) {
                    req.getParams().put(JsonReq.SECURITY_USER_INFO, userInfo);
                    // 用户信息
                    JSONObject userResult = userInfo.getJSONObject("userResult");
                    if (userResult != null) {
                        Long userId = userResult.getLong("id");
                        Integer tenantId = userResult.getInteger("tenantId");
                        String userName = userResult.getString("userName");
                        String emailAddress = userResult.getString("emailAddress");
                        String surname = userResult.getString("surname");
                        req.setUserId(userId);
                        if(tenantId!=null){
                            req.setTenantId(tenantId);
                        }
                        req.setUserName(userName);
                        req.setSurname(surname);
                        req.getParams().put("userEmailAddress", emailAddress);
                    }
                    // 用户角色信息
                    JSONArray roles = userInfo.getJSONArray("roleResult");
                    List<Map<String, String>> roleList = new ArrayList<>();
                    List<Long> roleIds = new ArrayList<>();
                    if (roles != null && roles.size() > 0) {
                        Map<String, String> userMap = null;
                        for (int i = 0; i < roles.size(); i++) {
                            JSONObject role = roles.getJSONObject(i);
                            Long roleId = role.getLong("roleId");
                            userMap = new HashMap<>();
                            userMap.put("roleId", roleId + "");
                            userMap.put("displayName", role.getString("displayName"));
                            roleList.add(userMap);
                            roleIds.add(roleId);
                        }
                    }
                    req.setRoleList(roleList);
                    req.setRoleIds(roleIds);
                    // 组织机构信息
                    JSONArray orgs = userInfo.getJSONArray("organizationUnitResult");
                    List<Map<String, String>> orgList = new ArrayList<>();
                    if (orgs != null && orgs.size() > 0) {
                        Map<String, String> userMap = null;
                        for (int i = 0; i < orgs.size(); i++) {
                            JSONObject role = orgs.getJSONObject(i);
                            userMap = new HashMap<>();
                            userMap.put("organizationId", role.getString("organizationUnitId"));
                            userMap.put("displayName", role.getString("displayName"));
                            userMap.put("code", role.getString("code"));
                            userMap.put("parentId", role.getString("parentId"));
                            orgList.add(userMap);
                        }
                    }
                    req.setOrgList(orgList);
                }
            }else {
                throw new WutosException("通过token获取用户数据失败！");
            }

        }
    }

    public static void getUserInfoByApiKey(HttpServletRequest request, HttpServletResponse response,String url) {
        JsonReq req = getJsonReq();
        if (req !=null && req.getUserId() != null) {
            return;
        }
        String apiKey = request.getHeader("ApiKey");
        if(apiKey==null){
            apiKey = request.getParameter("ApiKey");
        }
        if (apiKey != null) {
            Map<String, String> header = new HashMap<>();
            header.put("api-key", apiKey);
//            JSONObject params = new JSONObject();
//            params.put("apikey", apiKey);

            JSONObject result = URLConnectionUtil.send(url,
                    URLConnectionUtil.METHOD_TYPE_GET, null, header);
            if (result.getJSONObject("result") != null && "true".equals(result.getString("success"))) {
                JSONObject userInfo = result.getJSONObject("result").getJSONObject("user");
                if (userInfo != null) {
                    req.getParams().put(JsonReq.SECURITY_USER_INFO, userInfo);
                    // 用户信息
                    if (userInfo != null) {
                        Long userId = userInfo.getLong("id");
                        String userName = userInfo.getString("userName");
                        String emailAddress = userInfo.getString("emailAddress");
                        String surname = userInfo.getString("surname");
                        Integer tenantId = userInfo.getInteger("tenantId");
                        req.setUserId(userId);
                        req.setUserName(userName);
                        req.setSurname(surname);
                        req.setTenantId(tenantId);
                        req.getParams().put("userEmailAddress", emailAddress);
                    }
                }
                // 用户角色信息
                JSONArray roles = result.getJSONObject("result").getJSONArray("role");
                List<Map<String, String>> roleList = new ArrayList<>();
                List<Long> roleIds = new ArrayList<>();
                if (roles != null && roles.size() > 0) {
                    Map<String, String> userMap = null;
                    for (int i = 0; i < roles.size(); i++) {
                        JSONObject role = roles.getJSONObject(i);
                        Long roleId = role.getLong("roleId");
                        userMap = new HashMap<>();
                        userMap.put("roleId", roleId + "");
                        userMap.put("displayName", role.getString("displayName"));
                        roleList.add(userMap);
                        roleIds.add(roleId);
                    }
                }
                req.setRoleList(roleList);
                req.setRoleIds(roleIds);
                // 组织机构信息
                JSONArray orgs = result.getJSONObject("result").getJSONArray("organizationUnit");
                List<Map<String, String>> orgList = new ArrayList<>();
                if (orgs != null && orgs.size() > 0) {
                    Map<String, String> userMap = null;
                    for (int i = 0; i < orgs.size(); i++) {
                        JSONObject role = orgs.getJSONObject(i);
                        userMap = new HashMap<>();
                        userMap.put("organizationId", role.getString("organizationUnitId"));
                        userMap.put("displayName", role.getString("displayName"));
                        userMap.put("code", role.getString("code"));
                        userMap.put("parentId", role.getString("parentId"));
                        orgList.add(userMap);
                    }
                }
                req.setOrgList(orgList);
            }else {
                throw new WutosException("通过ApiKey获取用户数据失败！");
            }

        }
    }

    public static String filterOfficeFullName(String officeAreaName){
        String areaName = officeAreaName;
        if (areaName != null) {
            String[] strings = officeAreaName.split("-");
            if (strings.length == 2){
                String s1 = strings[0];
                String s2 = strings[1];
                if (s2.contains(s1)){
                    areaName = s2;
                }
            }
        }
        return areaName;
    }


}
