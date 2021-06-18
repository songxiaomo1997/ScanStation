package com.scanStation.tools;

import com.googlecode.aviator.AviatorEvaluator;
import com.googlecode.aviator.Expression;
import com.googlecode.aviator.runtime.function.AbstractFunction;
import com.googlecode.aviator.runtime.type.AviatorBoolean;
import com.googlecode.aviator.runtime.type.AviatorObject;
import com.scanStation.commonOkHttp.CommonOkHttpClient;
import com.scanStation.commonOkHttp.CommonOkHttpClientBuilder;
import lombok.extern.log4j.Log4j2;

import java.util.Map;

@Log4j2
public class avitorTools {
    private String expression;
    private Map<String, Object> env;
    private Expression compiledExp;

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
        AviatorEvaluator.addFunction(new avitorTools.sleepFunction());
        AviatorEvaluator.addFunction(new avitorTools.oobFunction());
    }

    public Boolean execAvitor() {

        Boolean result = (Boolean) this.compiledExp.execute(this.env);
        return result;
    }

    public Map<String, Object> getEnv() {
        return env;
    }

    public void setEnv(Map<String, Object> env) {
        this.env = env;
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.compiledExp = AviatorEvaluator.compile(expression);
    }

    static class sleepFunction extends AbstractFunction {
        //传入正常请求ruleBean ， 漏洞响应包
        @Override
        public AviatorObject call(Map<String, Object> env) {
            //正常请求30次获取响应时间
            // max = 正常请求平均值+TIME_STDEV_COEFF(偏差倍数默认7倍)*正常请求的标准差
            // min = 正常请求平均值-TIME_STDEV_COEFF(偏差倍数默认7倍)*正常请求的标准差
            double[] normalRequest = new double[30];
            double max;
            double min;
            Calc calc = new Calc();
            CommonOkHttpClient httpClientNotSafe = new CommonOkHttpClientBuilder().unSafe(true).build();
            for (int i = 0; i < 30; i++) {
                sleepThread sleepThread = new sleepThread(String.valueOf(i), normalRequest, env, httpClientNotSafe);
                sleepThread.start();
            }
            max = calc.average(normalRequest) + calc.stdev(normalRequest) * 7;
            min = calc.average(normalRequest) - calc.stdev(normalRequest) * 7;

            double current = Double.valueOf((String) env.get("time"));
            return Math.max(min, current) == Math.max(current, max) && max + min != 0 ? AviatorBoolean.TRUE : AviatorBoolean.FALSE;
        }

        @Override
        public String getName() {
            return "sleep";
        }
    }

    static class oobFunction extends AbstractFunction {
        //传入正常请求ruleBean ， 漏洞响应包
        @Override
        public AviatorObject call(Map<String, Object> env) {
            CommonOkHttpClient httpClientNotSafe = new CommonOkHttpClientBuilder().unSafe(true).build();
            Map response =httpClientNotSafe.request("http://"+"www.dnslog.cn"+"/getrecords.php?t=0.2111003315964448",null,"GET");
            String body = (String) response.get("body");
            log.info("aaa"+response);
            if (body.contains(env.get("oobflag")+"."+env.get("oob"))) {
                return AviatorBoolean.TRUE;
            }else {
                return AviatorBoolean.FALSE;
            }
        }

        @Override
        public String getName() {
            return "oob";
        }
    }
}
