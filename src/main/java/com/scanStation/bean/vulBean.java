package com.scanStation.bean;

import java.util.Map;

public class vulBean {
    private String name;
    private ruleBean rules;
    private Map<String,Object> detail;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ruleBean getRules() {
        return rules;
    }

    public void setRules(ruleBean rules) {
        this.rules = rules;
    }

    public Map<String, Object> getDetail() {
        return detail;
    }

    public void setDetail(Map<String, Object> detail) {
        this.detail = detail;
    }

    @Override
    public String toString() {
        return "vul:{" +
                "name='" + name + '\'' +
                ", rules=" + rules +
                ", detail=" + detail +
                '}';
    }
}
