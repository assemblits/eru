package org.assemblits.eru.fieldbus.context;

import lombok.Data;
import org.assemblits.eru.fieldbus.actors.Executor;

/**
 * Created by mtrujillo on 3/9/2016.
 */
@Data
public abstract class Context<T> implements Executor {
    private T target;
    private boolean repeatable;
    private boolean prepared;
}
