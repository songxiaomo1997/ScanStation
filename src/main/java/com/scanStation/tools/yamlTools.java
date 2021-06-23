package com.scanStation.tools;

import com.scanStation.bean.vulBean;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class yamlTools {
    private String filepath;

    public yamlTools(String filepath) {
        this.filepath = filepath;
    }


    public vulBean load() {
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(filepath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Yaml yaml = new Yaml();
        vulBean vulBean = yaml.loadAs(inputStream, vulBean.class);
        return vulBean;
    }
}
