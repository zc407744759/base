package com.wutos.base.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wutos.base.common.handler.WutosException;
import com.wutos.base.domain.entity.SeaweedFsUploadResponseEntity;
import com.wutos.base.service.IHttpClientPoolService;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.message.BasicHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.Charset;

@Service
public class FileServer extends IHttpClientPoolService {

    @Value("${seaweedFs.host}")
    public String master;

    @Value("${seaweedFs.port}")
    public String fdb_port;

    public String uploadUrl;


    public FileServer() {
    }

    private final static Logger logger = LoggerFactory.getLogger(FileServer.class);

    @Autowired
    private ObjectMapper mapper;

    public void getFileToResponce(String url, HttpServletResponse response) throws IOException {
        try (InputStream fileStream = getFileStream(url)) {
            IOUtils.copyLarge(fileStream, response.getOutputStream());
        }
    }

    /**
     * 测试文件下载统用接口
     * @param url
     * @param response
     * @throws IOException
     */
    public void getFileToResponce1(String url, HttpServletResponse response) throws IOException {
        try (InputStream fileStream = getFileStream1(url,response)) {
            IOUtils.copyLarge(fileStream, response.getOutputStream());
        }
    }

    public InputStream getFileStream(String url) {
        HttpResponse fileResponse = null;
        HttpGet httpGet = null;
        if (!url.startsWith(this.master)) {
            url = this.master + ":" + fdb_port + "/" + url;
        }
        logger.debug("class:" + "{}   " + "method:{}    " + "params:{}    ", this.getClass().getName(), Thread.currentThread().getStackTrace()[1].getMethodName(), new Object[]{"url:" + url});
        logger.info("class:" + "{}   " + "method:{}    " + "params:{}    ", this.getClass().getName(), Thread.currentThread().getStackTrace()[1].getMethodName(), new Object[]{"url:" + url});
        try {
            httpGet = new HttpGet(url);

            fileResponse = getHttpClient().execute(httpGet);

            if (fileResponse == null) {
                throw new Exception("文件服务器未响应");
            }

            if (fileResponse.getEntity().getContentLength() == 0) {
                throw new Exception("文件不存在");
            }
            return fileResponse.getEntity().getContent();
        } catch (Exception e) {
            e.printStackTrace();
            httpGet.releaseConnection();
            closeTimeoutConnection();
            throw new WutosException(e.getMessage());
        }
    }

    /**
     * 测试统用文件下载
     * @param url
     * @return
     */
    public InputStream getFileStream1(String url,HttpServletResponse response) {
        HttpResponse fileResponse = null;
        HttpGet httpGet = null;
        if (!url.startsWith(this.master)) {
            url = this.master + ":" + fdb_port + "/" + url;
        }
        logger.debug("class:" + "{}   " + "method:{}    " + "params:{}    ", this.getClass().getName(), Thread.currentThread().getStackTrace()[1].getMethodName(), new Object[]{"url:" + url});
        logger.info("class:" + "{}   " + "method:{}    " + "params:{}    ", this.getClass().getName(), Thread.currentThread().getStackTrace()[1].getMethodName(), new Object[]{"url:" + url});
        try {
            httpGet = new HttpGet(url);

            fileResponse = getHttpClient().execute(httpGet);

            Header[] allHeaders = fileResponse.getAllHeaders();
            for(Header head:allHeaders){
//                request.setAttribute(head.getName(),head.getValue());
                if("Content-Disposition".equals(head.getName())){
                    //表示可以在前台xhr的headers里面看到到  否则会隐藏
                    response.setHeader("Access-Control-Expose-Headers",head.getName());
                    String s = new String(head.getValue().getBytes("ISO-8859-1"),"utf-8");
                    String[] split = s.split("filename=");
                    response.setHeader(head.getName(), split[0]+"filename="+ URLEncoder.encode(split[1].substring(1,split[1].length()-1),"utf-8"));
                    continue;
                }
                if("Content-Type".equals(head.getName())){
                    String s = head.getValue().replace("ISO-8859-1","UTF-8");
                    response.setHeader(head.getName(),s);
                    continue;
                }
                response.setHeader(head.getName(),head.getValue());

            }
            if (fileResponse == null) {
                throw new Exception("文件服务器未响应");
            }

            if (fileResponse.getEntity().getContentLength() == 0) {
                throw new Exception("文件不存在");
            }
            return fileResponse.getEntity().getContent();
        } catch (Exception e) {
            e.printStackTrace();
            httpGet.releaseConnection();
            closeTimeoutConnection();
            throw new WutosException(e.getMessage());
        }
    }

    public SeaweedFsUploadResponseEntity uploadFile(File file) {
        HttpResponse fileResponse = null;
        HttpPost httpPost = null;
        if (uploadUrl == null) {
            this.uploadUrl = this.master + ":" + fdb_port + "/submit";
        }
        try {
            httpPost = new HttpPost(uploadUrl);
            httpPost.setHeader(new BasicHeader("Accept-Language", "zh-cn"));
            HttpEntity reqEntity = MultipartEntityBuilder.create()
                    .setCharset(Charset.forName("UTF-8"))
                    .setMode(HttpMultipartMode.BROWSER_COMPATIBLE)
                    .addBinaryBody("file", file)
                    .build();
            httpPost.setEntity(reqEntity);
            fileResponse = getHttpClient().execute(httpPost);
            if (fileResponse == null) {
                throw new WutosException("文件服务器未响应");
            }

            SeaweedFsUploadResponseEntity entity = mapper.readValue(fileResponse.getEntity().getContent(), SeaweedFsUploadResponseEntity.class);
            if (StringUtils.isBlank(entity.getError())) {
                return entity;
            } else {
                throw new WutosException("文件服务器内部错误:" + entity.getError());
            }
        } catch (Exception e) {
            closeTimeoutConnection();
            throw new WutosException(e.getMessage());
        } finally {
            httpPost.releaseConnection();
            responseClose(fileResponse, null);
        }
    }

    public SeaweedFsUploadResponseEntity uploadFile(String fileName, InputStream in) {
        HttpResponse fileResponse = null;
        HttpPost httpPost = null;
        if (uploadUrl == null) {
            this.uploadUrl = this.master + ":" + fdb_port + "/submit";
        }
        try {
            httpPost = new HttpPost(uploadUrl);
            httpPost.setHeader(new BasicHeader("Accept-Language", "zh-cn"));
            HttpEntity reqEntity = MultipartEntityBuilder.create()
                    .setCharset(Charset.forName("UTF-8"))
                    .setMode(HttpMultipartMode.BROWSER_COMPATIBLE)
                    .addBinaryBody(fileName, in, ContentType.MULTIPART_FORM_DATA, fileName)
                    .build();
            httpPost.setEntity(reqEntity);
            fileResponse = getHttpClient().execute(httpPost);
            if (fileResponse == null) {
                httpPost.abort();
                throw new WutosException("文件服务器未响应");
            }

            SeaweedFsUploadResponseEntity entity = mapper.readValue(fileResponse.getEntity().getContent(), SeaweedFsUploadResponseEntity.class);
            if (StringUtils.isBlank(entity.getError())) {
                return entity;
            } else {
                throw new WutosException("文件服务器内部错误:" + entity.getError());
            }
        } catch (Exception e) {
            httpPost.releaseConnection();
            closeTimeoutConnection();
            throw new WutosException(e.getMessage());
        } finally {
            responseClose(fileResponse, in);
        }
    }

    private void responseClose(HttpResponse response, InputStream inputStream) {
        try {
            if (inputStream != null) {
                inputStream.close();
            }
            if (response != null) {
                response.getEntity().getContent().close();
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    @Override
    protected HttpHost getHttpHost() {
        return new HttpHost(master, Integer.parseInt(fdb_port));
    }
}
