/******************************************************************************
 * Copyright (c) 2017 Assemblits contributors                                 *
 *                                                                            *
 * This file is part of Eru The open JavaFX SCADA by Assemblits Organization. *
 *                                                                            *
 * Eru The open JavaFX SCADA is free software: you can redistribute it        *
 * and/or modify it under the terms of the GNU General Public License         *
 *  as published by the Free Software Foundation, either version 3            *
 *  of the License, or (at your option) any later version.                    *
 *                                                                            *
 * Eru is distributed in the hope that it will be useful,                     *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of             *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the              *
 * GNU General Public License for more details.                               *
 *                                                                            *
 * You should have received a copy of the GNU General Public License          *
 * along with Foobar.  If not, see <http://www.gnu.org/licenses/>.            *
 ******************************************************************************/
package org.assemblits.eru.contract;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;

public class InvalidObservableContract implements Contract {

    private final Observable observable;

    private final InvalidationListener listener;

    private boolean accepted;

    public InvalidObservableContract(Observable observable, InvalidationListener listener) {
        this.observable = observable;
        this.listener = listener;
    }

    @Override
    public void accept() {
        if (accepted) return;
        this.observable.addListener(listener);
        this.accepted = true;
    }

    @Override
    public void revoke() {
        if (!accepted) return;
        this.observable.removeListener(listener);
        this.accepted = false;
    }

    @Override
    public boolean isAccepted() {
        return accepted;
    }
}
