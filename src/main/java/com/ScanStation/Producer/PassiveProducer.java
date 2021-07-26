package com.ScanStation.Producer;

import com.ScanStation.Bean.HttpBean;
import com.ScanStation.Bean.PayloadBean;
import com.ScanStation.Bean.VulBean;
import com.ScanStation.Divider.Divider;

import java.util.ArrayList;

public class PassiveProducer extends ActiveProducer{
    public PassiveProducer(Divider<PayloadBean> divider) {
        super(divider);
    }

    @Override
    public void ProduceScan(HttpBean http) {
    }

    @Override
    public ArrayList<VulBean> getVul(String pocPath, Boolean isdebug) {
        return null;
    }

}
