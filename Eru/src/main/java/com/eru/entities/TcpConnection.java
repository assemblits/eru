package com.eru.entities;

import com.eru.logger.LogUtil;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import net.wimpi.modbus.net.TCPMasterConnection;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;
import java.net.InetAddress;

/**
 * Created by mtrujillo on 18/05/17.
 */
@Entity
@DiscriminatorValue(value = "TCPIP")
public class TcpConnection extends Connection{
    private StringProperty hostname;
    private IntegerProperty port;
    private TCPMasterConnection coreConnection;

    public TcpConnection() {
        this.hostname   = new SimpleStringProperty("localhost");
        this.port       = new SimpleIntegerProperty(502);
    }

    @Override
    public void connect() {
        if(!isEnabled() || (coreConnection != null && coreConnection.isConnected())) return;
        try {
            LogUtil.logger.info("Connecting " + getName() + " connection...");
            coreConnection = new TCPMasterConnection(InetAddress.getByName(hostname.get()));
            coreConnection.setPort(port.get());
            coreConnection.setTimeout(getTimeout());
            coreConnection.connect();
            setConnected(true);
            setStatus("Connected");
            LogUtil.logger.info(getName() + " connected.");
        } catch (Exception e) {
            e.printStackTrace();
            setStatus(e.getLocalizedMessage());
            setConnected(false);
            LogUtil.logger.error(getName() + " connection failure.", e);
        }
    }

    @Override
    public void discconnect() {
        if(coreConnection != null && coreConnection.isConnected()){
            LogUtil.logger.info("Disconnecting TCP connection:\t" + getName());
            coreConnection.close();
            setConnected(false);
            setStatus("Disconnected");
            LogUtil.logger.info(getName() + " disconnected.");
        }
    }

    public String getHostname() {
        return hostname.get();
    }
    public StringProperty hostnameProperty() {
        return hostname;
    }
    public void setHostname(String hostname) {
        this.hostname.set(hostname);
    }

    public int getPort() {
        return port.get();
    }
    public IntegerProperty portProperty() {
        return port;
    }
    public void setPort(int port) {
        this.port.set(port);
    }

    @Transient
    public TCPMasterConnection getCoreConnection() {
        return coreConnection;
    }
}
