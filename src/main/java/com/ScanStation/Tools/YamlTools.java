package com.ScanStation.Tools;

import lombok.extern.log4j.Log4j2;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Map;

@Log4j2
public class YamlTools<T> {
    String filePath;

    public YamlTools() {
        this(null);
    }

    public YamlTools(String filePath) {
        this.filePath = filePath;
    }

    public T load(java.lang.Class<T> type, String filePath) {
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(filePath);
        } catch (FileNotFoundException e) {
            log.error(filePath + "不存在");
            System.exit(0);
        }
        Yaml yaml = new Yaml();
        return yaml.loadAs(inputStream, type);
    }

    public T load(Class type) {
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(this.filePath);
        } catch (FileNotFoundException e) {
            log.error(this.filePath + "不存在");
            return null;
        }
        Yaml yaml = new Yaml();
        T t = (T) yaml.loadAs(inputStream, type);
        return t;
    }
}
