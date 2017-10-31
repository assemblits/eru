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
package org.assemblits.eru.entities;

import com.ghgande.j2mod.modbus.net.TCPMasterConnection;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.assemblits.eru.exception.ConnectException;
import org.assemblits.eru.fieldbus.protocols.modbus.Modbus;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;
import java.net.InetAddress;

@Entity
@DiscriminatorValue(value = "TCPIP")
public class TcpConnection extends Connection implements Modbus{
    private StringProperty hostname;
    private IntegerProperty port;
    private TCPMasterConnection coreConnection;

    public TcpConnection() {
        this.hostname = new SimpleStringProperty("localhost");
        this.port = new SimpleIntegerProperty(502);
    }

    @Override
    public void connect() throws ConnectException{
        if (!isEnabled()) return;
        try {
            coreConnection = new TCPMasterConnection(InetAddress.getByName(hostname.get()));
            coreConnection.setPort(port.get());
            coreConnection.setTimeout(getTimeout());
            coreConnection.connect();
            setConnected(true);
            setStatus("Connected");
        } catch (Exception e) {
            setConnected(false);
            setStatus(e.getLocalizedMessage());
            throw new ConnectException(e.getLocalizedMessage());
        }
    }

    @Override
    public void disconnect() {
        if (coreConnection != null && coreConnection.isConnected()) {
            coreConnection.close();
            setConnected(false);
            setStatus("Disconnected");
        }
    }

    public String getHostname() {
        return hostname.get();
    }

    public void setHostname(String hostname) {
        this.hostname.set(hostname);
    }

    public StringProperty hostnameProperty() {
        return hostname;
    }

    public int getPort() {
        return port.get();
    }

    public void setPort(int port) {
        this.port.set(port);
    }

    public IntegerProperty portProperty() {
        return port;
    }

    @Transient
    public TCPMasterConnection getCoreConnection() {
        return coreConnection;
    }
}
