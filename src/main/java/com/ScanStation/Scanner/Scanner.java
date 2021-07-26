package com.ScanStation.Scanner;

import com.ScanStation.Bean.PayloadBean;
import com.ScanStation.Bean.ResultBean;

import java.util.concurrent.Callable;

public interface Scanner {
    /**
     * 只通过scanBean扫描返回ResultBean漏洞存在或不存在
     * 使用多线程
     *
     * @return*/
    Callable<ResultBean> scan(PayloadBean payload);

    ResultBean getResult(PayloadBean payload);

}
