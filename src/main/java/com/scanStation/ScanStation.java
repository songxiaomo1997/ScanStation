package com.scanStation;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.github.monkeywie.proxyee.intercept.HttpProxyInterceptInitializer;
import com.github.monkeywie.proxyee.intercept.HttpProxyInterceptPipeline;
import com.github.monkeywie.proxyee.intercept.common.CertDownIntercept;
import com.github.monkeywie.proxyee.intercept.common.FullRequestIntercept;
import com.github.monkeywie.proxyee.intercept.common.FullResponseIntercept;
import com.github.monkeywie.proxyee.server.HttpProxyServer;
import com.github.monkeywie.proxyee.server.HttpProxyServerConfig;
import com.scanStation.bean.resultBean;
import com.scanStation.bean.vulBean;
import com.scanStation.tools.yamlTools;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

@Log4j2
public class ScanStation {
    @Parameter(names = {"-u", "--url"}, description = "url")
    private String url;

    @Parameter(names = {"--target"}, description = "target")
    private String target;

    @Parameter(names = {"-p", "--pocPath"}, description = "pocPath", required = true)
    private String pocPath = ""; //后期内置poc

    @Parameter(names = {"-c", "--cookie"}, description = "cookie")
    private String cookie = "";

    @Parameter(names = {"-gP", "--globalParam"}, description = "globalParam")
    private String globalParam = "";

    @Parameter(names = "-debug", description = "Debug mode")
    private boolean debug = false;

    @Parameter(names = {"-hC", "--headerConfig"}, description = "headerConfig")
    private String headerConfig;

    @Parameter(names = {"-t", "--threads"}, description = "threads")
    private int threads = 10;

    @Parameter(names = {"--proxy"}, description = "proxy")
    private String proxyProt;

    public static void main(String... args) {
        ScanStation scanStation = new ScanStation();
        try {
            JCommander.newBuilder().addObject(scanStation).build().parse(args);
        } catch (com.beust.jcommander.ParameterException e) {
            log.error("输入错误");
            return;
        }
        scanStation.run();
    }

    public void run() {
        ArrayList<resultBean> re = new ArrayList<>();

        if (target != null && !target.equals("")) {
            ArrayList<String> targets = getTargets(this.target);
            System.out.println(targets);
            if (targets != null) {
                for (String t : targets) {
                    log.info(t + "开始检测------------------------------------------------------------------------");
                    ActiveScan(re, t);
                    log.info(t + "检测结束------------------------------------------------------------------------");
                }
            }
        } else if (url != null && !url.equals("")) {
            log.info(this.url + "开始检测------------------------------------------------------------------------");
            ActiveScan(re, this.url);
            log.info(this.url + "检测结束------------------------------------------------------------------------");
        }

        if (re.size() > 0) {
            for (resultBean r : re) {
                System.out.println(r.toString() + "\r\n--------------------------------------------\r\n");
            }
        }
    }

    private ArrayList<String> getTargets(String target) {
        ArrayList<String> targets = new ArrayList<>();
        String tmp = "";
        try {
            BufferedReader in = new BufferedReader(new FileReader(target));
            while ((tmp = in.readLine()) != null) {
                if (!tmp.equals("")) {
                    targets.add(tmp);
                }
                log.info("添加目标" + tmp);
            }
        } catch (FileNotFoundException e) {
            log.debug(target + "文件不存在");
            return targets;
        } catch (IOException e) {
            e.printStackTrace();
            return targets;
        }
        return targets;
    }

    private void ActiveScan(ArrayList<resultBean> re, String url) {
        if (!debug) {
            //多线程扫描默认10个线程
            Queue<String> files = getPocs(this.pocPath);
            scanThread(re, files, url);
        } else {
            yamlTools yamlTools = new yamlTools();
            vulBean vul = yamlTools.vulGet(pocPath, url, globalParam, cookie);
            scanner scan = new scanner(vul, headerConfig);
            resultBean result = scan.scan();
            if (result != null) {
                re.add(result);
            }
        }
    }

    @NotNull
    private Queue<String> getPocs(String pocPath) {
        File dir = new File(pocPath);
        String[] children = dir.list();
        Queue<String> files = new LinkedList<>();
        for (String file : children) {
            files.offer(pocPath + "/" + file);
        }
        return files;
    }

    private void scanThread(ArrayList<resultBean> re, Queue<String> files, String url) {
        ExecutorService es = Executors.newFixedThreadPool(threads);
        int size = files.size();
        boolean closeable = false;
        while (!closeable) {
            for (int i = 0; i < size; i++) {
                String tmp = files.poll();
                if (tmp != null) {
                    es.submit(new ScanThread(String.valueOf(i), tmp, url, globalParam, cookie, headerConfig, re));
                } else {
                    es.shutdown();
                }
            }
            if (files.size() == 0 && es.isTerminated()) {
                closeable = true;
            }
        }
    }
}
