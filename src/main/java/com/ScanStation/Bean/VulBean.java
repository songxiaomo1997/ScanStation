package com.ScanStation.Bean;

import java.util.Map;

public class VulBean {
    String name;
    RuleBean rules;
    Map<String,Object> detail;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public RuleBean getRules() {
        return rules;
    }

    public void setRules(RuleBean rules) {
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
        final StringBuilder sb = new StringBuilder("{");
        sb.append("\"name\":\"")
                .append(name).append('\"');
        sb.append(",\"rules\":")
                .append(rules);
        sb.append(",\"detail\":")
                .append(detail);
        sb.append('}');
        return sb.toString();
    }
}
