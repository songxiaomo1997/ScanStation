package com.ScanStation.Bean;

import java.util.HashMap;
import java.util.Map;

public class HttpBean {
    /**
     *          主动          被动
     * url      外部获取       netty解析
     * method   不填充         netty解析
     * path     不填充         netty解析
     * header   配置文件获取     netty解析
     * param    外部获取        netty解析
     * paramType 不填充        netty解析后
     **/

    String url;
    String method;
    String path;
    Map<String, String> header;
    String param;
    String paramType;

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

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Map<String, String> getHeader() {
        return header;
    }

    public void setHeader(Map<String, String> header) {
        this.header = header;
    }

    public String getParam() {
        return param;
    }

    public void setParam(String param) {
        this.param = param;
    }

    public String getParamType() {
        return paramType;
    }

    public void setParamType(String paramType) {
        this.paramType = paramType;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("{");
        sb.append("\"url\":\"")
                .append(url).append('\"');
        sb.append(",\"method\":\"")
                .append(method).append('\"');
        sb.append(",\"path\":\"")
                .append(path).append('\"');
        sb.append(",\"header\":")
                .append(header);
        sb.append(",\"param\":\"")
                .append(param).append('\"');
        sb.append(",\"paramType\":\"")
                .append(paramType).append('\"');
        sb.append('}');
        return sb.toString();
    }

    //便于获取Map类型参数
    public Map<String, Object> getParams() {
        Map<String, Object> params = new HashMap<>();
        if (this.param != null && !this.param.equals("")) {
            for (String param : this.param.split("&")) {
                String[] var = param.split("=");
                params.put(var[0], var.length >= 2 ? var[1] : "");
            }
        }
        return params;
    }
}
