package org.assemblits.eru.gui.model;

import java.util.List;

/**
 * Created by marlontrujillo1080 on 10/15/17.
 */
public interface Model<T extends List> {
    T getElements(Class type);
}
