package com.ScanStation.Tools;

import lombok.extern.slf4j.Slf4j;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.SafeConstructor;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

@Slf4j
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
//        Yaml yaml = new Yaml(new SafeConstructor()); //避免yaml反序列化
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
//        Yaml yaml = new Yaml(new SafeConstructor()); //避免yaml反序列化
        Yaml yaml = new Yaml();
        T t = (T) yaml.loadAs(inputStream, type);
        return t;
    }
}
