package com.scanStation.bean;

import java.util.Map;

public class scannerBean {


    @Override
    public String toString() {
        return "scannerBean{" +
                "url='" + url + '\'' +
                ", name='" + name + '\'' +
                ", param=" + param +
                ", expression='" + expression + '\'' +
                ", response=" + response +
                ", result=" + result +
                '}';
    }

    private String url;
    private String name;
    private Map<String,String> param;
    private String expression;
    private Map<String,String> response;
    private Boolean result;

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
