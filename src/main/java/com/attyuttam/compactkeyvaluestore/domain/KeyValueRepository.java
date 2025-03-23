package com.attyuttam.compactkeyvaluestore.domain;

import java.util.Map;

public interface KeyValueRepository {
    void put(String key, String value);
    String get(String key);
    Map<String, String> getAll();
}
