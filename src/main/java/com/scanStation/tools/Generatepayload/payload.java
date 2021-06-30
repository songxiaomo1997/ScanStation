package com.scanStation.tools.Generatepayload;

import com.scanStation.bean.ruleBean;
import com.scanStation.bean.scannerBean;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Log4j2
public class payload {
    private ruleBean rule;

    public payload(ruleBean rule) {
        this.rule = rule;
    }

    public ArrayList<scannerBean> Generatepayload() {
        ArrayList<scannerBean> scanner = new ArrayList<>();
        payloadsget(scanner);
        getheaderPayload(scanner);
        return scanner;
    }
    private ArrayList<scannerBean> payloadsget(ArrayList<scannerBean> scanner) {

        int i = scanner.size();
        for (String vul : rule.getVulParam().split("&")) {
            for (Map<String, String> payload : rule.getPayloads()) {
                Map<String, Object> params = new HashMap<>();
                String tmp = payload.get("payload");

               if ("Form".equals(rule.getType())||"Multi".equals(rule.getType())){
                   params = rule.getParams();
                   String[] var = vul.split("=");
                   params.put(var[0], var.length >= 2 ? var[1] + tmp : tmp); //如果有参数直接在参数后加入没有则直接加入
               }else if ("Json".equals(rule.getType())){
                   replaceJson replaceJson = new replaceJson();
                   params = replaceJson.replace(rule.getOriginalParam(),vul,tmp);
               }

                //scannerBean生成
                scannerBean scb = new scannerBean();
                scb.setUrl(rule.getUrl());
                scb.setName("payload" + i);
                scb.setParam(params);
                scb.setExpression(payload.get("expression"));
                scb.setMethod(rule.getMethod());
                scb.setType(rule.getType());
                scanner.add(scb);
                i++;
            }
        }

        return scanner;
    }

    private ArrayList<scannerBean> getheaderPayload(ArrayList<scannerBean> scanner) {
        if (rule.isHeaderscan()) {
            int i = scanner.size();
            for (Map.Entry<String, String> header : rule.getHeader().entrySet()) {
                for (Map<String, String> payload : rule.getPayloads()) {
                    scannerBean scb = new scannerBean();
                    Map<String, String> headers = new HashMap<>();
                    Map<String, Object> params = new HashMap<>();
                    String tmp = payload.get("payload");
                    //存在带外等替换
                    headers.put(header.getKey(), tmp);
                    if(rule.getType().equals("Form")||rule.getType().equals("Multi")) {
                        params = rule.getParams(); //原始参数
                    }else if (rule.getType().equals("Json")){
                        replaceJson replaceJson = new replaceJson();
                        params= replaceJson.replace(rule.getOriginalParam(),"","");
                    }
                    scb.setHeader(headers);
                    scb.setUrl(rule.getUrl());
                    scb.setName("payload" + i);
                    scb.setParam(params);
                    scb.setExpression(payload.get("expression"));
                    scb.setMethod(rule.getMethod());
                    scb.setType(rule.getType());
                    scb.setHeaderscan(true);
                    scanner.add(scb);
                    i++;
                }
            }
        } else {
            for (scannerBean scb : scanner) {
                scb.setHeader(rule.getHeader());
            }
        }
        return scanner;
    }


//    @NotNull
//    private String replaceSpecialParam(String tmp, String Special, String Param) {
//        if (tmp.contains(Special)) {
//            tmp = tmp.replace(Special, Param);//带外地址
//        }
//        return tmp;
//    }


}
