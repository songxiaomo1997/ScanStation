package com.scanStation;

import com.scanStation.bean.ruleBean;
import com.scanStation.bean.scannerBean;
import com.scanStation.bean.vulBean;
import com.scanStation.commonOkHttp.CommonOkHttpClient;
import com.scanStation.commonOkHttp.CommonOkHttpClientBuilder;
import com.scanStation.tools.Generatepayload.payload;
import com.scanStation.tools.avitorTools;
import com.scanStation.tools.yamlTools;
import lombok.extern.log4j.Log4j2;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Log4j2
public class scanner {

    public static void main(String[] args) {
        String url = args[0];
        String path = args[1];
        String cookie;
        String param;

        if (args.length >= 3) {
            cookie = args[2];
        } else {
            cookie = "";
        }
        if (args.length >= 4) {
            param = args[3];
        } else {
            param = "";
        }
        scanner scanner = new scanner();
        ArrayList<String> re = new ArrayList<>();
        File dir = new File(path);
        String[] children = dir.list();
        for (String file : children) {
            if (file.endsWith(".yaml")) {
                scanner scan = new scanner();
                vulBean vul = scan.vulGet(path + "/" + file, url, param, cookie);

                scanner.scan(vul, re);
            }
        }
        for (String r : re) {
            System.out.println(r);
        }

    }


    public void scan(vulBean vul, ArrayList<String> re) {
        log.info("------------------------------------检测开始------------------------------------");

        //加载规则
        ruleBean rule = vul.getRules();


        log.info("加载规则:" + vul.getName());
        log.info("检测规则:" + rule.toString());
        log.info("---规则加载完成");

        //初始化request
        CommonOkHttpClient httpClientNotSafe = getCommonOkHttpClient(rule.getCookie(), rule.getGlobalParam(), rule);
        log.info("---初始化request完成");

        log.info("payload开始生成");
        payload paylaod = new payload(rule);
        ArrayList<scannerBean> payloadAndExpression = paylaod.Generatepayload();
        log.info("payload生成完成开始扫描");

        //开始检测
        Map<String, Object> expressionsEnv = new HashMap<>();
        for (scannerBean scb : payloadAndExpression) {
            log.info(scb.getName() + "检测开始");
            log.debug("payload信息" + scb.toString());
            Map<String, String> response = httpClientNotSafe.request(scb);
            scb.setResult(judgment(rule, scb, response));
            expressionsEnv.put(scb.getName(), scb.getResult());

            log.info(scb.getName() + "检测完成");
            log.debug("payload信息" + scb.toString() + scb.getMethod());
            log.info("响应头:" + response.get("status") + " 响应时间:" + response.get("time"));

        }
        log.info("---payload检测完成");
        //expressions判断
        avitorTools avitor = new avitorTools();
        avitor.setEnv(expressionsEnv);
        avitor.setExpression(rule.getExpressions());
        Boolean expressionsRe;
        try {
            expressionsRe = avitor.execAvitor();
        } catch (Exception e) {
            log.error("表达式错误");
            log.debug(expressionsEnv);
            log.debug(rule.getExpressions());
            expressionsRe = false;
        }
        if (expressionsRe) {
            for (scannerBean scb : payloadAndExpression) {
                if (scb.getResult()) {
                    re.add("++++++++++++vulFind:" + vul.getName() + "-" + scb.toString() + " " + vul.getDetail());
                    log.info("++++++++++++vulFind:" + vul.getName() + "-" + scb.toString() + " " + vul.getDetail());
                }
            }
        }
        log.info("---expressions判断完成");
        log.info("------------------------------------检测完成------------------------------------");
    }

    private vulBean vulGet(String file, String url, String globalParam, String cookie) {
        vulBean vul = new yamlTools(file).load();
        vul.getRules().setUrl(url);
        ruleBean rule = vul.getRules();
        rule.setUrl(url);
        if (globalParam != null && !globalParam.equals("")) {
            rule.setGlobalParam(globalParam);
        }
        if (cookie != null && !cookie.equals("")) {
            rule.setCookie(cookie);
        }
        //rule.setOob("q3fljw.dnslog.cn"); //设置dnslog

        return vul;
    }

    private CommonOkHttpClient getCommonOkHttpClient(String cookie, String param, ruleBean rule) {
        CommonOkHttpClient httpClientNotSafe = new CommonOkHttpClientBuilder().unSafe(true).build();
        if (cookie != null && !"".equals(cookie)) {
            //设置全局cookie
            httpClientNotSafe.setCookie(cookie);
            log.info("设置全局cookie值为:" + cookie);
        }
        if (param != null && !"".equals(param)) {
            //通过String方式设置全局参数
            httpClientNotSafe.setGlobalParam(param);
            log.info("设置全局参数值为:" + param);
        }
        if (rule.isHeaderscan()) {
            httpClientNotSafe.setHeaderExt(rule.getHeader());
        }
        //后续通过配置文件获取全局请求头，支持外部获取和默认配置
        return httpClientNotSafe;
    }

    private Boolean judgment(ruleBean rule, scannerBean scb, Map response) {
        if ("timeout...".equals(response.get("status"))) {
            log.error("检测超时");
            return false;
        } else if ("network error".equals(response.get("status")) || "502".equals(response.get("status"))) {
            log.error("检测目标网络错误");
            return false;
        }
        avitorTools avitor = new avitorTools();
        response.put("param", rule.getParams());
        response.put("method", rule.getMethod());
        response.put("url", scb.getUrl());
        response.put("dnslog", rule.getOob());
        response.put("flag", rule.getOobflag());

        avitor.setEnv(response);

        avitor.setExpression(scb.getExpression());
        Boolean re = avitor.execAvitor();
        return re;
    }
}
