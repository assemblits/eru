package com.marlontrujillo.eru.comm.connection;

import com.marlontrujillo.eru.logger.LogUtil;
import net.wimpi.modbus.net.TCPMasterConnection;

import javax.persistence.Column;
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
    private String hostname;
    private int port;
    private TCPMasterConnection coreConnection;

    public TcpConnection() {
    }

    @Override
    public void connect() {
        if(!isEnabled() || (coreConnection != null && coreConnection.isConnected())) return;
        try {
            LogUtil.logger.info("Connecting " + getName() + " connection...");
            coreConnection = new TCPMasterConnection(InetAddress.getByName(hostname));
            coreConnection.setPort(port);
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

    @Column(name = "hostname")
    public String getHostname() {
        return hostname;
    }
    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    @Column(name = "port")
    public int getPort() {
        return port;
    }
    public void setPort(int port) {
        this.port = port;
    }

    @Transient
    public TCPMasterConnection getCoreConnection() {
        return coreConnection;
    }
}
