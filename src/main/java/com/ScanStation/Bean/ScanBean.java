package com.ScanStation.Bean;

import java.util.Map;

public class ScanBean {
    String name;
    String url;
    String method;
    Map<String, Object> param;
    String expression;
    Map<String, String> response;
    Map<String, String> header;
    Boolean headerScan;
    String type;
    String oobflag;
    Boolean result;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Map<String, Object> getParam() {
        return param;
    }

    public void setParam(Map<String, Object> param) {
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

    public Map<String, String> getHeader() {
        return header;
    }

    public void setHeader(Map<String, String> header) {
        this.header = header;
    }

    public Boolean getHeaderScan() {
        return headerScan;
    }

    public void setHeaderScan(Boolean headerScan) {
        this.headerScan = headerScan;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getOobflag() {
        if (expression.contains("oob()")){
            //生成oobflag
        }
        return oobflag;
    }

    public void setOobflag(String oobflag) {
        this.oobflag = oobflag;
    }

    public Boolean getResult() {
        return result;
    }

    public void setResult(Boolean result) {
        this.result = result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("{");
        sb.append("\"name\":\"")
                .append(name).append('\"');
        sb.append(",\"url\":\"")
                .append(url).append('\"');
        sb.append(",\"method\":\"")
                .append(method).append('\"');
        sb.append(",\"param\":")
                .append(param);
        sb.append(",\"expression\":\"")
                .append(expression).append('\"');
        sb.append(",\"response\":")
                .append(response);
        sb.append(",\"header\":")
                .append(header);
        sb.append(",\"headerScan\":")
                .append(headerScan);
        sb.append(",\"type\":\"")
                .append(type).append('\"');
        sb.append(",\"oobflag\":\"")
                .append(oobflag).append('\"');
        sb.append(",\"result\":")
                .append(result);
        sb.append('}');
        return sb.toString();
    }
}
