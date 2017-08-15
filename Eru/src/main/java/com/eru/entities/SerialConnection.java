package com.eru.entities;

import com.eru.logger.LogUtil;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
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

    private StringProperty  port;
    private IntegerProperty bitsPerSeconds;
    private IntegerProperty dataBits;
    private StringProperty  parity;
    private IntegerProperty stopsBits;
    private StringProperty  frameEncoding;
    private net.wimpi.modbus.net.SerialConnection coreConnection;

    public SerialConnection() {
        this.port           = new SimpleStringProperty("COM1");
        this.bitsPerSeconds = new SimpleIntegerProperty(19200);
        this.dataBits       = new SimpleIntegerProperty(8);
        this.parity         = new SimpleStringProperty("NONE");
        this.stopsBits      = new SimpleIntegerProperty(1);
        this.frameEncoding  = new SimpleStringProperty("RTU");
    }

    @Override
    public void connect() {
        if(!isEnabled() || (coreConnection != null && coreConnection.isOpen())) return;
        try {
            LogUtil.logger.info("Connecting " + getName() + " connection...");
            SerialParameters connectionParameters = new SerialParameters();
            connectionParameters.setPortName(port.get());
            connectionParameters.setBaudRate(bitsPerSeconds.get());
            connectionParameters.setEncoding(frameEncoding.get());
            connectionParameters.setStopbits(stopsBits.get());
            connectionParameters.setParity(parity.get());
            connectionParameters.setDatabits(dataBits.get());
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
        return port.get();
    }
    public StringProperty portProperty() {
        return port;
    }
    public void setPort(String port) {
        this.port.set(port);
    }

    @Column(name = "bits_per_seconds")
    public int getBitsPerSeconds() {
        return bitsPerSeconds.get();
    }
    public IntegerProperty bitsPerSecondsProperty() {
        return bitsPerSeconds;
    }
    public void setBitsPerSeconds(int bitsPerSeconds) {
        this.bitsPerSeconds.set(bitsPerSeconds);
    }

    @Column(name = "data_bits")
    public int getDataBits() {
        return dataBits.get();
    }
    public IntegerProperty dataBitsProperty() {
        return dataBits;
    }
    public void setDataBits(int dataBits) {
        this.dataBits.set(dataBits);
    }

    @Column(name = "parity")
    public String getParity() {
        return parity.get();
    }
    public StringProperty parityProperty() {
        return parity;
    }
    public void setParity(String parity) {
        this.parity.set(parity);
    }

    @Column(name = "stop_bits")
    public int getStopsBits() {
        return stopsBits.get();
    }
    public IntegerProperty stopsBitsProperty() {
        return stopsBits;
    }
    public void setStopsBits(int stopsBits) {
        this.stopsBits.set(stopsBits);
    }

    @Column(name = "frame_encoding")
    public String getFrameEncoding() {
        return frameEncoding.get();
    }
    public StringProperty frameEncodingProperty() {
        return frameEncoding;
    }
    public void setFrameEncoding(String frameEncoding) {
        this.frameEncoding.set(frameEncoding);
    }

    @Transient
    public net.wimpi.modbus.net.SerialConnection getCoreConnection() {
        return coreConnection;
    }
}
