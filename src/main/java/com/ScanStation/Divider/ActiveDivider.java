package com.ScanStation.Divider;

import com.ScanStation.Bean.PayloadBean;
import com.ScanStation.Bean.ResultBean;
import com.ScanStation.Scanner.BaseScanner;
import com.ScanStation.Scanner.Scanner;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;

@Slf4j
public class ActiveDivider implements Divider<PayloadBean> {
    LinkedBlockingQueue<PayloadBean> payloadBeanLinkedBlockingQueue;
    int threads;

    public ActiveDivider(LinkedBlockingQueue<PayloadBean> payloadBeanLinkedBlockingQueue, int threads) {
        this.payloadBeanLinkedBlockingQueue = payloadBeanLinkedBlockingQueue;
        this.threads = threads;

    }

    @Override
    public Boolean addQueue(PayloadBean unit) {
        try {
            payloadBeanLinkedBlockingQueue.put(unit);
        } catch (InterruptedException e) {
            log.error("扫描队列添加失败" + unit.toString() + "\r\n错误信息:" + e);
            return false;
        }
        return true;
    }

    @Override
    public void scan() {
        log.info("扫描开始共有:" + payloadBeanLinkedBlockingQueue.size() + "个payloadbean");

        LinkedBlockingQueue<Future<ResultBean>> Futures = new LinkedBlockingQueue<>();
        ExecutorService es = Executors.newFixedThreadPool(threads);
        Scanner scanner = new BaseScanner();
        int size = payloadBeanLinkedBlockingQueue.size();
        boolean closeable = false;
        while (!closeable) {
            for (int i = 0; i < size; i++) {
                PayloadBean payloadBean = null;
                try {
                    payloadBean = payloadBeanLinkedBlockingQueue.take();
                    log.debug(payloadBean.toString());
                    log.info(payloadBean.getRuleName() + "开始扫描" + payloadBeanLinkedBlockingQueue.size());
                    Futures.put(es.submit(scanner.scan(payloadBean)));
                } catch (InterruptedException e) {
                    log.error("扫描队列获取失败");
                    e.printStackTrace();
                }

                if (payloadBeanLinkedBlockingQueue.size() == 0) {
                    log.info("扫描完成关闭线程");
                    es.shutdown();
                    log.info("扫描完成关闭线程完成,准备开始获取结果");
                }
            }

            for (Future<ResultBean> future : Futures) {
                if (future.isDone()) {
                    try {
                        ResultBean resultBean = future.get();
                        Futures.remove(future);
                        log.info("剩余"+Futures.size());
                        if (resultBean.getStatus()!=null){
                            log.info("开始获取"+resultBean.getRuleName()+"结果");
                            log.info("resultBean:" + resultBean.toString());
                        }
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }
                }
            }
            if (es.isTerminated()) {
                closeable = true;
                log.info("关闭");
            }
        }
        //
    }
}

