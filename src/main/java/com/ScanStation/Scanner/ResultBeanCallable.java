package com.ScanStation.Scanner;

import com.ScanStation.Bean.ResultBean;

import java.util.concurrent.Callable;

public class ResultBeanCallable implements Callable<ResultBean> {
    ResultBean resultBean;
    ResultBeanCallable(ResultBean resultBean){
        this.resultBean = resultBean;
    }
    @Override
    public ResultBean call() {
        return this.resultBean;
    }
}
