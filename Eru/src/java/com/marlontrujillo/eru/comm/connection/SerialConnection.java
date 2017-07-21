package com.marlontrujillo.eru.comm.connection;

import com.marlontrujillo.eru.logger.LogUtil;
import net.wimpi.modbus.util.SerialParameters;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;

/**
 * Created by mtrujillo on 18/05/17.
 */
@Entity
@DiscriminatorValue(value = "SERIAL")
public class SerialConnection extends Connection {
    private String port;
    private int bitsPerSeconds;
    private int dataBits;
    private String parity;
    private int stopsBits;
    private String frameEncoding;
    private net.wimpi.modbus.net.SerialConnection coreConnection;

    public SerialConnection() {
    }

    @Override
    public void connect() {
        if(!isEnabled() || (coreConnection != null && coreConnection.isOpen())) return;
        try {
            LogUtil.logger.info("Connecting " + getName() + " connection...");
            SerialParameters connectionParameters = new SerialParameters();
            connectionParameters.setPortName(port);
            connectionParameters.setBaudRate(bitsPerSeconds);
            connectionParameters.setEncoding(frameEncoding);
            connectionParameters.setStopbits(stopsBits);
            connectionParameters.setParity(parity);
            connectionParameters.setDatabits(dataBits);
            connectionParameters.setReceiveTimeout(getTimeout());
            coreConnection = new net.wimpi.modbus.net.SerialConnection(connectionParameters);
            coreConnection.open();
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
        if(coreConnection != null && coreConnection.isOpen()){
            LogUtil.logger.info("Disconnecting Serial connection:\t" + getName());
            coreConnection.close();
            setConnected(false);
            setStatus("Disconnected");
            LogUtil.logger.info(getName() + " disconnected.");
        }
    }

    @Column(name = "port")
    public String getPort() {
        return port;
    }
    public void setPort(String port) {
        this.port = port;
    }

    @Column(name = "bits_per_seconds")
    public int getBitsPerSeconds() {
        return bitsPerSeconds;
    }
    public void setBitsPerSeconds(int bitsPerSeconds) {
        this.bitsPerSeconds = bitsPerSeconds;
    }

    @Column(name = "data_bits")
    public int getDataBits() {
        return dataBits;
    }
    public void setDataBits(int dataBits) {
        this.dataBits = dataBits;
    }

    @Column(name = "parity")
    public String getParity() {
        return parity;
    }
    public void setParity(String parity) {
        this.parity = parity;
    }

    @Column(name = "stop_bits")
    public int getStopsBits() {
        return stopsBits;
    }
    public void setStopsBits(int stopsBits) {
        this.stopsBits = stopsBits;
    }

    @Column(name = "frame_encoding")
    public String getFrameEncoding() {
        return frameEncoding;
    }
    public void setFrameEncoding(String frameEncoding) {
        this.frameEncoding = frameEncoding;
    }

    @Transient
    public net.wimpi.modbus.net.SerialConnection getCoreConnection() {
        return coreConnection;
    }
}
