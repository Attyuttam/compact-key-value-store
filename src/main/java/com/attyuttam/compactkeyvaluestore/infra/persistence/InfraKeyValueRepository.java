package com.attyuttam.compactkeyvaluestore.infra.persistence;

import com.attyuttam.compactkeyvaluestore.domain.KeyValueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class InfraKeyValueRepository implements KeyValueRepository {
    ConcurrentHashMap<String, String> keyValueStore = new ConcurrentHashMap<>();
    @Override
    public void put(String key, String value) {
        keyValueStore.put(key, value);
    }

    @Override
    public String get(String key) {
        return keyValueStore.getOrDefault(key, "");
    }

    @Override
    public Map<String, String> getAll() {
        return keyValueStore;
    }
}
