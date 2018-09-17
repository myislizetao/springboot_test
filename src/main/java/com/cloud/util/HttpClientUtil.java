package com.cloud.util;

import com.alibaba.fastjson.JSON;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.sql.SQLOutput;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * httpClient 工具类
 */
public class HttpClientUtil {

    public static void main(String[] args) {
        String url = "http://mobile.yangkeduo.com/proxy/api/mobile/code/request?pdduid=0&is_back=1";
        HttpResult httpResultToken = doGet("http://192.168.10.204:3000?type=8",null,HttpRequestConfig.defaultConfig());
        String screen_token = httpResultToken.getResult();
        Map params = new HashMap();
        Map fingerprintMap = new HashMap();
        fingerprintMap.put("innerHeight",203);
        fingerprintMap.put("innerWidth",1920);
        fingerprintMap.put("devicePixelRatio",1);
        fingerprintMap.put("availHeight",1040);
        fingerprintMap.put("availWidth",1920);
        fingerprintMap.put("height",1080);
        fingerprintMap.put("width",1920);
        fingerprintMap.put("colorDepth",24);
        fingerprintMap.put("locationHerf","http://mobile.yangkeduo.com/login.html?from=http://mobile.yangkeduo.com/personal.html?refer_page_name=setting&refer_page_id=10134_1536650612055_x00KgQT2HW&refer_page_sn=10134&refer_page_name=personal&refer_page_id=10001_1536650612971_oy1Troo0S6&refer_page_sn=10001&page_id=login_1536650620486_whvveN5msh&is_back=1");
        fingerprintMap.put("referer","personal");
        fingerprintMap.put("timezoneOffset",-480);
        Map navigatorMap = new HashMap();
        navigatorMap.put("appCodeName","Mozilla");
        navigatorMap.put("appName","Netscape");
        navigatorMap.put("hardwareConcurrency",4);
        navigatorMap.put("language","zh-CN");
        navigatorMap.put("cookieEnabled",true);
        navigatorMap.put("platform","Win32");
        navigatorMap.put("doNotTrack",null);
        navigatorMap.put("ua","Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/67.0.3396.99 Safari/537.36");
        navigatorMap.put("vendor","Google Inc.");
        navigatorMap.put("product","Gecko");
        navigatorMap.put("productSub","20030107");
        fingerprintMap.put("navigator",navigatorMap);
        params.put("fingerprint",fingerprintMap);
        params.put("mobile","13123379906");
        params.put("platform","4");
        params.put("screen_token",screen_token);
        Map toucheventMap = new HashMap();
        toucheventMap.put("mobileInputEditStartTime",System.currentTimeMillis()-25);
        toucheventMap.put("mobileInputKeyboardEvent","0|0|0|819-1051-1275-1587-1819-1971-2259-2571-2748-3059-3356");
        toucheventMap.put("mobileInputEditFinishTime",System.currentTimeMillis()-15);
        toucheventMap.put("sendSmsButtonTouchPoint","1179,157");
        toucheventMap.put("sendSmsButtonClickTime",System.currentTimeMillis()-5);
        params.put("touchevent",toucheventMap);
        String json = JSON.toJSONString(params);
//        System.out.println(json);
        Map<String,String> headers = new HashMap<String,String>();
        headers.put("Accept","*/*");
        headers.put("Accept-Encoding","gzip, deflate");
        headers.put("Accept-Language","zh-CN,zh;q=0.9");
        headers.put("AccessToken","");
        headers.put("Cache-Control","no-cache");
        headers.put("Connection","keep-alive");
        headers.put("Content-Type","application/json;charset=UTF-8");
        headers.put("Cookie","api_uid=rBQRpFtNualm9UxuBJq3Ag==; new_arrivals=new_arrivals_tgQsZ8; rec_18=rec_18_2sbn4h; rec_list_catgoods=rec_list_catgoods_7KCs1b; rec_list_index=rec_list_index_n5M6lN; search=search_YbfYoh; ua=Mozilla%2F5.0%20(Windows%20NT%206.1%3B%20WOW64)%20AppleWebKit%2F537.36%20(KHTML%2C%20like%20Gecko)%20Chrome%2F67.0.3396.99%20Safari%2F537.36; webp=1; chat_list_rec_list=chat_list_rec_list_KMnGwb; rec_list=rec_list_eOmZqy; mall_main=mall_main_kmYtB0; rec_list_mall_bottom=rec_list_mall_bottom_L9UKN5; msec=1800000; rec_list_personal=rec_list_personal_O7apD8");
        headers.put("Host","mobile.yangkeduo.com");
        headers.put("Origin","http://mobile.yangkeduo.com");
        headers.put("Pragma","no-cache");
        headers.put("Referer","http://mobile.yangkeduo.com/login.html?from=http%3A%2F%2Fmobile.yangkeduo.com%2Fpersonal.html%3Frefer_page_name%3Dsetting%26refer_page_id%3D10134_1536650612055_x00KgQT2HW%26refer_page_sn%3D10134&refer_page_name=personal&refer_page_id=10001_1536650612971_oy1Troo0S6&refer_page_sn=10001&page_id=login_1536650620486_whvveN5msh&is_back=1");
        headers.put("User-Agent","Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/67.0.3396.99 Safari/537.36");
        HttpResult httpResult = doPostJSONHeaders(url,json,headers,HttpRequestConfig.defaultConfig());
        System.out.println(httpResult.toString());
    }

    /**
     * post请求传json字符串
     * @param url
     * @param json 要传的json字符串
     * @param headers 请求头
     * @param requestConfig 请求的配置
     * @return
     */
    public static HttpResult doPostJSONHeaders(String url, String json, Map<String,String> headers,RequestConfig requestConfig){
        CloseableHttpClient httpclient = HttpClients.createDefault();
        CloseableHttpResponse httpResp = null;
        try {
            HttpPost httppost = new HttpPost(url);
            httppost.setConfig(requestConfig);
            StringEntity requestEntity = new StringEntity(json,"utf-8");
            requestEntity.setContentEncoding("UTF-8");
            httppost.setEntity(requestEntity);
            if(headers!=null){
                Set<String> keySet = headers.keySet();
                for (String key : keySet) {
                    httppost.addHeader(key, headers.get(key));
                }
            }
            httpResp = httpclient.execute(httppost);
            Integer code = httpResp.getStatusLine().getStatusCode();
            String resultStr = EntityUtils.toString(httpResp.getEntity(), "UTF-8");
            return new HttpResult(code,resultStr);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if(httpResp!=null){
                    httpResp.close();
                }
                httpclient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     *get请求
     * @param url
     * @param headers 请求头
     * @param requestConfig  请求配置
     * @return
     */
    public static HttpResult doGet(String url, Map<String, String> headers,RequestConfig requestConfig){
        CloseableHttpClient httpclient = HttpClients.createDefault();
        CloseableHttpResponse httpResp = null;
        try {
            HttpGet httpGet = new HttpGet(url);
            httpGet.setConfig(requestConfig);
            if(headers!=null){
                Set<String> keySet = headers.keySet();
                for (String key : keySet) {
                    httpGet.addHeader(key, headers.get(key));
                }
            }
            httpResp = httpclient.execute(httpGet);
            Integer code = httpResp.getStatusLine().getStatusCode();
            String resultStr = EntityUtils.toString(httpResp.getEntity(), "UTF-8");
            return new HttpResult(code,resultStr);
        }catch (Exception e) {
            e.printStackTrace();
        }finally{
            try {
                if(httpResp!=null){
                    httpResp.close();
                }
                httpclient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
