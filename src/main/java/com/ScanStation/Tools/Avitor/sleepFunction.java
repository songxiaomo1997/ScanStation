package com.ScanStation.Tools.Avitor;

import com.ScanStation.Bean.ScanBean;
import com.commonOkHttp.CommonOkHttpClient;
import com.commonOkHttp.CommonOkHttpClientBuilder;
import com.googlecode.aviator.runtime.function.AbstractFunction;
import com.googlecode.aviator.runtime.type.AviatorBoolean;
import com.googlecode.aviator.runtime.type.AviatorObject;

import java.util.Map;

public class sleepFunction extends AbstractFunction {
    //传入正常请求ruleBean ， 漏洞响应包
    @Override
    public AviatorObject call(Map<String, Object> env) {
        ScanBean normalRequest = (ScanBean) env.get("normalrequest"); //获取原始请求
        ScanBean scaned = (ScanBean) env.get("scaned");
        //正常请求30次获取响应时间
        // max = 正常请求平均值+TIME_STDEV_COEFF(偏差倍数默认7倍)*正常请求的标准差
        // min = 正常请求平均值-TIME_STDEV_COEFF(偏差倍数默认7倍)*正常请求的标准差
        double[] normalRequestTimes = new double[30];
        double max;
        double min;
        Calc calc = new Calc();
        CommonOkHttpClient httpClientNotSafe = new CommonOkHttpClientBuilder().unSafe(true).build();
        for (int i = 0; i < 30; i++) {
            sleepThread sleepThread = new sleepThread(String.valueOf(i), normalRequestTimes, normalRequest, httpClientNotSafe);
            sleepThread.start();
        }
        max = calc.average(normalRequestTimes) + calc.stdev(normalRequestTimes) * 7;
        min = calc.average(normalRequestTimes) - calc.stdev(normalRequestTimes) * 7;

        double current = Double.parseDouble(scaned.getResponse().get("time"));
        return Math.max(min, current) == Math.max(current, max) && max + min != 0 ? AviatorBoolean.TRUE : AviatorBoolean.FALSE;
    }

    @Override
    public String getName() {
        return "sleep";
    }
}