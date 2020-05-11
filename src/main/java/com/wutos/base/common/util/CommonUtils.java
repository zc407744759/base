package com.wutos.base.common.util;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

import java.util.concurrent.TimeUnit;

public class CommonUtils {
//	public static final ExecutorService threadPool =
//			new ThreadPoolExecutor(2, 10,
//			30000L, TimeUnit.MILLISECONDS,
//			new LinkedBlockingQueue<>(100));

	public static final PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();;
	private static RequestConfig requestConfig;

	private static final int connectTimeout = 1000*60;
	private static final int connectRequestTimeout = 1000*10;

	static {
		connectionManager.setMaxTotal(10000);
		connectionManager.setDefaultMaxPerRoute(100);
		requestConfig = RequestConfig.custom()
				.setConnectTimeout(connectTimeout)
				.setConnectionRequestTimeout(connectRequestTimeout)
				.setSocketTimeout(connectRequestTimeout)
				.build();
	}

	public static CloseableHttpClient getHttpClient() {
		return HttpClients.custom().setConnectionManager(connectionManager).setDefaultRequestConfig(requestConfig).build();
	}

    public static void releaseConnection() {
        connectionManager.closeExpiredConnections();
        connectionManager.closeIdleConnections(connectTimeout,TimeUnit.MILLISECONDS);
    }

	public static RequestConfig getRequestConfig() {
		return requestConfig;
	}

	public static void setRequestConfig(RequestConfig requestConfig) {
		CommonUtils.requestConfig = requestConfig;
	}
}
