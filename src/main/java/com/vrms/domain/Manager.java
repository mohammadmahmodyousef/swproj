package com.vrms.domain;

public class Manager {

    private String username;
    private String password;

    public Manager(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public boolean hasPassword(String password) {
        return this.password.equals(password);
    }

    public String toFileLine() {
        return username + "|" + password;
    }

    public static Manager fromFileLine(String line) {
        String[] data = line.split("\\|");

        if (data.length != 2) {
            throw new IllegalArgumentException("Invalid manager data");
        }

        return new Manager(data[0], data[1]);
    }
}