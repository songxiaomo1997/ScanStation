package com.ScanStation.Producer;

import com.ScanStation.Bean.HttpBean;
import com.ScanStation.Bean.PayloadBean;
import com.ScanStation.Bean.VulBean;
import com.ScanStation.Divider.Divider;
import com.ScanStation.Tools.Generatepayload.Payload;
import com.ScanStation.Tools.YamlTools;
import lombok.extern.log4j.Log4j2;

import java.io.File;
import java.util.ArrayList;

@Log4j2
public class ActiveProducer implements Producer<PayloadBean> {
    Divider<PayloadBean> divider;
    ArrayList<VulBean> vulBeanArrayList;

    public ActiveProducer(Divider<PayloadBean> divider) {
        this.divider = divider;
    }

    //将通过httpBean组装rule返回一个Scanerbeanlist同时将生成的scanerBean放入到分配器
    @Override
    public void ProduceScan(HttpBean http) {
        log.info("开始构造payload");
        Payload payload = new Payload();
        for (VulBean vul : this.vulBeanArrayList) {

            PayloadBean payloadBean = new PayloadBean();
            log.info("开始构造rule:" + vul.toString());

            payloadBean.setRuleName(vul.getName());
            payloadBean.setScanList(payload.insertPyload(http, vul.getRules()));
            payloadBean.setNormalRequest(payload.getnormalRequest(http, vul.getRules()));
            payloadBean.setDetail(vul.getDetail());
            payloadBean.setExpressions(vul.getRules().getExpressions());

            divider.addQueue(payloadBean);
            log.info("构造器构造paylaodbean完成:" + payloadBean.toString());
        }
    }

    //从本地获取规则组装到本类的vulbeanlist就只读取一次本地资源
    @Override
    public ArrayList<VulBean> getVul(String pocPath, Boolean isdebug) {
        YamlTools<VulBean> yamlTools = new YamlTools<VulBean>();
        ArrayList<VulBean> vulBeanArrayList = new ArrayList<>();

        if (isdebug) {
            VulBean vul = yamlTools.load(VulBean.class, pocPath);
            vulBeanArrayList.add(vul);
        } else {
            File dir = new File(pocPath);
            String[] children = dir.list();
            for (String file : children) {
                if (file.endsWith(".yaml")) {
                    VulBean vul = yamlTools.load(VulBean.class, pocPath + "/" + file);
                    vulBeanArrayList.add(vul);
                    log.info(pocPath + "/" + file + " " + vul.getName() + "已加载");
                }
            }
        }
        log.info("共加载POC:" + vulBeanArrayList.size());
        this.vulBeanArrayList = vulBeanArrayList;
        return vulBeanArrayList;
    }

}
