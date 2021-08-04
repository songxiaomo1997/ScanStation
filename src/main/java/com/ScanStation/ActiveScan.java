package com.ScanStation;

import com.ScanStation.Bean.CommandBean;
import com.ScanStation.Bean.HttpBean;
import com.ScanStation.Bean.PayloadBean;
import com.ScanStation.Bean.ScanBean;
import com.ScanStation.Divider.ActiveDivider;
import com.ScanStation.Divider.Divider;
import com.ScanStation.Producer.ActiveProducer;
import com.ScanStation.Producer.Producer;
import com.ScanStation.Tools.YamlTools;
import lombok.extern.slf4j.Slf4j;
import org.yaml.snakeyaml.Yaml;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

@Slf4j
public class ActiveScan {
    CommandBean cmd;

    ActiveScan(CommandBean cmd) {
        this.cmd = cmd;
    }

    /**
     * 1.构造httpBean添加需要的内容
     * 2.传入httpBean到ActiveProducer
     * 3.ActiveProducer制造scan队列
     * 4.divider使用scanner进行扫描
     * 5.返回扫描结果
     **/
    public void scan() {

        log.info("----------------主动扫描开始----------------");
        LinkedBlockingQueue<PayloadBean> payloadBeanLinkedBlockingQueue = new LinkedBlockingQueue<>();

        Divider<PayloadBean> divider = new ActiveDivider(payloadBeanLinkedBlockingQueue, cmd.getThreads());
        Producer<PayloadBean> producer = new ActiveProducer(divider);
        producer.getVul(cmd.getPocPath(),cmd.isDebug());

        //构造payload插入到分配器队列中
        if (cmd.getTarget() != null) {
            ArrayList<String> targets = getTargets(cmd.getTarget());
            for (String url : targets) {
                HttpBean http = getHttp(url);
                producer.ProduceScan(http);
            }
        } else {
            HttpBean http = getHttp(cmd.getUrl());
            log.debug("HttpBean构造完成:" + http.toString());
            producer.ProduceScan(http);
        }

        //开始扫描
        divider.scan();
        log.info("----------------主动扫描完成----------------");
    }

    //用于获取httpbean
    private HttpBean getHttp(String url) {
        HttpBean http = new HttpBean();
        http.setUrl(url);
        http.setParam(cmd.getGlobalParam());
        //添加cookie
        SetHeader(http);
        //添加全局参数

        if (cmd.getGlobalParam() != null && !"".equals(cmd.getGlobalParam())) {
            http.setParam(cmd.getGlobalParam());
        }

        return http;
    }

    private void SetHeader(HttpBean http) {
        Map<String, String> header = new HashMap<>();

        //添加全局请求头
        if (cmd.getHeaderConfig() != null && !"".equals(cmd.getHeaderConfig())) {
            YamlTools<Map<String, String>> yamlTools = new YamlTools<Map<String, String>>(cmd.getHeaderConfig());
            header = yamlTools.load(Map.class);
        } else {
            Yaml yaml = new Yaml();
            header = yaml.loadAs(ActiveScan.class.getResourceAsStream("/config/headconfig.yaml"), Map.class);

        }

        if (cmd.getCookie() != null && !"".equals(cmd.getCookie())) {
            header.put("cookie", cmd.getCookie());
        }

        //统一添加header头
        if (http.getHeader() == null) {
            http.setHeader(header);
        } else {
            Map<String, String> tmp = http.getHeader();
            header.forEach(tmp::put);
            http.setHeader(tmp);
        }

    }

    private ArrayList<String> getTargets(String target) {
        ArrayList<String> targets = new ArrayList<>();
        String tmp = "";
        try {
            BufferedReader in = new BufferedReader(new FileReader(target));
            while ((tmp = in.readLine()) != null) {
                tmp=tmp.replaceAll("\r\n|\r|\n", "");
                if (!tmp.equals("")) {
                    targets.add(tmp);
                    log.info("添加目标" + tmp);
                }
            }
        } catch (FileNotFoundException e) {
            log.error(target + "文件不存在");
            return targets;
        } catch (IOException e) {
            e.printStackTrace();
            return targets;
        }
        return targets;
    }

}
