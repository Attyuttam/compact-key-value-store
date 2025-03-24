package com.attyuttam.compactkeyvaluestore.infra;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class KeyValueReplicationService {
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("#{'${replication.followerNodes}'.split(',')}")
    List<String> followerNodes;

    public <K,V> void replicateToFollowers(K key, V value) {
        log.info("REPLICATION INITIATED");
        for (String follower : followerNodes) {
            try {
                restTemplate.postForObject(follower + "/compact-key-value/replicate", Map.of("key", key, "value", value), String.class);
            } catch (Exception e) {
                System.err.println("Failed to replicate to: " + follower);
            }
        }
        log.info("REPLICATION COMPLETED");
    }
}
