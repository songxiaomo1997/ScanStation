package com.ScanStation.Divider;

import com.ScanStation.Bean.PayloadBean;
import com.ScanStation.Bean.ResultBean;
import com.ScanStation.Scanner.BaseScanner;
import com.ScanStation.Scanner.Scanner;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class PassiveDivider implements Divider<PayloadBean> {
    LinkedBlockingQueue<PayloadBean> payloadBeanLinkedBlockingQueue;
    int threads;

    public PassiveDivider(LinkedBlockingQueue<PayloadBean> payloadBeanLinkedBlockingQueue, int threads) {
        this.payloadBeanLinkedBlockingQueue = payloadBeanLinkedBlockingQueue;
        this.threads = threads;

    }

    @Override
    public Boolean addQueue(PayloadBean unit) {
        try {
            payloadBeanLinkedBlockingQueue.put(unit);
            log.debug("添加扫描队列:" + unit.toString());
        } catch (InterruptedException e) {
            log.error("扫描队列添加失败" + unit.toString() + "\r\n错误信息:" + e);
            return false;
        }
        return true;
    }

    @Override
    public void scan() {
        LinkedBlockingQueue<Future<ResultBean>> Futures = new LinkedBlockingQueue<>();
        ExecutorService es = Executors.newFixedThreadPool(threads);
        Scanner scanner = new BaseScanner();
        AtomicInteger i = new AtomicInteger(1);
        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (!payloadBeanLinkedBlockingQueue.isEmpty()) {

                    log.debug("进入扫描");
                    PayloadBean payloadBean = null;
                    try {
                        payloadBean = payloadBeanLinkedBlockingQueue.take();

                        log.debug(payloadBean.toString());
                        log.debug(payloadBean.getRuleName() + "开始扫描" + payloadBeanLinkedBlockingQueue.size());
                        Futures.put(es.submit(scanner.scan(payloadBean)));
                        log.info("扫描完成:"+payloadBean.getNormalRequest().getMethod()+" "+payloadBean.getNormalRequest().getUrl()+" "+payloadBean.getNormalRequest().getParam()+" "+payloadBean.getNormalRequest().getType());
                        log.info("scanned:"+i+"---pending:"+payloadBeanLinkedBlockingQueue.size());
                        i.getAndIncrement();
                    } catch (InterruptedException e) {
                        log.error("扫描队列获取失败");
                        e.printStackTrace();
                    }
                }
                if (!Futures.isEmpty()) {
                    for (Future<ResultBean> future : Futures) {
                        if (future.isDone()) {
                            try {
                                ResultBean resultBean = future.get();
                                Futures.remove(future);
                                log.debug("剩余" + Futures.size());
                                if (resultBean.getStatus() != null) {
                                    log.info("存在漏洞:" + resultBean.getOriginalRequest().getMethod() +" "+ resultBean.getOriginalRequest().getUrl() +" "+ resultBean.getRuleName());
                                }
                            } catch (InterruptedException | ExecutionException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }

            }
        }).start();
        log.info("扫描器启动完成");
    }
}

