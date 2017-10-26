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
@Table(name = "user", schema = "public")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "user_type",discriminatorType = DiscriminatorType.STRING)
@DiscriminatorValue(value = "USER")
public class User {

    private IntegerProperty id;
    private StringProperty userName;
    private StringProperty firstName;
    private StringProperty lastName;
    private StringProperty email;
    private StringProperty password;
    private BooleanProperty online;
    private StringProperty groupName;

    public User() {
        this.id         = new SimpleIntegerProperty();
        this.userName   = new SimpleStringProperty("");
        this.firstName  = new SimpleStringProperty("");
        this.lastName   = new SimpleStringProperty("");
        this.email      = new SimpleStringProperty("");
        this.password   = new SimpleStringProperty("");
        this.online     = new SimpleBooleanProperty(false);
        this.groupName  = new SimpleStringProperty("");
    }

    @Id @GeneratedValue
    public Integer getId() {
        return id.get();
    }
    public IntegerProperty idProperty() {
        return id;
    }
    public void setId(Integer id) {
        this.id.set(id);
    }

    public String getUserName() {
        return userName.get();
    }
    public StringProperty userNameProperty() {
        return userName;
    }
    public void setUserName(String userName) {
        this.userName.set(userName);
    }

    public String getFirstName() {
        return firstName.get();
    }
    public StringProperty firstNameProperty() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName.set(firstName);
    }

    public String getLastName() {
        return lastName.get();
    }
    public StringProperty lastNameProperty() {
        return lastName;
    }
    public void setLastName(String lastName) {
        this.lastName.set(lastName);
    }

    public String getEmail() {
        return email.get();
    }
    public StringProperty emailProperty() {
        return email;
    }
    public void setEmail(String email) {
        this.email.set(email);
    }

    public String getPassword() {
        return password.get();
    }
    public StringProperty passwordProperty() {
        return password;
    }
    public void setPassword(String password) {
        this.password.set(password);
    }

    public boolean isOnline() {
        return online.get();
    }
    public BooleanProperty onlineProperty() {
        return online;
    }
    public void setOnline(boolean online) {
        this.online.set(online);
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

    @Override
    public String toString(){
        return getGroupName()+":"+getUserName();
    }

}
