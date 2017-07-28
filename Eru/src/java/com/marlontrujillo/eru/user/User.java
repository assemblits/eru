package com.marlontrujillo.eru.user;

import javax.persistence.*;

/**
 * Created by mtrujillo on 6/05/14.
 */
@Entity
@Table(name = "user", schema = "public")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "user_type",discriminatorType = DiscriminatorType.STRING)
@DiscriminatorValue(value = "USER")
public class User {

    private int id;
    private String userName;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private boolean online;

    public User() {
    }

    public User(String userName) {
        this.userName = userName;
    }

    @Id @GeneratedValue(strategy=GenerationType.AUTO)
    public int getId() {
        return id;
    }
    private void setId(int id) {
        this.id = id;
    }

    @Column(name = "user_name")
    public String getUserName() {
        return userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Column(name = "first_name")
    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @Column(name = "last_name")
    public String getLastName() {
        return lastName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Column(name = "email")
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    @Column(name = "password")
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    @Column(name = "online")
    public boolean isOnline() {
        return online;
    }
    public void setOnline(boolean online) {
        this.online = online;
    }

    @Override
    public String toString(){
        return userName + " - " + email;
    }

}
