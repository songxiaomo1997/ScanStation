package com.scanStation.tools.Generatepayload;

import com.scanStation.bean.ruleBean;
import com.scanStation.bean.scannerBean;

import java.util.ArrayList;
import java.util.Map;

public class payload {
    private ruleBean rule;

    public payload(ruleBean rule){
        this.rule = rule;
    }

    public ArrayList<scannerBean> Generatepayload(){
        ArrayList<scannerBean> scanner = new ArrayList<>();
        if ("form".equals(rule.getType())){
            getformPayloads(scanner);
        }
        return scanner;
    }

    private ArrayList<scannerBean> getformPayloads(ArrayList<scannerBean> scanner) {
        int i = scanner.size();
        for (String vul : rule.getVulParam().split("&")) {
            for (Map<String, String> payload : rule.getPayloads()) {
                Map<String, String> params = rule.getParams();
                String[] var = vul.split("=");
                String tmp = payload.get("payload");
                if (tmp.contains("{{dnslog}}")) {
                    System.out.println(tmp + " " + rule.getOob());
                    tmp = tmp.replace("{{dnslog}}", rule.getOob());//带外地址
                }

                params.put(var[0], var.length >= 2 ? var[1] + tmp : tmp); //暂时直接加入

                scannerBean scb = new scannerBean();
                scb.setUrl(rule.getUrl());
                scb.setName("payload" + i);
                scb.setParam(params);
                scb.setExpression(payload.get("expression"));
                scb.setMethod(rule.getMethod());
                scanner.add(scb);
                i++;
            }
        }

        return scanner;
    }


}
