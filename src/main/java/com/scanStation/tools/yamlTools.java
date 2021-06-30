package com.scanStation.tools;

import com.scanStation.bean.ruleBean;
import com.scanStation.bean.vulBean;
import lombok.extern.log4j.Log4j2;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Map;

@Log4j2
public class yamlTools {
    private String filepath;

    public yamlTools() {
    }

    public yamlTools(String filepath) {
        this.filepath = filepath;
    }

    public Map<String, String> load(String filepath) {
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(filepath);
        } catch (FileNotFoundException e) {
            log.error(this.filepath+"不存在");
            return null;
        }
        Yaml yaml = new Yaml();
        Map<String, String> map = yaml.loadAs(inputStream, Map.class);
        return map;
    }

    public vulBean load() {
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(this.filepath);
        } catch (FileNotFoundException e) {
            log.error(this.filepath+"不存在");
            return null;
        }
        Yaml yaml = new Yaml();
        return yaml.loadAs(inputStream, vulBean.class);
    }

    public vulBean vulGet(String file, String url, String globalParam, String cookie) {
        vulBean vul = new yamlTools(file).load();
        vul.getRules().setUrl(url);
        ruleBean rule = vul.getRules();
        rule.setUrl(url);
        if (globalParam != null && !globalParam.equals("")) {
            rule.setGlobalParam(globalParam);
        }
        if (cookie != null && !cookie.equals("")) {
            rule.setCookie(cookie);
        }
//        rule.setOob("123123.dns.com");
        return vul;
    }
}
