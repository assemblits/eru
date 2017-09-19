package com.eru.preferences;

import javafx.beans.property.SimpleObjectProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Created by mtrujillo on 9/17/17.
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class EruPreference<T> extends SimpleObjectProperty<T> {
    private final T defaultValue;

    public EruPreference(Object bean, String name, T defaultValue) {
        super(bean, name);
        this.defaultValue = defaultValue;
    }
}
