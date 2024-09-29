package com.example.pmg302_project;

import java.util.HashMap;
import java.util.Map;

public class InMemoryStorage {
    private static final Map<String, String> storage = new HashMap<>();

    public static void save(String key, String value) {
        storage.put(key, value);
    }

    public static String get(String key) {
        return storage.get(key);
    }

    public static void clear() {
        storage.clear();
    }
}
