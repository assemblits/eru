package com.eru.entity;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * Created by mtrujillo on 6/05/14.
 */
@Entity
@DiscriminatorValue(value = "OPERATOR")
public class Operator extends User {

    private StringProperty departmentName;

    public Operator() {
        this.departmentName = new SimpleStringProperty("");
    }

    @Column(name = "department_name")
    public String getDepartmentName() {
        return departmentName.get();
    }
    public StringProperty departmentNameProperty() {
        return departmentName;
    }
    public void setDepartmentName(String departmentName) {
        this.departmentName.set(departmentName);
    }
}