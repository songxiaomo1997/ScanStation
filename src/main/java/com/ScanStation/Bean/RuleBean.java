package com.ScanStation.Bean;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RuleBean {
    String method;
    String path;
    String originalParam;
    String vulParam;
    List<Map<String, String>> payloads;
    String expressions;
    Map<String, String> header;
    boolean headerscan;
    String type;

    public String getMethod() {
        if (method==null){
            this.method="GET";
        }
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getPath() {
        if (path==null){
            this.path="";
        }
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
        if (vulParam == null) {
            vulParam = "";
        }
        return vulParam;
    }

    public void setVulParam(String vulParam) {
        this.vulParam = vulParam;
    }

    public List<Map<String, String>> getPayloads() {
        return payloads;
    }

    public void setPayloads(List<Map<String, String>> payloads) {
        this.payloads = payloads;
    }

    public String getExpressions() {
        if (expressions == null || expressions.equals("")) {
            expressions = "true";
        }
        return expressions;
    }

    public void setExpressions(String expressions) {
        this.expressions = expressions;
    }

    public Map<String, String> getHeader() {
        if (header == null) {
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
        if (type==null){
            type="Form";
        }
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    //方便获取原始参数的Map类型
    public Map<String, Object> getParams() {
        Map<String, Object> params = new HashMap<>();
        if (originalParam != null && !originalParam.equals("")) {
            for (String param : originalParam.split("&")) {
                String[] var = param.split("=");
                params.put(var[0], var.length >= 2 ? var[1] : "");
            }
        }
        return params;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("{");
        sb.append("\"method\":\"")
                .append(method).append('\"');
        sb.append(",\"path\":\"")
                .append(path).append('\"');
        sb.append(",\"originalParam\":\"")
                .append(originalParam).append('\"');
        sb.append(",\"vulParam\":\"")
                .append(vulParam).append('\"');
        sb.append(",\"payloads\":")
                .append(payloads);
        sb.append(",\"expressions\":\"")
                .append(expressions).append('\"');
        sb.append(",\"header\":")
                .append(header);
        sb.append(",\"headerscan\":")
                .append(headerscan);
        sb.append(",\"type\":\"")
                .append(type).append('\"');
        sb.append('}');
        return sb.toString();
    }
}
