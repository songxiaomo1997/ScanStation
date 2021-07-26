package com.ScanStation.Producer;

import com.ScanStation.Bean.HttpBean;
import com.ScanStation.Bean.VulBean;

import java.util.ArrayList;

public interface Producer<T> {
    /**
     * 工厂模式
     * 继承接口后重写方法
     * **/
    void ProduceScan(HttpBean http);

    ArrayList<VulBean> getVul(String pocPath,Boolean isdebug);


}
