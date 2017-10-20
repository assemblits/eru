package org.assemblits.eru.entities;

import com.ghgande.j2mod.modbus.util.SerialParameters;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import lombok.extern.slf4j.Slf4j;
import org.assemblits.eru.fieldbus.protocols.modbus.Modbus;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;

/**
 * Created by mtrujillo on 18/05/17.
 */
@Entity
@DiscriminatorValue(value = "SERIAL")
@Slf4j
public class SerialConnection extends Connection implements Modbus{

    private StringProperty port;
    private IntegerProperty bitsPerSeconds;
    private IntegerProperty dataBits;
    private StringProperty parity;
    private IntegerProperty stopsBits;
    private StringProperty frameEncoding;
    private com.ghgande.j2mod.modbus.net.SerialConnection coreConnection;

    public SerialConnection() {
        this.port = new SimpleStringProperty("COM1");
        this.bitsPerSeconds = new SimpleIntegerProperty(19200);
        this.dataBits = new SimpleIntegerProperty(8);
        this.parity = new SimpleStringProperty("NONE");
        this.stopsBits = new SimpleIntegerProperty(1);
        this.frameEncoding = new SimpleStringProperty("RTU");
    }

    @Override
    public void connect() {
        if (!isEnabled()) return;
        try {
            log.info("Starting connection <{}>", getName());
            SerialParameters connectionParameters = new SerialParameters();
            connectionParameters.setPortName(port.get());
            connectionParameters.setBaudRate(bitsPerSeconds.get());
            connectionParameters.setEncoding(frameEncoding.get());
            connectionParameters.setStopbits(stopsBits.get());
            connectionParameters.setParity(parity.get());
            connectionParameters.setDatabits(dataBits.get());
//            connectionParameters.setReceiveTimeout(getTimeout());
            coreConnection = new com.ghgande.j2mod.modbus.net.SerialConnection(connectionParameters);
            coreConnection.open();
            setConnected(true);
            setStatus("Connected");
            log.info("<{}> connected", getName());
        } catch (Exception e) {
            setStatus(e.getLocalizedMessage());
            setConnected(false);
            log.error("{} connection failure.", getName());
        }
    }

    @Override
    public void disconnect() {
        if (coreConnection != null && coreConnection.isOpen()) {
            log.info("Disconnecting Serial connection:\t{}", getName());
            coreConnection.close();
            setConnected(false);
            setStatus("Disconnected");
            log.info("{} disconnected.", getName());
        }
    }

    @Column(name = "port")
    public String getPort() {
        return port.get();
    }

    public void setPort(String port) {
        this.port.set(port);
    }

    public StringProperty portProperty() {
        return port;
    }

    @Column(name = "bits_per_seconds")
    public int getBitsPerSeconds() {
        return bitsPerSeconds.get();
    }

    public void setBitsPerSeconds(int bitsPerSeconds) {
        this.bitsPerSeconds.set(bitsPerSeconds);
    }

    public IntegerProperty bitsPerSecondsProperty() {
        return bitsPerSeconds;
    }

    @Column(name = "data_bits")
    public int getDataBits() {
        return dataBits.get();
    }

    public void setDataBits(int dataBits) {
        this.dataBits.set(dataBits);
    }

    public IntegerProperty dataBitsProperty() {
        return dataBits;
    }

    @Column(name = "parity")
    public String getParity() {
        return parity.get();
    }

    public void setParity(String parity) {
        this.parity.set(parity);
    }

    public StringProperty parityProperty() {
        return parity;
    }

    @Column(name = "stop_bits")
    public int getStopsBits() {
        return stopsBits.get();
    }

    public void setStopsBits(int stopsBits) {
        this.stopsBits.set(stopsBits);
    }

    public IntegerProperty stopsBitsProperty() {
        return stopsBits;
    }

    @Column(name = "frame_encoding")
    public String getFrameEncoding() {
        return frameEncoding.get();
    }

    public void setFrameEncoding(String frameEncoding) {
        this.frameEncoding.set(frameEncoding);
    }

    public StringProperty frameEncodingProperty() {
        return frameEncoding;
    }

    @Transient
    public com.ghgande.j2mod.modbus.net.SerialConnection getCoreConnection() {
        return coreConnection;
    }
}
