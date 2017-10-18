package org.assemblits.eru.entities;

import com.ghgande.j2mod.modbus.net.TCPMasterConnection;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import lombok.extern.slf4j.Slf4j;
import org.assemblits.eru.bus.protocols.modbus.Modbus;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;
import java.net.InetAddress;

/**
 * Created by mtrujillo on 18/05/17.
 */
@Entity
@DiscriminatorValue(value = "TCPIP")
@Slf4j
public class TcpConnection extends Connection implements Modbus{
    private StringProperty hostname;
    private IntegerProperty port;
    private TCPMasterConnection coreConnection;

    public TcpConnection() {
        this.hostname = new SimpleStringProperty("localhost");
        this.port = new SimpleIntegerProperty(502);
    }

    @Override
    public void connect() {
        if (!isEnabled()) return;
        try {
            log.info("Starting connection <{}>", getName());
            coreConnection = new TCPMasterConnection(InetAddress.getByName(hostname.get()));
            coreConnection.setPort(port.get());
            coreConnection.setTimeout(getTimeout());
            coreConnection.connect();
            setConnected(true);
            setStatus("Connected");
            log.info("<{}> connected.", getName());
        } catch (Exception e) {
            setStatus(e.getLocalizedMessage());
            setConnected(false);
            log.error("<{}> connection failure.", getName());
        }
    }

    @Override
    public void disconnect() {
        if (coreConnection != null && coreConnection.isConnected()) {
            coreConnection.close();
            setConnected(false);
            setStatus("Disconnected");
            log.info("<{}> disconnected.", getName());
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
