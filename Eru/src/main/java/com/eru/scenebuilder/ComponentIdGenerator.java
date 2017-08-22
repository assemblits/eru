package com.eru.scenebuilder;

import java.util.UUID;

public class ComponentIdGenerator {

    public String generateId() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
