package com.wutos.base.service;

import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.NoHttpResponseException;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.protocol.HttpContext;

import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

public abstract class IHttpClientPoolService implements Runnable {

    private HttpClientBuilder httpClientBuilder = null;

    private final static int TIME_To_LIVE = 2*60*1000; //毫秒值
    private final PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(TIME_To_LIVE,TimeUnit.MILLISECONDS);//过期时间

    public IHttpClientPoolService() {
//        new Thread(this).start();
    }

    protected CloseableHttpClient getHttpClient(){
        if(httpClientBuilder == null){
            buildHttpClientBuilder();
        }
        return httpClientBuilder.build();
//        return HttpClients.createDefault();
    }

    private void buildHttpClientBuilder(){

        synchronized (connectionManager){
            if(httpClientBuilder == null){
                RequestConfig requestConfig;

                final int connectRequestTimeout = 3000;
                final int connectTimeout = 60000;
                final int socketTimeout = 60000;

                connectionManager.setMaxTotal(20);

                connectionManager.setMaxPerRoute(new HttpRoute(getHttpHost()), 20);

                //请求失败时,进行请求重试
                HttpRequestRetryHandler handler = new HttpRequestRetryHandler() {
                    @Override
                    public boolean retryRequest(IOException e, int i, HttpContext httpContext) {
                        if (i > 3){
                            //重试超过3次,放弃请求
//                    logger.error("retry has more than 3 time, give up request");
                            return false;
                        }
                        if (e instanceof NoHttpResponseException){
                            //服务器没有响应,可能是服务器断开了连接,应该重试
//                    logger.error("receive no response from server, retry");
                            return true;
                        }
                        if (e instanceof SSLHandshakeException){
                            // SSL握手异常
//                    logger.error("SSL hand shake exception");
                            return false;
                        }
                        if (e instanceof InterruptedIOException){
                            //超时
//                    logger.error("InterruptedIOException");
                            return false;
                        }
                        if (e instanceof UnknownHostException){
                            // 服务器不可达
//                    logger.error("server host unknown");
                            return false;
                        }
                        if (e instanceof ConnectTimeoutException){
                            // 连接超时
//                    logger.error("Connection Time out");
                            return false;
                        }
                        if (e instanceof SSLException){
//                    logger.error("SSLException");
                            return false;
                        }

                        HttpClientContext context = HttpClientContext.adapt(httpContext);
                        HttpRequest request = context.getRequest();
                        if (!(request instanceof HttpEntityEnclosingRequest)){
                            //如果请求不是关闭连接的请求
                            return true;
                        }
                        return false;
                    }
                };


                requestConfig = RequestConfig.custom()
                        .setConnectionRequestTimeout(connectRequestTimeout)//从连接池获取连接超时时间
                        .setConnectTimeout(connectTimeout)//和目标服务器建立连接超时时间
                        .setSocketTimeout(socketTimeout)//response返回超时时间
                        .build();

                httpClientBuilder = HttpClients.custom().setConnectionManager(connectionManager).setDefaultRequestConfig(requestConfig).setRetryHandler(handler);
            }
        }

    }

    protected void closeTimeoutConnection(){

        connectionManager.closeExpiredConnections();
//        connectionManager.closeIdleConnections(60, TimeUnit.MILLISECONDS);
    }

    protected abstract HttpHost getHttpHost();//HttpHost httpHost = new HttpHost(fdb_ip,fdb_port);

    @Override
    public void run() {
        try {
            //定期清理连接
            Thread.sleep(TIME_To_LIVE);
            closeTimeoutConnection();
            System.out.println("连接关系信息======================="+connectionManager.getTotalStats().toString());
            new Thread(this).start();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}

