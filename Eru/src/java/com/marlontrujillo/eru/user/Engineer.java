package com.marlontrujillo.eru.user;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * Created by mtrujillo on 6/05/14.
 */
@Entity
@DiscriminatorValue(value = "ENGINEER")
public class Engineer extends User {

    private StringProperty licence;

    public Engineer() {
        this.licence = new SimpleStringProperty("");
    }

    @Column(name = "licence")
    public String getLicence() {
        return licence.get();
    }
    public StringProperty licenceProperty() {
        return licence;
    }
    public void setLicence(String licence) {
        this.licence.set(licence);
    }
}
