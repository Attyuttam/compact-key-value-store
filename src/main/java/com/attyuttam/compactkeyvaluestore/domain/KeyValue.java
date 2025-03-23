package com.attyuttam.compactkeyvaluestore.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class KeyValue {
    private String key;
    private String value;
}
