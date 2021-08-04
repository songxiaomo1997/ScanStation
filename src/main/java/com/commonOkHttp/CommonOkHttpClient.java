package com.commonOkHttp;

import com.ScanStation.Bean.ScanBean;
import com.google.gson.Gson;
import com.commonOkHttp.utils.HttpsUtils;
import com.google.gson.GsonBuilder;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import okhttp3.FormBody.Builder;
import okhttp3.internal.Util;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.TestOnly;

import java.io.File;
import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Slf4j
public final class CommonOkHttpClient {

    private OkHttpClient okHttpClient;
    private RequestTimeEventListener requestTimeEventListener = new RequestTimeEventListener();//时间参数获取

    CommonOkHttpClient(long readTimeout, long writeTimeout, long connectTimeout, HttpsUtils.SSLParams sslParams) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.readTimeout(readTimeout, TimeUnit.MILLISECONDS);
        builder.writeTimeout(writeTimeout, TimeUnit.MILLISECONDS);
        builder.connectTimeout(connectTimeout, TimeUnit.MILLISECONDS);
        builder.eventListener(requestTimeEventListener);
//        builder.proxy(new Proxy(Proxy.Type.HTTP,new InetSocketAddress("127.0.0.1",8080)));

        // sslParams 如果为null只是不设置证书相关的参数,而使用默认的CA认证方式
        if (sslParams != null) {
            builder.sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager);
            if (sslParams.hostnameVerifier != null) {
                builder.hostnameVerifier(sslParams.hostnameVerifier);
            }
        }
        okHttpClient = builder.build();
    }


    public Map<String, String> request(ScanBean scb) {
        Map<String, String> response = new HashMap<String, String>();
        log.debug(scb.toString());
        if (scb.getMethod().equalsIgnoreCase("GET")) {
            return get(scb.getUrl(), scb.getParam(), scb.getHeader());
        } else if (scb.getMethod().equalsIgnoreCase("POST")) {
            if (scb.getType().equalsIgnoreCase("MULT")) {
                return Multipost(scb.getUrl(), scb.getParam(), scb.getHeader());
            } else {
                return doPost(scb.getUrl(), scb.getParam(), scb.getHeader(), scb.getType());
            }
        } else {
            response.put("info", "not Method");
            return response;
        }
    }

    public Map<String, String> get(String url, Map<String, Object> param, Map<String, String> headerExt) {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();
        if (param != null) {
            param.forEach((k, v) -> urlBuilder.addQueryParameter(k, String.valueOf(v)));
        }
        Request.Builder reqBuilder = new Request.Builder().get().url(urlBuilder.build());

        if (headerExt.containsKey("Accept-Encoding")) {
            if (headerExt.get("Accept-Encoding").contains("gzip")) {
                headerExt.replace("Accept-Encoding", headerExt.get("Accept-Encoding").replaceAll("gzip", ""));
            }
        }
        if (headerExt.size() > 0) {
            headerExt.forEach(reqBuilder::addHeader);
        }
        Request request = reqBuilder.build();
        return sendRequest(request);
    }


    public <T extends UploadFileBase> Map<String, String> Multipost(String url, Map<String, Object> prarm, Map<String, String> headerExt) {
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);
        if (prarm != null) {
            String basePath = "/Users/song/Documents/GitHub/ScanStation/src/main/resources/";
            prarm.forEach((k, v) -> {
                if (k.contains("<")) {
                    String filename = k.substring(k.indexOf("<") + 1, k.indexOf(">"));
                    File file = new File(basePath +"/"+ filename);
                    RequestBody requestBody = RequestBody.create(MediaType.parse("application/octet-stream"), file);
                    builder.addFormDataPart(k.substring(0, k.indexOf("<")), String.valueOf(v), requestBody);
                } else {
                    builder.addFormDataPart(k, String.valueOf(v));
                }
            });
        }

        MultipartBody uploadBody = builder.build();
        Request.Builder reqBuilder = new Request.Builder().post(uploadBody).url(url);

        //添加请求头
        if (headerExt.containsKey("Accept-Encoding")) {
            if (headerExt.get("Accept-Encoding").contains("gzip")) {
                headerExt.replace("Accept-Encoding", headerExt.get("Accept-Encoding").replaceAll("gzip", ""));
            }
        }
        if (headerExt.size() > 0) {
            headerExt.forEach(reqBuilder::addHeader);
        }
        Request request = reqBuilder.build();
        return sendRequest(request);
    }

    public Map<String, String> doPost(String url, Map<String, Object> prarm, Map<String, String> headerExt, String type) {
        //构造请求体
        RequestBody body = getRequestBody(prarm, headerExt, type);

        Request.Builder reqBuilder = new Request.Builder().post(body).url(url);
        //添加请求头
        if (headerExt.containsKey("Accept-Encoding")) {
            if (headerExt.get("Accept-Encoding").contains("gzip")) {
                headerExt.replace("Accept-Encoding", headerExt.get("Accept-Encoding").replaceAll("gzip", ""));
            }
        }
        if (headerExt.size() > 0) {
            headerExt.forEach(reqBuilder::addHeader);
        }

        Request request = reqBuilder.build();
        return sendRequest(request);
    }

    private RequestBody getRequestBody(Map<String, Object> prarm, Map<String, String> headerExt, String type) {
        RequestBody body = Util.EMPTY_REQUEST;
        if (prarm != null && prarm.size() > 0) {
            if (type.equalsIgnoreCase("json")) {
                GsonBuilder builder = new GsonBuilder();
                Gson gson = builder.create();
                String json = gson.toJson(prarm);
                body = RequestBody.create(MediaType.parse(headerExt.getOrDefault("Content-Type", "application/json;charset=UTF-8;")), json);
            } else if (type.equalsIgnoreCase("Form")) {
                if (headerExt.containsKey("Content-Type")) {
                    body = RequestBody.create(MediaType.parse(headerExt.get("Content-Type")), formParam(prarm));
                } else {
                    Builder builder = new Builder();
                    prarm.forEach((k, v) -> builder.add(k, String.valueOf(v)));
                    body = builder.build();
                }
            }
        }
        return body;
    }

    private String formParam(Map<String, Object> param) {
        final String[] form = {""};
        param.forEach((k, v) -> form[0] += k + "=" + String.valueOf(v) + "&");

        return form[0].substring(0, form[0].length() - 1);
    }

    private Map<String, String> sendRequest(Request request) {
        // 同步
        try {

            Response response = okHttpClient.newCall(request).execute();

            Map<String, String> responseMap = new HashMap<>();
            responseMap.put("status", String.valueOf(response.code()));
            responseMap.put("header", response.headers().toString());
            responseMap.put("body", Objects.requireNonNull(response.body()).string());
            responseMap.put("time", String.valueOf(requestTimeEventListener.getRequestTime()));
            return responseMap;

        } catch (SocketTimeoutException e) {
            //log点
            Map<String, String> responseMap = new HashMap<>();
            responseMap.put("status", "timeout");
            responseMap.put("header", "timeout");
            responseMap.put("body", "timeout");
            responseMap.put("time", String.valueOf(System.currentTimeMillis() - requestTimeEventListener.getStarttime()));
            return responseMap;
        } catch (SocketException e) {
            log.error("检测目标网络错误:" + request.url());
            Map<String, String> responseMap = new HashMap<>();
            responseMap.put("status", "network error");
            responseMap.put("header", "network error");
            responseMap.put("body", "network error");
            responseMap.put("time", "0");
            return responseMap;
        } catch (IOException e) {
            e.printStackTrace();
            log.error("检测目标网络错误:" + request.url());
            Map<String, String> responseMap = new HashMap<>();
            responseMap.put("status", "network error");
            responseMap.put("header", "network error");
            responseMap.put("body", "network error");
            responseMap.put("time", "0");
            return responseMap;
        }
    }

    @TestOnly
    public static void main(String[] args) {
        Map<String, Object> param = new HashMap<>();
        param.put("id", 123);

        Map<String, String> header = new HashMap<>();
        header.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.107 Safari/537.36");
        header.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9");
        header.put("Accept-Encoding", "gzip, deflate");
        ScanBean scanBean = new ScanBean();
        scanBean.setUrl("http://120.26.84.240:8008/Less-1/");
        scanBean.setParam(param);
        scanBean.setHeader(header);
        scanBean.setType("From");
        scanBean.setMethod("GET");

        CommonOkHttpClient httpClientNotSafe = new CommonOkHttpClientBuilder().unSafe(true).build();
        Map<String, String> response = httpClientNotSafe.request(scanBean);
        log.info(String.valueOf(response));
    }
}


