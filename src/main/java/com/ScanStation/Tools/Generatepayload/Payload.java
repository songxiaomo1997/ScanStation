package com.ScanStation.Tools.Generatepayload;

import com.ScanStation.Bean.HttpBean;
import com.ScanStation.Bean.RuleBean;
import com.ScanStation.Bean.ScanBean;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class Payload {
    private RuleBean rule;
    private HttpBean http;

    public ArrayList<ScanBean> insertPyload(HttpBean http, RuleBean rule) {
        this.rule = rule;
        this.http = http;
        if (http.getParamType() != null) {
            Map<String, String> header = new HashMap<>();
            header.putAll(http.getHeader());
            header.putAll(rule.getHeader());

            rule.setOriginalParam(http.getParam());
            rule.setVulParam(http.getParam());
            rule.setHeader(header);
            rule.setMethod(http.getMethod());
            rule.setPath(http.getPath());
            rule.setType(http.getParamType());
        }

        ArrayList<ScanBean> scanner = new ArrayList<>();
        payloadsGetActive(scanner);
        getHeaderPayloadActive(scanner);
        return scanner;
    }

    public ScanBean getnormalRequest(HttpBean http, RuleBean rule) {
        ScanBean scanBean = new ScanBean();
        Map<String, Object> params = new HashMap<>();
        Map<String, String> header = new HashMap<>();
        if (http.getParamType() == null) {
            //主动模式
            //url path method type param header
            //从rule获取参数
            params = rule.getParams();
            Map<String, Object> tmpMap = new HashMap<>();
            http.getParams().forEach(tmpMap::put);
            params.putAll(tmpMap);

            ////从rule获取请求头
            header = rule.getHeader();
            header.putAll(http.getHeader());

            scanBean.setUrl(http.getUrl() + rule.getPath());
            scanBean.setType(rule.getType());
            scanBean.setMethod(rule.getMethod());
        } else {
            //被动模式

            params = http.getParams();
            header = http.getHeader();

            scanBean.setUrl(http.getUrl() + http.getPath());
            scanBean.setType(http.getParamType());
            scanBean.setMethod(http.getMethod());
        }


        scanBean.setParam(params);
        scanBean.setHeader(header);

        return scanBean;
    }

    private ArrayList<ScanBean> payloadsGet(String param, HttpBean http) {
        ArrayList<ScanBean> scanner = new ArrayList<>();
        int index = 0;
        for (String scanParam : param.split("&")) {
            for (Map<String, String> payloadAndexpression : rule.getPayloads()) {


                //漏洞参数
                Map<String, Object> params = new HashMap<>();

                //参数添加
                Map<String, Object> tmpMap = new HashMap<>();
                http.getParams().forEach(tmpMap::put);
                params.putAll(tmpMap);

                //添加payload生成map
                String payload = payloadAndexpression.get("payload");
                if ("Form".equalsIgnoreCase(rule.getType()) || "Multi".equalsIgnoreCase(rule.getType())) {
                    params = rule.getParams();
                    String[] var = scanParam.split("=");
                    params.put(var[0], var.length > 1 ? var[1] + payload : payload); //如果有参数直接在参数后加入没有则直接加入

                } else if ("Json".equalsIgnoreCase(rule.getType())) {
                    replaceJson replaceJson = new replaceJson();
                    params = replaceJson.replace(rule.getOriginalParam(), scanParam, payload);
                }


                //header头添加
                Map<String, String> header = new HashMap<>();
                header.putAll(http.getHeader());
                header.putAll(rule.getHeader());

                //ScanBean生成
                ScanBean scb = new ScanBean();
                scb.setUrl(http.getUrl() + rule.getPath());
                scb.setName("payload" + index);
                scb.setMethod(rule.getMethod());
                scb.setHeader(header);
                scb.setParam(params);
                scb.setType(http.getParamType() == null ? rule.getType() : http.getParamType());
                scb.setExpression(payloadAndexpression.get("expression"));
                scanner.add(scb);
                index++;
            }
        }

        return scanner;
    }

    private ArrayList<ScanBean> payloadsGetActive(ArrayList<ScanBean> scanner) {
        int i = scanner.size();
        for (String scanParam : rule.getVulParam().split("&")) {
            for (Map<String, String> payload : rule.getPayloads()) {
                Map<String, Object> params = new HashMap<>();

                //参数添加
                Map<String, Object> tmpMap = new HashMap<>();
                http.getParams().forEach(tmpMap::put);
                params.putAll(tmpMap);

                //添加payload生成map
                String tmp = payload.get("payload");
                if ("Form".equalsIgnoreCase(rule.getType()) || "Multi".equalsIgnoreCase(rule.getType())) {
                    params = rule.getParams();
                    String[] var = scanParam.split("=");
                    params.put(var[0], var.length > 1 ? var[1] + tmp : tmp); //如果有参数直接在参数后加入没有则直接加入
                    log.debug("参数为:"+params);
                } else if ("Json".equalsIgnoreCase(rule.getType())) {
                    replaceJson replaceJson = new replaceJson();
                    params = replaceJson.replace(rule.getOriginalParam(), scanParam, tmp);
                }


                //header头添加
                Map<String, String> header = new HashMap<>();
                header.putAll(http.getHeader());
                header.putAll(rule.getHeader());

                String type;
                if (http.getParamType() == null) {
                    type = rule.getType();
                } else {
                    type = http.getParamType();
                }
                //ScanBean生成
                log.debug("参数为:"+params);
                ScanBean scb = new ScanBean();
                scb.setUrl(http.getUrl() + rule.getPath());
                scb.setName("payload" + i);
                scb.setMethod(rule.getMethod());
                scb.setHeader(header);
                scb.setParam(params);
                scb.setType(type);
                scb.setExpression(payload.get("expression"));
                scanner.add(scb);
                i++;
            }
        }

        return scanner;
    }

    private ArrayList<ScanBean> getHeaderPayloadActive(ArrayList<ScanBean> scanner) {
        if (rule.isHeaderscan()) {
            int i = scanner.size();

            for (Map.Entry<String, String> header : rule.getHeader().entrySet()) {
                for (Map<String, String> payload : rule.getPayloads()) {

                    Map<String, Object> params = new HashMap<>();

                    if (rule.getType().equals("Form") || rule.getType().equals("Multi")) {
                        params = rule.getParams(); //原始参数
                    } else if (rule.getType().equals("Json")) {
                        replaceJson replaceJson = new replaceJson();
                        params = replaceJson.replace(rule.getOriginalParam(), "", "");
                    }

                    //参数添加
                    Map<String, Object> tmpMap = new HashMap<>();
                    http.getParams().forEach(tmpMap::put);
                    params.putAll(tmpMap);

                    //header头添加
                    Map<String, String> headers = new HashMap<>();
                    headers.putAll(http.getHeader());
                    String tmp = payload.get("payload");
                    headers.put(header.getKey(), header.getValue() + tmp);

                    String type;
                    if (http.getParamType() == null) {
                        type = rule.getType();
                    } else {
                        type = http.getParamType();
                    }

                    ScanBean scb = new ScanBean();
                    scb.setHeader(headers);
                    scb.setUrl(http.getUrl() + rule.getPath());
                    scb.setName("payload" + i);
                    scb.setParam(params);
                    scb.setExpression(payload.get("expression"));
                    scb.setMethod(rule.getMethod());
                    scb.setType(type);
                    scb.setHeaderScan(true);
                    scanner.add(scb);
                    i++;
                }
            }
        }
        return scanner;
    }


}
