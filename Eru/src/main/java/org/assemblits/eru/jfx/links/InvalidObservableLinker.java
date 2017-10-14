package org.assemblits.eru.jfx.links;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;

/**
 * Created by marlontrujillo1080 on 10/13/17.
 */
public class InvalidObservableLinker implements Linker {

    private final Observable observable;

    private final InvalidationListener listener;

    private boolean linked;

    public InvalidObservableLinker(Observable observable, InvalidationListener listener) {
        this.observable = observable;
        this.listener = listener;
    }

    @Override
    public void link() {
        if (linked) return;
        this.observable.addListener(listener);
        this.linked = true;
    }

    @Override
    public void unlink() {
        if (!linked) return;
        this.observable.removeListener(listener);
        this.linked = false;
    }
}
