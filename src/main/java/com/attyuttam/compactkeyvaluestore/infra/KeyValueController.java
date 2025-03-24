package com.attyuttam.compactkeyvaluestore.infra;

import com.attyuttam.compactkeyvaluestore.application.KeyValueInteractionService;
import com.attyuttam.compactkeyvaluestore.domain.KeyValue;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/compact-key-value")
public class KeyValueController {
    private final KeyValueInteractionService keyValueInteractionService;
    private final KeyValueReplicationService keyValueReplicationService;
    private final ObjectMapper objectMapper;

    @Value("${node.role}")
    private String nodeRole;

    @Value("${replication.leader.url}")
    private String leaderUrl;

    @PostMapping("/put")
    public ResponseEntity<String> putKeyValue(@RequestBody Map<String, Object> request){
        if ("follower".equals(nodeRole)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Followers cannot accept writes directly.");
        }
        try{
            Object key = request.get("key");
            Object value = request.get("value");

            if (key == null || value == null){
                return ResponseEntity.badRequest().body("Key and Value are required.");
            }
            keyValueInteractionService.putKeyValue(key, value);
            keyValueReplicationService.replicateToFollowers(key, value);
            return ResponseEntity.ok("Stored successfully!");
        }catch(Exception ex){
            return ResponseEntity.internalServerError().body("Error storing key-value: " + ex.getMessage());
        }
    }

    @GetMapping("/get")
    public ResponseEntity<?> getKeyValue(@RequestBody String key, @RequestParam String keyType, @RequestParam String valueType){
        try{
            Class<?> keyClass = Class.forName(keyType);
            Class<?> valueClass = Class.forName(valueType);

            Object typedKey = objectMapper.readValue(key, keyClass);
            Object value = keyValueInteractionService.getKeyValue(typedKey, valueClass);
            return value != null ? ResponseEntity.ok(value) : ResponseEntity.notFound().build();
        }catch(Exception ex){
            return ResponseEntity.internalServerError().body("Error fetching value for key: " + ex.getMessage());
        }
    }

    @GetMapping("/get-all")
    public ResponseEntity<?> getAllKeyValue(){
        try{
            List<KeyValue> keyValueList = keyValueInteractionService.getAllKeyValue();
            return ! CollectionUtils.isEmpty(keyValueList) ? ResponseEntity.ok(keyValueList) : ResponseEntity.notFound().build();
        }catch (Exception ex){
            return ResponseEntity.internalServerError().body("Error fetching values: " + ex.getMessage());
        }
    }


    @PostMapping("/replicate")
    public ResponseEntity<String> replicate(@RequestBody Map<String, String> replicationRequest) {
        if ("leader".equals(nodeRole)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Leader does not accept replication requests.");
        }
        try{
            log.info("FOLLOWER REPLICATION STARTED");
            Object key = replicationRequest.get("key");
            Object value = replicationRequest.get("value");

            if (key == null || value == null){
                return ResponseEntity.badRequest().body("Key and Value are required.");
            }
            keyValueInteractionService.putKeyValue(key, value);
            log.info("FOLLOWER REPLICATION COMPLETED");
            return ResponseEntity.ok("Replicated successfully!");
        }catch(Exception ex){
            return ResponseEntity.internalServerError().body("Error storing key-value: " + ex.getMessage());
        }
    }
}
