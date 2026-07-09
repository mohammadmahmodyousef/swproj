package com.vrms.domain;

public class Vehicle {

    private String id;
    private String name;
    private String model;
    private VehicleStatus status;

    public Vehicle(String id, String name, String model, VehicleStatus status) {
        this.id = id;
        this.name = name;
        this.model = model;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getModel() {
        return model;
    }

    public VehicleStatus getStatus() {
        return status;
    }

    public void setStatus(VehicleStatus status) {
        this.status = status;
    }

    public boolean isAvailable() {
        return status == VehicleStatus.AVAILABLE;
    }

    public String toFileLine() {
        return id + "|" + name + "|" + model + "|" + status;
    }

    public static Vehicle fromFileLine(String line) {
        String[] data = line.split("\\|");

        if (data.length != 4) {
            throw new IllegalArgumentException("Invalid vehicle data");
        }

        return new Vehicle(data[0], data[1], data[2], VehicleStatus.valueOf(data[3]));
    }

    @Override
    public String toString() {
        return id + " - " + name + " - " + model + " - " + status;
    }
}