package com.scanStation;

import com.scanStation.bean.resultBean;

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

    ScanThread(String name,String file, String url, String globalParam, String cookie, String headerConfig, ArrayList<resultBean> result){
        this.threadName=name;
        this.file = file;
        this.url = url;
        this.globalParam =globalParam;
        this.cookie=cookie;
        this.headerConfig=headerConfig;
        this.result = result;
    }
    public void run() {
        scanner scan = new scanner(file,url,globalParam,cookie,headerConfig);
        resultBean re = scan.scan();
        if(re!=null) {
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
