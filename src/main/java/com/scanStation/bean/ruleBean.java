package com.scanStation.bean;

import java.util.*;

import org.jetbrains.annotations.NotNull;
import org.springframework.util.DigestUtils;

public class ruleBean {
    /**
     * 构造时从文件获取后设置
     * url
     * path
     * originalParam
     * vulParam
     * */
    @Override
    public String toString() {
        return "ruleBean{" +
                "url='" + url + '\'' +
                ", method='" + method + '\'' +
                ", path='" + path + '\'' +
                ", originalParam='" + originalParam + '\'' +
                ", vulParam='" + vulParam + '\'' +
                ", payloads=" + payloads +
                ", expressions='" + expressions + '\'' +
                ", payloadlength=" + payloadlength +
                ", oob='" + oob + '\'' +
                ", oobflag='" + oobflag + '\'' +
                ", globalParam='" + globalParam + '\'' +
                ", cookie='" + cookie + '\'' +
                ", header=" + header +
                ", headerscan=" + headerscan +
                ", type='" + type + '\'' +
                '}';
    }

    private String url; //扫描目标url 如 http://127.0.0.1
    private String method; //请求方法 POST GET
    private String path; //扫描路径 /api/getall
    private String originalParam; //原始参数
    private String vulParam; //漏洞参数需要扫描的参数
    private List<Map<String, String>> payloads; //payloads包含一个payload和一个表达式
    private String expressions; //总表达式
    private int payloadlength; //有几个payload
    private String oob; //带外地址
    private String oobflag; //带外的flag
    private String globalParam; //全局参数
    private String cookie; //cookie
    private Map<String, String> header; //本次扫描请求头
    private boolean headerscan; //是否对请求头进行扫描
    private String type; //请求类型 json 等

    public String getGlobalParam() {
        return globalParam;
    }

    public void setGlobalParam(String globalParam) {
        this.globalParam = globalParam;
    }

    public String getCookie() {
        return cookie;
    }

    public void setCookie(String cookie) {
        this.cookie = cookie;
    }

    public void setOobflag(String oobflag) {
        this.oobflag = oobflag;
    }

    public Map<String, String> getHeader() {
        if (this.header == null) {
            header = new HashMap<>();
        }
        return header;
    }

    public void setHeader(Map<String, String> header) {
        this.header = header;
    }

    public boolean isHeaderscan() {
        return headerscan;
    }

    public void setHeaderscan(boolean headerscan) {
        this.headerscan = headerscan;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getOobflag() {
        return oobflag;
    }

    public void setOobflag() {
        String base = String.valueOf(System.currentTimeMillis());
        this.oobflag = DigestUtils.md5DigestAsHex(base.getBytes());

    }

    public String getOob() {
        return oobflag + "." + oob;
    }

    public void setOob(String oob) {
        setOobflag();
        this.oob = oob;
    }

    public String getExpressions() {
        return expressions;
    }

    public void setExpressions(String expressions) {
        this.expressions = expressions;
    }

    public Map<String, Object> getParams() {
        Map<String, Object> params = new HashMap<>();
        for (String param : originalParam.split("&")) {
            String[] var = param.split("=");
            params.put(var[0], var.length >= 2 ? var[1] : "");
        }
        return params;
    }

    public int getPayloadlength() {
        return payloadlength;
    }

    public void setPayloadlength(int payloadlength) {
        this.payloadlength = payloadlength;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getOriginalParam() {
        return originalParam;
    }

    public void setOriginalParam(String originalParam) {
        this.originalParam = originalParam;
    }

    public String getVulParam() {
        return vulParam;
    }

    public void setVulParam(String vulParam) {
        this.vulParam = vulParam;
    }

    public List<Map<String, String>> getPayloads() {
        return payloads;
    }

    public void setPayloads(List<Map<String, String>> payloads) {
        for (Map<String, String> payload : payloads) {
            replaceSpecialParam(payload.get("payload"), "{{dnslog}}", "getOob()");
        }
        this.payloads = payloads;
        this.payloadlength = payloads.size();
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url + getPath();
    }

    private String replaceSpecialParam(String tmp, String Special, String Param) {
        if (tmp.contains(Special)) {
            tmp = tmp.replace(Special, Param);//带外地址
        }
        return tmp;
    }

}
