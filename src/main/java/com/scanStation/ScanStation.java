package com.scanStation;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.scanStation.bean.resultBean;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ScanStation {
    @Parameter(names = {"-u","--url"},description = "url",required = true)
    private String url;

    @Parameter(names = {"-p","--pocPath"},description = "pocPath",required = true)
    private String pocPath="";

    @Parameter(names = {"-c","--cookie"},description = "cookie")
    private String cookie="";

    @Parameter(names = {"-gP","--globalParam"},description = "globalParam")
    private String globalParam="";

    @Parameter(names = "-debug", description = "Debug mode")
    private boolean debug = false;

    @Parameter(names = {"-hC","--headerConfig"}, description = "headerConfig")
    private String headerConfig;

    @Parameter(names = {"-t","--threads"}, description = "threads")
    private int threads=10;

    public static void main(String...args) {
        ScanStation scanStation = new ScanStation();
        JCommander.newBuilder().addObject(scanStation).build().parse(args);
        scanStation.run();
    }

    public void run(){
        ArrayList<resultBean> re = new ArrayList<>();
        if (!debug){

            File dir = new File(pocPath);
            String[] children = dir.list();
            Queue<String> files = new LinkedList<>();
            for (String file : children) {
                if (file.endsWith(".yaml")) {
                    files.offer(pocPath + "/" + file);
                }
            }
            ExecutorService es = Executors.newFixedThreadPool(threads);
            int size = files.size();
            boolean closeable = false;
            while (!closeable){
                for (int i = 0; i < size; i++) {
                    String tmp = files.poll();
                    if (tmp != null) {
                        es.submit(new ScanThread(String.valueOf(i),tmp,url,globalParam,cookie,headerConfig,re));
                    } else {
                        es.shutdown();
                    }
                }
                if (files.size() == 0 && es.isTerminated()) {
                    closeable = true;
                }
            }
        }else {
            scanner scan = new scanner(pocPath,url,globalParam,cookie,headerConfig);
            resultBean result = scan.scan();
            if(result!=null){
                re.add(result);
            }
        }
        for (resultBean r : re) {
            System.out.println(r.toString()+"\r\n--------------------------------------------\r\n");
        }
    }
}
