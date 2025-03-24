package com.attyuttam.compactkeyvaluestore.infra.persistence;

import com.attyuttam.compactkeyvaluestore.domain.KeyValueRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
@Slf4j
public class InfraKeyValueRepository implements KeyValueRepository {
    ConcurrentHashMap<String, String> keyValueStore = new ConcurrentHashMap<>();
    @Override
    public void put(String key, String value) {
        log.info("INSERTING KEY={} VALUE={}",key,value);
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
