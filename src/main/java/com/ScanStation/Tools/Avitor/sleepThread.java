package com.ScanStation.Tools.Avitor;


import com.ScanStation.Bean.ScanBean;
import com.commonOkHttp.CommonOkHttpClient;

import java.util.Map;

class sleepThread extends Thread {
    private CommonOkHttpClient httpClientNotSafe;
    private double[] normalRequest;
    private Thread t;
    private String threadName;
    private ScanBean scanBean;

    sleepThread(String name, double[] normalRequest, ScanBean scanBean, CommonOkHttpClient httpClientNotSafe) {
        this.threadName = name;
        this.normalRequest = normalRequest;
        this.scanBean = scanBean;
        this.httpClientNotSafe = httpClientNotSafe;
    }

    public void run() {
        Map response = httpClientNotSafe.request(scanBean);

        normalRequest[Integer.parseInt(threadName)] = Double.parseDouble((String) response.get("time"));
    }

    public void start() {
        if (t == null) {
            t = new Thread(this, threadName);
            t.start();
        }
    }
}