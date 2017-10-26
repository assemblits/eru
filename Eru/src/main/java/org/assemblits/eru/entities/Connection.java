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

import javafx.beans.property.*;

import javax.persistence.*;

@Entity
@Table(name = "connection", schema = "public")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "connection_type", discriminatorType = DiscriminatorType.STRING)
@DiscriminatorValue(value = "CONNECTION")
public abstract class Connection {
    private IntegerProperty id;
    private StringProperty  name;
    private BooleanProperty enabled;
    private IntegerProperty timeout;
    private IntegerProperty samplingTime;
    private BooleanProperty connected;
    private StringProperty  status;
    private StringProperty  groupName;

    public Connection() {
        this.id             = new SimpleIntegerProperty();
        this.name           = new SimpleStringProperty("");
        this.enabled        = new SimpleBooleanProperty(false);
        this.timeout        = new SimpleIntegerProperty(3000);
        this.samplingTime   = new SimpleIntegerProperty(500);
        this.connected      = new SimpleBooleanProperty(false);
        this.status         = new SimpleStringProperty("");
        this.groupName      = new SimpleStringProperty("");
    }

    @Transient
    public abstract void connect();

    @Transient
    public abstract void disconnect();

    @Id @GeneratedValue(strategy=GenerationType.AUTO)
    public Integer getId() {
        return id.get();
    }
    public IntegerProperty idProperty() {
        return id;
    }
    public void setId(Integer id) {
        this.id.set(id);
    }

    @Column(name = "name")
    public String getName() {
        return name.get();
    }
    public StringProperty nameProperty() {
        return name;
    }
    public void setName(String name) {
        this.name.set(name);
    }

    @Column(name = "enabled")
    public boolean isEnabled() {
        return enabled.get();
    }
    public BooleanProperty enabledProperty() {
        return enabled;
    }
    public void setEnabled(boolean enabled) {
        this.enabled.set(enabled);
    }

    @Column(name = "timeout")
    public int getTimeout() {
        return timeout.get();
    }
    public IntegerProperty timeoutProperty() {
        return timeout;
    }
    public void setTimeout(int timeout) {
        this.timeout.set(timeout);
    }

    @Column(name = "sampling_time")
    public int getSamplingTime() {
        return samplingTime.get();
    }
    public IntegerProperty samplingTimeProperty() {
        return samplingTime;
    }
    public void setSamplingTime(int samplingTime) {
        this.samplingTime.set(samplingTime);
    }

    @Column(name = "group_name")
    public String getGroupName() {
        return groupName.get();
    }
    public StringProperty groupNameProperty() {
        return groupName;
    }
    public void setGroupName(String groupName) {
        this.groupName.set(groupName);
    }

    @Transient
    public boolean isConnected() {
        return connected.get();
    }
    public BooleanProperty connectedProperty() {
        return connected;
    }
    public void setConnected(boolean connected) {
        this.connected.set(connected);
    }

    @Transient
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
        return  getGroupName()+":"+getName();
    }
}
