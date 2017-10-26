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
package org.assemblits.eru.fieldbus.actors;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.LinkedBlockingQueue;

@Slf4j
@Data
@EqualsAndHashCode(callSuper = true)
@Component
public class Director extends Thread {
    private final LinkedBlockingQueue<Executor> executors = new LinkedBlockingQueue<>();

    @Override
    public void run() {
        log.info("Starting executors updating...");
        while (!Thread.currentThread().isInterrupted()) {
            try {
                Executor headParticipant = executors.take();
                if (!headParticipant.isPrepared()) headParticipant.prepare();
                headParticipant.execute();
                if (headParticipant.isRepeatable()) executors.put(headParticipant);
            } catch (InterruptedException e) {
                 log.info("Director stopped.");
            } catch (Exception e) {
                log.error("Director halt", e);
                throw new RuntimeException("Director halt: " + e.getLocalizedMessage());
            }
        }
    }

}
