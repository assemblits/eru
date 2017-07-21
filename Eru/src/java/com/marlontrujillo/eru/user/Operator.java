package com.marlontrujillo.eru.user;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * Created by mtrujillo on 6/05/14.
 */
@Entity
@DiscriminatorValue(value = "OPERATOR")
public class Operator extends User {
    @Column(name = "department_name")
    private String departmentName;

    public Operator() {
    }

    public Operator(String userName) {
        super(userName);
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }
}