package com.vrms.domain;


public class Manager {

    private final String username;
    private final String password;

    public Manager(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public boolean hasPassword(String enteredPassword) {
        return password != null && password.equals(enteredPassword);
    }
}