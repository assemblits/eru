package org.assemblits.eru.contract;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;

/**
 * Created by marlontrujillo1080 on 10/13/17.
 */
public class InvalidObservableContract implements Contract {

    private final Observable observable;

    private final InvalidationListener listener;

    private boolean linked;

    public InvalidObservableContract(Observable observable, InvalidationListener listener) {
        this.observable = observable;
        this.listener = listener;
    }

    @Override
    public void accept() {
        if (linked) return;
        this.observable.addListener(listener);
        this.linked = true;
    }

    @Override
    public void revoke() {
        if (!linked) return;
        this.observable.removeListener(listener);
        this.linked = false;
    }
}
