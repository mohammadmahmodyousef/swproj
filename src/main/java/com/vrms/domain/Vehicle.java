package com.vrms.domain;

public class Vehicle {

    private final String id;
    private final String name;
    private final String type;
    private VehicleStatus status;

    public Vehicle(String id, String name, String type, VehicleStatus status) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public VehicleStatus getStatus() {
        return status;
    }

    public void setStatus(VehicleStatus status) {
        this.status = status;
    }

    public String toString() {
        return id + " - " + name + " - " + type;
    }
}