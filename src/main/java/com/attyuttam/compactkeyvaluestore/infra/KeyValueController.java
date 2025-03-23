package com.attyuttam.compactkeyvaluestore.infra;

import com.attyuttam.compactkeyvaluestore.application.KeyValueInteractionService;
import com.attyuttam.compactkeyvaluestore.domain.KeyValue;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/compact-key-value")
public class KeyValueController {
    private final KeyValueInteractionService keyValueInteractionService;
    private final ObjectMapper objectMapper;

    @PostMapping("/put")
    public ResponseEntity<String> putKeyValue(@RequestBody Map<String, Object> request){
        try{
        Object key = request.get("key");
        Object value = request.get("value");

        if (key == null || value == null){
            return ResponseEntity.badRequest().body("Key and Value are required.");
        }
        keyValueInteractionService.putKeyValue(key, value);
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
}
