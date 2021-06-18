package com.scanStation.tools;


import com.scanStation.commonOkHttp.CommonOkHttpClient;

import java.util.Map;

class sleepThread extends Thread {
    private CommonOkHttpClient httpClientNotSafe;
    private Map<String, Object> env;
    private double[] normalRequest;
    private Thread t;
    private String threadName;

    sleepThread(String name, double[] normalRequest, Map<String, Object> env, CommonOkHttpClient httpClientNotSafe) {
        this.threadName = name;
        this.normalRequest = normalRequest;
        this.env = env;
        this.httpClientNotSafe = httpClientNotSafe;
    }

    public void run() {
        Map response = httpClientNotSafe.request((String) env.get("url"),(Map) env.get("param"), (String) env.get("method"));
        normalRequest[Integer.parseInt(threadName)] = Double.valueOf((String) response.get("time"));
    }

    public void start() {
        if (t == null) {
            t = new Thread(this, threadName);
            t.start();
        }
    }
}