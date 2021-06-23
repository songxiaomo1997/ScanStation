package com.scanStation.tools.Generatepayload;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class replaceJson {
    public static void revar(Map<String, Object> map, String key, String var, String mode) {
        for (Map.Entry entry : map.entrySet()) {
//            System.out.println(entry.getKey() + "+++" + entry.getValue() + "+++" + entry.getValue().getClass());
            if (entry.getValue() instanceof Map) {
                revar((Map<String, Object>) entry.getValue(), key, var, mode);
            } else if (entry.getValue() instanceof List) {
                for (int i = 0; i < ((List<?>) entry.getValue()).size(); i++) {
                    if (((List<?>) entry.getValue()).get(i) instanceof Map) {
                        revar((Map<String, Object>) ((List<?>) entry.getValue()).get(i), key, var, mode);
                    }
                }
                if (key.equals(entry.getKey())) {
                    ArrayList<String> arrayList = ((ArrayList<String>) entry.getValue());
                    arrayList.add(var);
                    map.put(String.valueOf(entry.getKey()), arrayList);
                }
            } else if (key.equals(entry.getKey())) {
                if (mode.equals("append")) {
                    map.put(key, entry.getValue() + var);
                } else if (mode.equals("replace")) {
                    map.put(key, var);
                }
            }
        }

    }

    public Map<String, Object> replace(String json, String key, String var){
        GsonBuilder builder = new GsonBuilder();
        Map<String, Object> map =new HashMap<>();
        Gson gson = builder.create();
        try {
            map = gson.fromJson(json, Map.class);
            revar(map, key, var, "append");
        }catch (JsonSyntaxException e){
            e.printStackTrace();
        }
//        System.out.println(gson.toJson(map));
        return map;
    }
}
