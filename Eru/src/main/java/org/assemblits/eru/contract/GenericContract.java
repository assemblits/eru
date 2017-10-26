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

import java.util.function.BiConsumer;

public class GenericContract<O,L> implements Contract {

    private final O deviceToLink;

    private final L linker;

    private final BiConsumer<? super O, ? super L> link;

    private final BiConsumer<? super O, ? super L> unlink;

    private boolean accepted;

    public GenericContract(O deviceToLink, L linker, BiConsumer<? super O, ? super L> link, BiConsumer<? super O, ? super L> unlink) {
        this.deviceToLink = deviceToLink;
        this.linker = linker;
        this.link = link;
        this.unlink = unlink;
    }

    @Override
    public void accept() {
        if (accepted) return;
        this.link.accept(deviceToLink, linker);
        this.accepted = true;
    }

    @Override
    public void revoke() {
        if (!accepted) return;
        this.unlink.accept(deviceToLink, linker);
        this.accepted = false;
    }

    @Override
    public boolean isAccepted() {
        return accepted;
    }
}
