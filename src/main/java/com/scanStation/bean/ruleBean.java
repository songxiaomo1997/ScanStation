package com.scanStation.bean;

import java.util.*;

import org.springframework.util.DigestUtils;

public class ruleBean {

    @Override
    public String toString() {
        return "ruleBean{" +
                "url='" + url + '\'' +
                ", method='" + method + '\'' +
                ", path='" + path + '\'' +
                ", originalParam='" + originalParam + '\'' +
                ", vulParam='" + vulParam + '\'' +
                ", payloads=" + payloads +
                ", expressions='" + expressions + '\'' +
                ", payloadlength=" + payloadlength +
                ", oob='" + oob + '\'' +
                ", oobflag='" + oobflag + '\'' +
                '}';
    }

    private String url;
    private String method;
    private String path;
    private String originalParam;
    private String vulParam;
    private List<Map> payloads;
    private String expressions;
    private int payloadlength;
    private String oob;
    private String oobflag;

    private Map<String, String> header;
    private boolean headerscan;
    private String type;

    public void setOobflag(String oobflag) {
        this.oobflag = oobflag;
    }

    public Map<String, String> getHeader() {
        return header;
    }

    public void setHeader(Map<String, String> header) {
        this.header = header;
    }

    public boolean isHeaderscan() {
        return headerscan;
    }

    public void setHeaderscan(boolean headerscan) {
        this.headerscan = headerscan;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getOobflag() {
        return oobflag;
    }

    public void setOobflag() {
        String base = String.valueOf(System.currentTimeMillis());
        this.oobflag = DigestUtils.md5DigestAsHex(base.getBytes());

    }

    public String getOob() {
        return oobflag + "." + oob;
    }

    public void setOob(String oob) {
        setOobflag();
        this.oob = oob;
    }

    public String getExpressions() {
        return expressions;
    }

    public void setExpressions(String expressions) {
        this.expressions = expressions;
    }

    public Map<String, String> getParams() {
        Map<String, String> params = new HashMap<>();
        for (String param : originalParam.split("&")) {
            String[] var = param.split("=");
            params.put(var[0], var.length >= 2 ? var[1] : "");
        }
        return params;
    }

    /**
     * 返回一个二维数组[0][0]是第一组payload
     * 数组第一排第一个[0][0]是组装好的payload,第二排第一个是对应的表达式[1][0]
     * [payload1,payload2]
     * [expression1,expression2]
     *
     * @return
     **/
    public ArrayList<scannerBean> Generatepayload() {

        ArrayList<scannerBean> scanner = new ArrayList<>();
        int i = 1;
        for (String vul : vulParam.split("&")) {
            for (Map<String, String> payload : payloads) {
                Map<String, String> params = getParams();
                String[] var = vul.split("=");
                String tmp = payload.get("payload");
                if (tmp.contains("{{dnslog}}")) {
                    System.out.println(tmp + " " + getOob());
                    tmp = tmp.replace("{{dnslog}}", this.getOob());//带外地址
                }

                params.put(var[0], var.length >= 2 ? var[1] + tmp : tmp); //暂时直接加入

                scannerBean scb = new scannerBean();
                scb.setUrl(getUrl());
                scb.setName("payload" + i);
                scb.setParam(params);
                scb.setExpression((String) payload.get("expression"));
                scb.setMethod(method);
                scanner.add(scb);
                i++;
            }
        }

        return scanner;
        /**
         * payload0 :
         *      a:123
         *      b:PG_SLEEP();
         * expression0 :
         *      sleep()
         *
         * **/
    }

    public int getPayloadlength() {
        return payloadlength;
    }

    public void setPayloadlength(int payloadlength) {
        this.payloadlength = payloadlength;
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

    public String getOriginalParam() {
        return originalParam;
    }

    public void setOriginalParam(String originalParam) {
        this.originalParam = originalParam;
    }

    public String getVulParam() {
        return vulParam;
    }

    public void setVulParam(String vulParam) {
        this.vulParam = vulParam;
    }

    public List<Map> getPayloads() {
        return payloads;
    }

    public void setPayloads(List<Map> payloads) {
        this.payloads = payloads;
        this.payloadlength = payloads.size();
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url + getPath();
    }

}
