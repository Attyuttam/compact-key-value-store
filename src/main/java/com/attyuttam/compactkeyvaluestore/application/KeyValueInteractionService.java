package com.attyuttam.compactkeyvaluestore.application;

import com.attyuttam.compactkeyvaluestore.domain.KeyValue;
import com.attyuttam.compactkeyvaluestore.domain.KeyValueRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;


@Component
@RequiredArgsConstructor
public class KeyValueInteractionService {
    private final KeyValueRepository keyValueRepository;
    private final ObjectMapper objectMapper;

    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    public <K,V> void putKeyValue(K key, V value) throws JsonProcessingException {
        lock.writeLock().lock();
        try{
            String keyJson = objectMapper.writeValueAsString(key);
            String valueJson = objectMapper.writeValueAsString(value);
            keyValueRepository.put(keyJson, valueJson);
        }finally{
            lock.writeLock().unlock();
        }
    }

    public <K,V> V getKeyValue(K typedKey, Class<?> valueClass) throws JsonProcessingException {
        lock.readLock().lock();
        try{
            String keyJson = objectMapper.writeValueAsString(typedKey);
            String valueJson = keyValueRepository.get(keyJson);
            return valueJson.isEmpty() ? null : (V)objectMapper.readValue(valueJson, valueClass);
        }finally{
            lock.readLock().unlock();
        }
    }

    public List<KeyValue> getAllKeyValue() {
        lock.readLock().lock();
        try{
            Map<String, String> keyValueMap = keyValueRepository.getAll();
            return keyValueMap.entrySet().stream().map((e) ->
                    KeyValue.builder().key(e.getKey()).value(e.getValue()).build())
                    .toList();
        }finally{
            lock.readLock().unlock();
        }
    }
}
