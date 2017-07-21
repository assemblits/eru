package com.marlontrujillo.eru.user;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * Created by mtrujillo on 6/05/14.
 */
@Entity
@DiscriminatorValue(value = "ENGINEER")
public class Engineer extends User {
    @Column(name = "civ_number")
    private String civNumber;

    public Engineer() {
    }

    public Engineer(String userName) {
        super(userName);
    }

    public String getCivNumber() {
        return civNumber;
    }

    public void setCivNumber(String civNumber) {
        this.civNumber = civNumber;
    }
}
