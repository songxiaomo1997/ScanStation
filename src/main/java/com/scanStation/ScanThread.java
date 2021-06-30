package com.scanStation;

import com.scanStation.bean.resultBean;
import com.scanStation.bean.vulBean;
import com.scanStation.tools.yamlTools;

import java.util.ArrayList;
import java.util.Map;

public class ScanThread extends Thread {
    private Thread t;
    private String threadName;

    String file;
    String url;
    String globalParam;
    String cookie;
    String headerConfig;
    ArrayList<resultBean> result;

    ScanThread(String name, String file, String url, String globalParam, String cookie, String headerConfig, ArrayList<resultBean> result) {
        this.threadName = name;
        this.file = file;
        this.url = url;
        this.globalParam = globalParam;
        this.cookie = cookie;
        this.headerConfig = headerConfig;
        this.result = result;
    }

    public void run() {
        resultBean re = new resultBean();
        yamlTools yamlTools = new yamlTools();
        vulBean vul = yamlTools.vulGet(file, url, globalParam, cookie);
        scanner scan = new scanner(vul, headerConfig);
        re = scan.scan();
        if (re != null) {
            result.add(re);
        }
    }

    public void start() {
        if (t == null) {
            t = new Thread(this, threadName);
            t.start();
        }
    }
}
