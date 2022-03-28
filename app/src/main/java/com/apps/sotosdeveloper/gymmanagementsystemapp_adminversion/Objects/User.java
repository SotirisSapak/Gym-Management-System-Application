package com.apps.sotosdeveloper.gymmanagementsystemapp_adminversion.Objects;

import com.apps.sotosdeveloper.gymmanagementsystemapp_adminversion.Constants.DatabaseConstants;

public class User {

    private String ID;
    private String firstName;
    private String lastName;
    private String type;
    private String email;
    private String password;

    public User(String ID, String firstName, String lastName, String email) {
        this.ID = ID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;

        type = DatabaseConstants.FIREBASE_USER_TYPE_1;
        password = "";
    }

    public User(String ID, String firstName, String lastName, String type, String email, String password) {
        this.ID = ID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.type = type;
        this.email = email;
        this.password = password;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
