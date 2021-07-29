package com.ScanStation.Tools;

import com.ScanStation.Bean.HttpBean;
import com.ScanStation.Bean.PayloadBean;
import com.ScanStation.Decomposer.HttpDecomposerImp;
import com.ScanStation.Divider.ActiveDivider;
import com.ScanStation.Divider.Divider;
import com.ScanStation.Producer.ActiveProducer;
import com.ScanStation.Producer.Producer;
import com.ScanStation.Proxy;
import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;

@Log4j2
public class testPassive {
    public static void main(String[] args) {
        LinkedBlockingQueue<HttpBean> httpQueue = new LinkedBlockingQueue<HttpBean>();
        ArrayList<String> targets = new ArrayList<>();
        targets.add("*");
        HttpDecomposerImp decomposer = new HttpDecomposerImp(httpQueue,targets);

        int prot = 1111;
        new Thread(() -> {
            Proxy proxy = new Proxy(decomposer);
            proxy.startProxy(prot);
        }).start();


        LinkedBlockingQueue<PayloadBean> payloadBeanLinkedBlockingQueue = new LinkedBlockingQueue<>();
        Divider<PayloadBean> divider = new ActiveDivider(payloadBeanLinkedBlockingQueue, 10);
        Producer<PayloadBean> producer = new ActiveProducer(divider);
        producer.getVul("/Users/song/tools/rule/", false);

        new Thread(() -> {
            while (true) {
                if (!decomposer.getQueue().isEmpty()) {
                    try {
                        HttpBean http = httpQueue.take();
                        log.info("HttpBean构造完成:" + http.toString());
                        log.info("剩余httpbean共:" + httpQueue.size());
                        producer.ProduceScan(http);
                        divider.scan();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        }).start();

        log.info("over");
    }
}
