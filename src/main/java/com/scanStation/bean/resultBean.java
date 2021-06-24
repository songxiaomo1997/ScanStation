package com.scanStation.bean;

import java.util.Map;

public class resultBean {
    @Override
    public String toString() {
        return "resultBean{" +
                "name='" + name + '\'' +
                ", detail=" + detail +
                ", scannerBean=" + scannerBean +
                '}';
    }

    private String name;
    private Map<String, Object> detail;
    private scannerBean scannerBean;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, Object> getDetail() {
        return detail;
    }

    public void setDetail(Map<String, Object> detail) {
        this.detail = detail;
    }

    public com.scanStation.bean.scannerBean getScannerBean() {
        return scannerBean;
    }

    public void setScannerBean(com.scanStation.bean.scannerBean scannerBean) {
        this.scannerBean = scannerBean;
    }
}
