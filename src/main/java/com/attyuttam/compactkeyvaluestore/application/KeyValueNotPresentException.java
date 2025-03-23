package com.attyuttam.compactkeyvaluestore.application;

public class KeyValueNotPresentException extends RuntimeException {
    KeyValueNotPresentException(){
        super("Key Value not present");
    }
}
