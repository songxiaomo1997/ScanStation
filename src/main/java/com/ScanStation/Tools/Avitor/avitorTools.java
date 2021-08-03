package com.ScanStation.Tools.Avitor;

import com.googlecode.aviator.AviatorEvaluator;
import com.googlecode.aviator.Expression;
import com.googlecode.aviator.runtime.function.AbstractFunction;
import com.googlecode.aviator.runtime.type.AviatorBoolean;
import com.googlecode.aviator.runtime.type.AviatorObject;
import com.commonOkHttp.CommonOkHttpClient;
import com.commonOkHttp.CommonOkHttpClientBuilder;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
public class avitorTools {

    public avitorTools() {
        new avitorTools("", null);
    }

    public avitorTools(String expression) {
        new avitorTools(expression, null);
    }

    public avitorTools(Map<String, Object> env) {
        new avitorTools(null, env);
    }

    public avitorTools(String expression, Map<String, Object> env) {
        AviatorEvaluator.addFunction(new sleepFunction());

    }

    public Boolean execAvitor(String expression,Map<String, Object> env) {
        Expression compiledExp = AviatorEvaluator.compile(expression);
        return (Boolean) compiledExp.execute(env);
    }

//    static class oobFunction extends AbstractFunction {
//        //传入正常请求ruleBean ， 漏洞响应包
//        @Override
//        public AviatorObject call(Map<String, Object> env) {
//            CommonOkHttpClient httpClientNotSafe = new CommonOkHttpClientBuilder().unSafe(true).build();
//            Map response =httpClientNotSafe.get("http://"+"www.dnslog.cn",null,null,null);
//
//            String body = (String) response.get("body");
//            log.info("aaa"+response);
//
//            if (body.contains(env.get("oobflag")+"."+env.get("oob"))) {
//                return AviatorBoolean.TRUE;
//            }else {
//                return AviatorBoolean.FALSE;
//            }
//        }
//
//        @Override
//        public String getName() {
//            return "oob";
//        }
//    }
}
