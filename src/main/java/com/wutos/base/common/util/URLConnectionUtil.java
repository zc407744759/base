package com.wutos.base.common.util;

import com.alibaba.fastjson.JSONObject;
import com.wutos.base.common.enums.CommonCode;
import com.wutos.base.common.handler.WutosException;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("all")
public class URLConnectionUtil {

    public static Logger logger = LoggerFactory.getLogger("URLConnectionUtil");

    public static final String module = "URLConnectionUtil";

    public static final String METHOD_TYPE_GET = "GET";

    public static final String METHOD_TYPE_POST = "POST";

    public static final String METHOD_TYPE_OPINION = "OPTIONS";

    public static JSONObject send(String url, String methodType, byte[] param, Map<String, String> headers) {
        String result = "";
        try {
            HttpURLConnection httpConn = null;

            httpConn = getConnection(url);
            httpConn.setDoOutput(true);
            httpConn.setDoInput(true);
            httpConn.setConnectTimeout(20000);
            if (methodType == null || methodType == "") {
                httpConn.setRequestMethod(METHOD_TYPE_GET);
            } else {
                httpConn.setRequestMethod(methodType);
            }
            if (null != headers) {
                Set<String> keys = headers.keySet();
                for (String key : keys) {
                    String value = headers.get(key);
                    httpConn.addRequestProperty(key, value);
//                    System.out.println(key+"    :"+value);
                }
                httpConn.addRequestProperty("Accept","*/*");
                httpConn.addRequestProperty("Cache-Control","no-cache");
                httpConn.addRequestProperty("Content-Type","application/json; charset=utf-8");
            }
            if (METHOD_TYPE_POST.equals(methodType) && null == param) {
                param = new byte[0];
            }
            if (null != param) {
                OutputStream writer = httpConn.getOutputStream();
                writer.write(param);
                writer.flush();
                writer.close();
            }

            BufferedReader in = new BufferedReader(new InputStreamReader(httpConn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
            in.close();
            return JSONObject.parseObject(result);
        } catch (Exception e) {
            // log 
            e.printStackTrace();
        }
        return null;
    }

    public static byte[] getByte(String url, String methodType, byte[] param, Map<String, String> headers) {
        byte[] result = null;
        try {
            HttpURLConnection httpConn = null;
            httpConn = getConnection(url);
            httpConn.setDoOutput(true);
            httpConn.setDoInput(true);
            httpConn.setConnectTimeout(20000);
            if (methodType == null || methodType == "") {
                httpConn.setRequestMethod(METHOD_TYPE_GET);
            } else {
                httpConn.setRequestMethod(methodType);
            }
            if (null != headers) {
                Set<String> keys = headers.keySet();
                for (String key : keys) {
                    String value = headers.get(key);
                    httpConn.addRequestProperty(key, value);
                }
            }
            if (METHOD_TYPE_POST.equals(methodType) && null == param) {
                param = new byte[0];
            }
            if (null != param) {
                OutputStream writer = httpConn.getOutputStream();
                writer.write(param);
                writer.flush();
                writer.close();
            }
            InputStream in = httpConn.getInputStream();
            result = ParamsUtil.getByteFromInputStream(in);
            in.close();
        } catch (Exception e) {
            LoggerFactory.getLogger("module").error(e.getMessage(), e);
        }
        return result;
    }

    private static HttpURLConnection getConnection(String baseUrl) throws Exception {

        HttpURLConnection urlConnection = null;

        if (baseUrl.startsWith("https")) {
            SSLContext sslcontext = SSLContext.getInstance("SSL", "SunJSSE");
            sslcontext.init(null, new TrustManager[] { new X509TrustManager() {

                @Override
                public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {

                }

                @Override
                public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {

                }

                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
            } }, new java.security.SecureRandom());
            URL url = new URL(baseUrl);
            HostnameVerifier ignoreHostnameVerifier = new HostnameVerifier() {

                public boolean verify(String s, SSLSession sslsession) {
                    return true;
                }
            };
            HttpsURLConnection.setDefaultHostnameVerifier(ignoreHostnameVerifier);
            HttpsURLConnection.setDefaultSSLSocketFactory(sslcontext.getSocketFactory());

            urlConnection = (HttpURLConnection) url.openConnection();
        } else {

            URL hUserUrl = new URL(baseUrl);
            urlConnection = (HttpURLConnection) hUserUrl.openConnection();
        }

        return urlConnection;
    }

    /**
     * 上传文件到指定URL
     * 
     * @param file
     * @param url
     * @return
     * @throws IOException
     */
    public static String doUploadFile(File file, String url) throws IOException {
        String response = "";
        if (!file.exists()) {
            return "file not exists";
        }
        PostMethod postMethod = new PostMethod(url);
        try {
            FilePart fp = new FilePart("file", file);
            Part[] parts = { fp };
            MultipartRequestEntity mre = new MultipartRequestEntity(parts, postMethod.getParams());
            postMethod.setRequestEntity(mre);
            HttpClient client = new HttpClient();
            // 由于要上传的文件可能比较大 , 因此在此设置最大的连接超时时间
            client.getHttpConnectionManager().getParams().setConnectionTimeout(50000);
            int status = client.executeMethod(postMethod);
            if (status == HttpStatus.SC_OK) {
                InputStream inputStream = postMethod.getResponseBodyAsStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
                StringBuffer stringBuffer = new StringBuffer();
                String str = "";
                while ((str = br.readLine()) != null) {
                    stringBuffer.append(str);
                }
                response = stringBuffer.toString();
            } else {
                response = "fail";
            }
        } catch (Exception e) {
            postMethod.releaseConnection();
            e.printStackTrace();
        } finally {
            postMethod.releaseConnection();
        }
        return response;
    }

    /**
     * 模拟form表单的形式 ，上传文件 以输出流的形式把文件写入到url中，然后用输入流来获取url的响应
     * 
     * @param url
     *            请求地址 form表单url地址
     * @param filePath
     *            文件在服务器保存路径
     * @return String url的响应信息返回值a
     * @throws IOException
     */
    public static JSONObject sendPost(String url, Map<String,Object> params,Map<String,String> headers,String type) {
        String result = null;
        URL urlObj = null;
        HttpPost httpPost = new HttpPost(url);
        CloseableHttpResponse rsp = null;
        try {
            if(type==null){
                List<NameValuePair> formparams = new ArrayList<NameValuePair>();
                for (String key:params.keySet()) {
                    formparams.add(new BasicNameValuePair(key, String.valueOf(params.get(key))));
                }
                UrlEncodedFormEntity uefEntity;
                uefEntity = new UrlEncodedFormEntity(formparams, "UTF-8");
                httpPost.setEntity(uefEntity);
            }else if("application/json".equals(type)){
                String content = "{";
                for (String key:params.keySet()) {
                    content +="\""+key+"\""+":"+ "\""+String.valueOf(params.get(key))+"\"";
                    content += ",";
                }
                if(content.contains(",")){
                    content =content.substring(0,content.length()-1)+ "}";
                }

                StringEntity se = new StringEntity(content,Charset.forName("UTF-8"));
                se.setContentEncoding("UTF-8");
                //设置数据类型
                se.setContentType("application/json");
                httpPost.addHeader("Content-type","application/json; charset=utf-8");

                httpPost.setHeader("Accept", "application/json");
                httpPost.setEntity(se);
            }
            if (null != headers) {
                Set<String> keys = headers.keySet();
                for (String key : keys) {
                    httpPost.setHeader(key, (String)headers.get(key));
                }
            }
            rsp = CommonUtils.getHttpClient().execute(httpPost);
            HttpEntity entity = rsp.getEntity();
            result = EntityUtils.toString(entity, "UTF-8");
            if(result==null&&"".equals(result)){
                return null;
            }
        }catch (Exception e){
            logger.error(CommonCode.INVALID_TOKEN.getDesc(), e);
            CommonUtils.releaseConnection();
            throw new WutosException(CommonCode.INVALID_TOKEN.getValue(), CommonCode.INVALID_TOKEN.getDesc());
        } finally {
            responseClose(rsp, null);
        }
        return JSONObject.parseObject(result);
    }


    public static JSONObject sendGet(String url,Map<String,String> headers) {
        String result = null;
        URL urlObj = null;
        HttpGet httpGet = new HttpGet(url);
        CloseableHttpResponse rsp = null;
        if (null != headers) {
            Set<String> keys = headers.keySet();
            for (String key : keys) {
                httpGet.setHeader(key, (String)headers.get(key));
            }
        }
        try {
            rsp = CommonUtils.getHttpClient().execute(httpGet);
            HttpEntity entity = rsp.getEntity();
            result = EntityUtils.toString(entity, "UTF-8");
            if(result==null&&"".equals(result)){
                return null;
            }
        }catch (Exception e){
            logger.error(CommonCode.INVALID_TOKEN.getDesc(), e);
            CommonUtils.releaseConnection();
            throw new WutosException(CommonCode.INVALID_TOKEN.getValue(), CommonCode.INVALID_TOKEN.getDesc());
        } finally {
            responseClose(rsp, null);
        }
        return JSONObject.parseObject(result);
    }

    public static JSONObject sendPut(String url, Map<String,Object> params,Map<String,String> headers,String type) {
        String result = null;
        URL urlObj = null;
        HttpPut httpPut = new HttpPut(url);
        CloseableHttpResponse rsp = null;
        try {
            if(type==null){
                List<NameValuePair> formparams = new ArrayList<NameValuePair>();
                for (String key:params.keySet()) {
                    formparams.add(new BasicNameValuePair(key, String.valueOf(params.get(key))));
                }
                UrlEncodedFormEntity uefEntity;
                uefEntity = new UrlEncodedFormEntity(formparams, "UTF-8");
                httpPut.setEntity(uefEntity);
            }else if("application/json".equals(type)){
                String content = "{";
                for (String key:params.keySet()) {
                    content +="\""+key+"\""+":"+ "\""+String.valueOf(params.get(key))+"\"";
                    content += ",";
                }
                if(content.contains(",")){
                    content =content.substring(0,content.length()-1)+ "}";
                }

                StringEntity se = new StringEntity(content,Charset.forName("UTF-8"));
                se.setContentEncoding("UTF-8");
                //设置数据类型
                se.setContentType("application/json");
                httpPut.addHeader("Content-type","application/json; charset=utf-8");

                httpPut.setHeader("Accept", "application/json");

                httpPut.setEntity(se);
            }
            if (null != headers) {
                Set<String> keys = headers.keySet();
                for (String key : keys) {
                    httpPut.setHeader(key, (String)headers.get(key));
                }
            }
            rsp = CommonUtils.getHttpClient().execute(httpPut);
            HttpEntity entity = rsp.getEntity();
            result = EntityUtils.toString(entity, "UTF-8");
            if(result==null&&"".equals(result)){
                return null;
            }
        }catch (Exception e){
            logger.error(CommonCode.INVALID_TOKEN.getDesc(), e);
            CommonUtils.releaseConnection();
            throw new WutosException(CommonCode.INVALID_TOKEN.getValue(), CommonCode.INVALID_TOKEN.getDesc());
        } finally {
            responseClose(rsp, null);
        }
        return JSONObject.parseObject(result);
    }

    public static JSONObject sendDelete(String url,Map<String,String> headers) {
        String result = null;
        URL urlObj = null;
        HttpDelete httpDelete = new HttpDelete(url);
        if (null != headers) {
            Set<String> keys = headers.keySet();
            for (String key : keys) {
                httpDelete.setHeader(key, (String)headers.get(key));
            }
        }
        try {
            CloseableHttpResponse rsp = CommonUtils.getHttpClient().execute(httpDelete);
            HttpEntity entity = rsp.getEntity();
            result = EntityUtils.toString(entity, "UTF-8");
            if(result==null&&"".equals(result)){
                return null;
            }
            responseClose(rsp, null);
        }catch (Exception e){
            logger.error(CommonCode.INVALID_TOKEN.getDesc(), e);
            CommonUtils.releaseConnection();
            throw new WutosException(CommonCode.INVALID_TOKEN.getValue(), CommonCode.INVALID_TOKEN.getDesc());
        }
        return JSONObject.parseObject(result);
    }

    public static void responseClose(CloseableHttpResponse response, InputStream inputStream) {
        try {
            if (inputStream != null)
                inputStream.close();
            if (response != null) {
                response.getEntity().getContent().close();
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }
}
