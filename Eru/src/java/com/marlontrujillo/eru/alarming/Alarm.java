package com.marlontrujillo.eru.alarming;

import javafx.beans.property.*;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * Created by mtrujillo on 8/05/14.
 */
@Entity
@Table(name = "alarm", schema = "public")
public class Alarm implements Comparable<Alarm>{
    public enum Priority{HIHI, HI, NOMINAL, LO, LOLO}

    private final LongProperty                id;
    private final ObjectProperty<Timestamp>   timeStamp;
    private final StringProperty              description;
    private final StringProperty              userInCharge;
    private final StringProperty              groupName;
    private final ObjectProperty<Priority>    priority;
    private final BooleanProperty             acknowledged;

    /* ********** Constructor ********** */
    public Alarm(Timestamp timeStamp, String description, String userInCharge, String groupName, Priority priority) {
        this();
        setTimeStamp(timeStamp);
        setDescription(description);
        setUserInCharge(userInCharge);
        setGroupName(groupName);
        setPriority(priority);
    }

    public Alarm() {
        id              = new SimpleLongProperty(Alarm.this, "id", 0);
        timeStamp       = new SimpleObjectProperty<>(Alarm.this, "alarm", new Timestamp(System.currentTimeMillis()));
        description     = new SimpleStringProperty();
        userInCharge    = new SimpleStringProperty();
        groupName       = new SimpleStringProperty();
        priority        = new SimpleObjectProperty<>(Alarm.this, "priority", Priority.NOMINAL);
        acknowledged    = new SimpleBooleanProperty();
    }

    /* ********** Properties ********** */
    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE)
    public long getId() {
        return id.get();
    }
    public LongProperty idProperty() {
        return id;
    }
    public void setId(long id) {
        this.id.set(id);
    }

    @Column(name = "time_stamp")
    public Timestamp getTimeStamp() {
        return timeStamp.get();
    }
    public ObjectProperty<Timestamp> timeStampProperty() {
        return timeStamp;
    }
    public void setTimeStamp(Timestamp timeStamp) {
        this.timeStamp.set(timeStamp);
    }

    @Column(name = "description")
    public String getDescription() {
        return description.get();
    }
    public StringProperty descriptionProperty() {
        return description;
    }
    public void setDescription(String description) {
        this.description.set(description);
    }

    @Column(name = "user_in_charge")
    public String getUserInCharge() {
        return userInCharge.get();
    }
    public StringProperty userInChargeProperty() {
        return userInCharge;
    }
    public void setUserInCharge(String userInCharge) {
        this.userInCharge.set(userInCharge);
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
    public Priority getPriority() {
        return priority.get();
    }
    public ObjectProperty<Priority> priorityProperty() {
        return priority;
    }
    public void setPriority(Priority priority) {
        this.priority.set(priority);
    }
    @Column(name = "priority")
    public String getPriorityName() {
        return getPriority() == null ? "" : getPriority().name();
    }
    public void setPriorityName(String priorityName) {
        this.priority.setValue(priorityName == null || priorityName.isEmpty() ? Priority.NOMINAL : Priority.valueOf(priorityName));
    }

    @Column(name = "acknowledged")
    public boolean getAcknowledged() {
        return acknowledged.get();
    }
    public BooleanProperty acknowledgedProperty() {
        return acknowledged;
    }
    public void setAcknowledged(boolean acknowledged) {
        this.acknowledged.set(acknowledged);
    }

    @Override
    public int compareTo(Alarm t) {
        long newTime = t.getTimeStamp().getTime();
        return (int) (newTime - getTimeStamp().getTime());
    }

    @Override
    public String toString() {
        return "[" + getId() + "]";
    }

}
