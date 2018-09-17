package com.cloud.util;

import org.apache.http.client.config.RequestConfig;

import java.util.HashMap;
import java.util.Map;

/**
 * httpClient 配置类
 */
public class HttpRequestConfig {

    /**
     * 默认配置
     * @return
     */
    public static RequestConfig defaultConfig(){
        return RequestConfig.custom()
                .setConnectTimeout(30000).setConnectionRequestTimeout(30000)
                .setSocketTimeout(30000).build();
    }
    /**
     * 自选配置
     * @return
     */
    public static RequestConfig.Builder selectConfig(){
        return RequestConfig.custom();
    }
    /**
     * 默认请求头配置
     * @return
     */
    public static Map<String,String> defaultHeader(){
        Map<String,String> headerMap = new HashMap<String,String>();
        headerMap.put("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q = 0.8");
        headerMap.put("Accept-Language","zh-CN,zh;q=0.8");
        headerMap.put("Cache-Control","no-cache");
        headerMap.put("Connection","keep-alive");
        headerMap.put("User-Agent","Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/52.0.2743.116 Safari/537.36");
        return headerMap;
    }
}
