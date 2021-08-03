package com.ScanStation.Decomposer;

import com.ScanStation.Bean.HttpBean;
import com.google.gson.*;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaderNames;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.TestOnly;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.nio.charset.Charset;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.regex.Pattern;

@Slf4j
public class HttpDecomposerImp implements Decomposer<HttpBean> {
    LinkedBlockingQueue<HttpBean> httpQueue;
    ArrayList<String> targets;

    public HttpDecomposerImp(LinkedBlockingQueue<HttpBean> httpQueue) {
        this(httpQueue, null);
    }

    public HttpDecomposerImp(LinkedBlockingQueue<HttpBean> httpQueue, ArrayList<String> targets) {
        this.httpQueue = httpQueue;
        this.targets = targets;
    }


    @Override
    public HttpBean ProduceHttp(FullHttpRequest request, Channel clientChannel) {
        HttpBean http = new HttpBean();

        String url = getUrl(request, clientChannel);
        String method = request.method().name();
        String path = request.uri().split("\\?")[0] == null ? request.uri() : request.uri().split("\\?")[0];
        Map<String, String> header = getHeader(request);
        String paramType = getParamType(request);

        if (getParam(request).equals("")){
            log.info("无参数或者为静态文件不扫描 " + method + " " + url + path + " " + getParam(request));
            return null;
        }
        if (paramType.equals("not support")) {
            log.error("not support " + method + " " + url + path + " " + getParam(request));
            return null;
        }
        String param = getParam(request);

        http.setUrl(url);
        http.setMethod(method);
        http.setPath(path);
        http.setHeader(header);
        http.setParam(param);
        http.setParamType(paramType);
        //记录转换的http请求
//        log.info("Start++++++++++++++++++++" + http.toString() + "++++++++++++++++++++End");
        try {
            httpQueue.put(http);
        } catch (InterruptedException e) {
            log.error("队列错误");
            e.printStackTrace();
        }
        //添加到队列并返回当前httpBean
        return http;
    }

    @Override
    /**
     * 用于判断是否需要构造HttpBean
     * 1.在范围内的扫描
     * 2.去重 记录 后续添加
     * **/
    public Boolean isProduce(FullHttpRequest request, Channel clientChannel) {
        for (String target : targets) {
            if (target.equalsIgnoreCase("*") || getUrl(request, clientChannel).contains(target)) {
                log.info("url:"+request.method().name() + " " + getUrl(request, clientChannel)+" "+getParam(request));
                return true;
            }
        }
        return false;
    }

    /**
     * 外部获取内部的队列
     *
     * @return
     */
    @Override
    public LinkedBlockingQueue<HttpBean> getQueue() {
        return httpQueue;
    }

    public void setHttpQueue(LinkedBlockingQueue<HttpBean> httpQueue) {
        this.httpQueue = httpQueue;
    }

    /**
     * 存在bug
     * 用于区分请求类型
     * xml <aa>asd<aa/>
     * json {"id":123,"name":"1213"}
     * form id=1&name=adsa
     * Multi
     **/
    private String getParamType(FullHttpRequest request) {
        String param = getParam(request);
        if (isMultipart(request)) {
            return "MULT";
        }
        if (isJson(param)) {
            return "JSON";
        }
        if (isXml(param)) {
            return "XML";
        }
        if (isFormData(request)) {
            return "FORM";
        }
        return "not support";
    }

    /**
     * GET类请求和POST取值不同
     **/
    private String getParam(FullHttpRequest request) {
        String param = "";
        if (request.uri().contains("?")) {
            param += request.uri().split("\\?")[1];
        }
        param += request.content().toString(Charset.defaultCharset());
        return param;
    }

    /**
     * 用于解析代理到的header
     * 入参:FullHttpRequest
     * 返回值:header的map
     **/
    private Map<String, String> getHeader(FullHttpRequest request) {
        Set<String> headerNames = request.headers().names();
        Map<String, String> header = new HashMap<>();
        for (String name : headerNames) {
            header.put(name, request.headers().get(name));
        }
        return header;
    }

    /**
     * 通过判断pipeline中是否存在sslHander存在则为https请求地址从host获取
     **/
    private String getUrl(FullHttpRequest request, Channel clientChannel) {
        Boolean isSsl = false;
        for (String name : clientChannel.pipeline().names()) {
            if (name.equals("sslHandle")) {
                isSsl = true;
            }
        }
        String protocol = isSsl ? "https://" : "http://";
        String url = protocol + request.headers().get(HttpHeaderNames.HOST);
        return url;
    }

    private boolean isFormData(FullHttpRequest request) {
        String param = getParam(request);
        String pattern = "(.*)=(.*)";
        String[] paramlist = param.split("&");
        for (String s : paramlist) {
            boolean isMatches = Pattern.matches(pattern, s);
            if (!isMatches) {
                return false;
            }
        }
        return true;

    }

    private boolean isXml(String xmlStr) {
        if (xmlStr == null || xmlStr.equals("")) {
            return false;
        }
        try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = documentBuilderFactory.newDocumentBuilder();
            builder.parse(xmlStr);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    private boolean isJson(String jsonStr) {
        JsonElement jsonElement;
        try {
            jsonElement = new JsonParser().parse(jsonStr);
        } catch (Exception e) {
            return false;
        }
        boolean flag = true;
        if (jsonElement == null) {
            return false;
        }
        if (!jsonElement.isJsonArray()) {
            flag = false;
        } else if (!jsonElement.isJsonObject()) {
            flag = false;
        } else if (!jsonElement.isJsonPrimitive()) {
            flag = false;
        }
        return flag;
    }

    private boolean isMultipart(FullHttpRequest request) {
        if (!"post".equals(request.method().name().toLowerCase())) {
            return false;
        }
        String contentType = request.headers().get(HttpHeaderNames.CONTENT_TYPE);
        if ((contentType != null) && (contentType.toLowerCase().startsWith("multipart/"))) {
            return true;
        } else {
            return false;
        }
    }


    @TestOnly
    public static void main(String[] args) {
        HttpDecomposerImp decomposerImp = new HttpDecomposerImp(null);
        String str = "{\n" +
                "  \"muser\": [\n" +
                "    {\n" +
                "      \"name\": \"zhangsan\",\n" +
                "      \"age\": \"10\",\n" +
                "      \"phone\": \"11111\",\n" +
                "      \"email\": \"11111@11.com\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"name\": \"lisi\",\n" +
                "      \"age\": \"20\",\n" +
                "      \"phone\": \"22222\",\n" +
                "      \"email\": \"22222@22.com\"\n" +
                "    }\n" +
                "  ]\n" +
                "}";


        System.out.printf(decomposerImp.isJson(str) + "");
    }
}
