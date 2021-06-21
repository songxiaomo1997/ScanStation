package com.scanStation.bean;

import java.util.Map;

public class scannerBean {


    @Override
    public String toString() {
        return "scanner:{" +
                "url='" + url + '\'' +
                ", name='" + name + '\'' +
                ", param=" + param +
                ", expression='" + expression + '\'' +
                ", response=" + response +
                ", result=" + result +
                ", method='" + method + '\'' +
                ", header=" + header +
                ", type='" + type + '\'' +
                '}';
    }

    private String url;
    private String name;
    private Map<String, String> param;
    private String expression;
    private Map<String, String> response;
    private Boolean result;
    private String method;
    private Map<String, String> header;
    private boolean headerscan;
    private String type;

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

    public Map<String, String> getHeader() {
        return header;
    }

    public void setHeader(Map<String, String> header) {
        this.header = header;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Boolean getResult() {
        return result;
    }

    public void setResult(Boolean result) {
        this.result = result;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, String> getParam() {
        return param;
    }

    public void setParam(Map<String, String> param) {
        this.param = param;
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public Map<String, String> getResponse() {
        return response;
    }

    public void setResponse(Map<String, String> response) {
        this.response = response;
    }


}
