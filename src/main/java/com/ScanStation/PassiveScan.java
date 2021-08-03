package com.ScanStation;

import com.ScanStation.Bean.CommandBean;
import com.ScanStation.Bean.HttpBean;
import com.ScanStation.Bean.PayloadBean;
import com.ScanStation.Decomposer.HttpDecomposerImp;
import com.ScanStation.Divider.ActiveDivider;
import com.ScanStation.Divider.Divider;
import com.ScanStation.Producer.ActiveProducer;
import com.ScanStation.Producer.Producer;
import com.ScanStation.Tools.YamlTools;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.LinkedBlockingQueue;

@Slf4j
public class PassiveScan {
    CommandBean cmd;

    PassiveScan(CommandBean cmd) {
        log.info(cmd.toString());
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
        log.info("----------------被动扫描开始----------------");
        //启动代理构造httpbean队列
        LinkedBlockingQueue<HttpBean> httpQueue = new LinkedBlockingQueue<HttpBean>();
        ArrayList<String> targets = new ArrayList<>();
        if (cmd.getTarget() != null&&!cmd.getTarget().equals("")) {
            File file = new File(cmd.getTarget());
            if (file.exists()) {
                YamlTools<ArrayList<String>> yamlTools = new YamlTools<>(cmd.getTarget());
                targets.addAll(yamlTools.load(ArrayList.class));
            } else {
                targets.addAll(Arrays.asList(cmd.getTarget().split(",")));
            }
        }else {
            targets.add("*");
        }
        HttpDecomposerImp decomposer = new HttpDecomposerImp(httpQueue,targets);

        int prot = cmd.getProxyProt();
        new Thread(() -> {
            Proxy proxy = new Proxy(decomposer);
            proxy.startProxy(prot);
        }).start();


        LinkedBlockingQueue<PayloadBean> payloadBeanLinkedBlockingQueue = new LinkedBlockingQueue<>();
        Divider<PayloadBean> divider = new ActiveDivider(payloadBeanLinkedBlockingQueue, cmd.getThreads());
        Producer<PayloadBean> producer = new ActiveProducer(divider);
        producer.getVul(cmd.getPocPath(), cmd.isDebug());

        new Thread(() -> {
            while (true) {
                if (!decomposer.getQueue().isEmpty()) {
                    try {
                        HttpBean http = httpQueue.take();
                        log.debug("HttpBean构造完成:" + http.toString());
                        log.debug("剩余httpbean共:" + httpQueue.size());
                        producer.ProduceScan(http);
                        divider.scan();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        }).start();

        log.info("代理启动完成,POC加载完成");
    }

    //用于获取httpbean

}
