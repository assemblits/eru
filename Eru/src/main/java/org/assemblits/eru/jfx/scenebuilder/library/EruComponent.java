package org.assemblits.eru.jfx.scenebuilder.library;

import lombok.NonNull;
import lombok.Value;

@Value
public class EruComponent {
    @NonNull
    private String name;
    @NonNull
    private Class<?> klass;

    String getName() {
        return name;
    }
}