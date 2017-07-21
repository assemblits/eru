package com.marlontrujillo.eru.comm.connection;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import javax.persistence.*;

/**
 * Created by mtrujillo on 18/05/17.
 */
@Entity
@Table(name = "connection", schema = "public")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "connection_type", discriminatorType = DiscriminatorType.STRING)
@DiscriminatorValue(value = "CONNECTION")
public abstract class Connection {
    private int id;
    private String name;
    private boolean enabled;
    private int timeout;
    private int samplingTime;
    private BooleanProperty connected;
    private StringProperty  status;

    public Connection() {
        this.connected = new SimpleBooleanProperty(this, "Connected", false);
        this.status    = new SimpleStringProperty(this, "status", "");
    }

    @Transient
    public abstract void connect();

    @Transient
    public abstract void discconnect();

    @Id @GeneratedValue(strategy=GenerationType.AUTO)
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    @Column(name = "name")
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "enabled")
    public boolean isEnabled() {
        return enabled;
    }
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Column(name = "timeout")
    public int getTimeout() {
        return timeout;
    }
    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    @Column(name = "sampling_time")
    public int getSamplingTime() {
        return samplingTime;
    }
    public void setSamplingTime(int samplingTime) {
        this.samplingTime = samplingTime;
    }

    @Column(name = "connected")
    public boolean isConnected() {
        return connected.get();
    }
    public BooleanProperty connectedProperty() {
        return connected;
    }
    public void setConnected(boolean connected) {
        this.connected.set(connected);
    }

    @Column(name = "status")
    public String getStatus() {
        return status.get();
    }
    public StringProperty statusProperty() {
        return status;
    }
    public void setStatus(String status) {
        this.status.set(status);
    }

    @Override
    public String toString() {
        return  name;
    }
}
