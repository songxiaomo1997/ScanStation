package com.ScanStation.Bean;

import java.util.ArrayList;
import java.util.Map;

public class PayloadBean {
    String ruleName;
    String expressions;
    ScanBean normalRequest;
    ArrayList<ScanBean> scanList;
    Map<String, Object> detail;

    public ArrayList<ScanBean> getScanList() {
        return scanList;
    }

    public void setScanList(ArrayList<ScanBean> scanList) {
        this.scanList = scanList;
    }

    public ScanBean getNormalRequest() {
        return normalRequest;
    }

    public void setNormalRequest(ScanBean normalRequest) {
        this.normalRequest = normalRequest;
    }

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    public String getExpressions() {
        return expressions;
    }

    public void setExpressions(String expressions) {
        this.expressions = expressions;
    }

    public Map<String, Object> getDetail() {
        return detail;
    }

    public void setDetail(Map<String, Object> detail) {
        this.detail = detail;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("{");
        sb.append("\"ruleName\":\"")
                .append(ruleName).append('\"');
        sb.append(",\"expressions\":\"")
                .append(expressions).append('\"');
        sb.append(",\"normalRequest\":")
                .append(normalRequest);
        sb.append(",\"scanList\":")
                .append(scanList);
        sb.append(",\"detail\":")
                .append(detail);
        sb.append('}');
        return sb.toString();
    }
}
