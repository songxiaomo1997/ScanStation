package com.scanStation;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.scanStation.bean.*;
import com.scanStation.commonOkHttp.*;
import com.scanStation.tools.Generatepayload.payload;
import com.scanStation.tools.*;
import lombok.extern.log4j.Log4j2;
import org.python.core.PyDictionary;
import org.python.core.*;
import org.python.core.PyFunction;
import org.python.util.PythonInterpreter;
import org.yaml.snakeyaml.Yaml;

import java.util.*;

/**
 * 分离获取vulbean
 * 初始化只传入一个vulbean对象
 **/
@Log4j2
public class scanner {
    vulBean vul;
    String headerConfig;
    String pyfile;

    public String getHeaderConfig() {
        return headerConfig;
    }

    public void setHeaderConfig(String headerConfig) {
        this.headerConfig = headerConfig;
    }

    public vulBean getVul() {
        return vul;
    }

    public void setVul(vulBean vul) {
        this.vul = vul;
    }

    public scanner(vulBean vul, String headerConfig) {
        this.vul = vul;
        this.headerConfig = headerConfig;
    }


    public resultBean pyscan() {
        resultBean result = new resultBean();
        PythonInterpreter interpreter = new PythonInterpreter();
        interpreter.execfile(pyfile);
        PyFunction pyFunction = interpreter.get("run", PyFunction.class);
        PyDictionary pyargs = new PyDictionary();
        pyargs.put("url", "1.1.1.1"); //传入参数标准 ip port url cookie,全局参数,全局请求头
        PyObject pyObject = pyFunction.__call__(pyargs);
        String json = String.valueOf(pyObject);
        //对返回数据进行处理判断如果存在则封装一个resultBean返回
        return result;
    }

    public resultBean scan() {
        log.info("------------------------------------检测开始------------------------------------");

        //加载规则
        ruleBean rule = this.vul.getRules();


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
            ArrayList<scannerBean> result = new ArrayList<>();
            if (payloadAndExpression.size() > 0) {
                for (scannerBean scb : payloadAndExpression) {
                    if (scb.getResult()) {
                        result.add(scb);
                        log.info("++++++++++++vulFind:" + vul.getName() + "-" + scb.toString() + " " + vul.getDetail());
                    }
                }
                if (result.size() > 0) {
                    resultBean results = new resultBean(vul.getName(), vul.getDetail(), result);
                    return results;
                } else {
                    return null;
                }
            }
        }
        log.info("---expressions判断完成");
        log.info("------------------------------------检测完成------------------------------------");
        return null;
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
        if (this.headerConfig != null && !"".equals(this.headerConfig)) {
            Map<String, String> header = new yamlTools().load(headerConfig);
            httpClientNotSafe.setHeaderExt(header);
        } else {
            System.out.println(this.getClass().getClassLoader().getResourceAsStream("config.yaml"));
            Yaml yaml = new Yaml();
            Map<String, String> header = yaml.loadAs(this.getClass().getClassLoader().getResourceAsStream("config.yaml"), Map.class);
            httpClientNotSafe.setHeaderExt(header);
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