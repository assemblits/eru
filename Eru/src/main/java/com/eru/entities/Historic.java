package com.eru.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;

/**
 * Created by mtrujillo on 14/02/14.
 *
 * This class is created to create the table in DataBase using the <property name="hibernate.hbm2ddl.auto" value="updateValueAndTimestamp"/>
 *
 */
@Entity
@Table(name = "historic", schema = "public")
public class Historic {
    private Timestamp timeStamp;

    public Historic() {
        this.timeStamp = new Timestamp(System.currentTimeMillis());
    }

    @Id
    @Column(name = "time_stamp")
    public Timestamp getTimeStamp() {
        return timeStamp;
    }
    public void setTimeStamp(Timestamp timeStamp) {
        this.timeStamp = timeStamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Historic historic = (Historic) o;

        return timeStamp.equals(historic.timeStamp);
    }

    @Override
    public int hashCode() {
        return timeStamp.hashCode();
    }
}
