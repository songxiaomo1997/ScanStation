package com.ScanStation.Divider;

public interface Divider<T> {

    //扫描队列添加
    Boolean addQueue(T unit);

    //从本类的队列中获取调用扫描扫描将结果记录
    void scan();

}
