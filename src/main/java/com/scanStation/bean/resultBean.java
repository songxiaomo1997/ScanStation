package com.scanStation.bean;

import java.util.ArrayList;
import java.util.Map;

public class resultBean {


    @Override
    public String toString() {
        return "resultBean{" +
                "name='" + name + '\'' +
                ", detail=" + detail +
                ", scannerBean=" + scannerBeans +
                '}';
    }

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

    public ArrayList<scannerBean> getScannerBeans() {
        return scannerBeans;
    }

    public void setScannerBeans(ArrayList<scannerBean> scannerBeans) {
        this.scannerBeans = scannerBeans;
    }
    public resultBean(){

    }
    public resultBean(String name, Map<String, Object> detail, ArrayList<scannerBean> scannerBeans) {
        this.name = name;
        this.detail = detail;
        this.scannerBeans = scannerBeans;
    }

    private String name;
    private Map<String,Object> detail;
    private ArrayList<scannerBean> scannerBeans;

}
