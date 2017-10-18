package org.assemblits.eru.bus.context;

import lombok.Data;
import org.assemblits.eru.bus.actors.BusExecutor;

/**
 * Created by mtrujillo on 3/9/2016.
 */
@Data
public abstract class Context<T> implements BusExecutor {
    private T target;
    private boolean repeatable;
    private boolean prepared;
}
