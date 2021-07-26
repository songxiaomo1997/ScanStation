package com.ScanStation.Bean;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.Callable;

public class ResultBean  {
    String ruleName;
    ArrayList<ScanBean> vulRequest;
    ScanBean originalRequest;
    Map<String, Object> detail;
    String status;

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    public ArrayList<ScanBean> getVulRequest() {
        return vulRequest;
    }

    public void setVulRequest(ArrayList<ScanBean> vulRequest) {
        this.vulRequest = vulRequest;
    }

    public ScanBean getOriginalRequest() {
        return originalRequest;
    }

    public void setOriginalRequest(ScanBean originalRequest) {
        this.originalRequest = originalRequest;
    }

    public Map<String, Object> getDetail() {
        return detail;
    }

    public void setDetail(Map<String, Object> detail) {
        this.detail = detail;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("{");
        sb.append("\"ruleName\":\"")
                .append(ruleName).append('\"');
        sb.append(",\"vulRequest\":")
                .append(vulRequest);
        sb.append(",\"originalRequest\":")
                .append(originalRequest);
        sb.append(",\"detail\":")
                .append(detail);
        sb.append(",\"status\":\"")
                .append(status).append('\"');
        sb.append('}');
        return sb.toString();
    }

}
