package com.ScanStation;

import com.ScanStation.Bean.CommandBean;
import com.beust.jcommander.JCommander;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class ScanStation {
    public static void main(String[] args) {
        CommandBean cmd = new CommandBean();
        try {
            JCommander.newBuilder().addObject(cmd).build().parse(args);
        } catch (Exception e) {
            log.error("命令错误");
            return;
        }
        if (cmd.getActive()) {
            //主动扫描获取规则构造一个Scan的队列
            ActiveScan activeScan = new ActiveScan(cmd);
            activeScan.scan();
        } else if (cmd.getPassive()) {
            //被动扫描启动代理 构造一个
            PassiveScan passiveScan = new PassiveScan(cmd);
            passiveScan.scan();
        }
    }
}
