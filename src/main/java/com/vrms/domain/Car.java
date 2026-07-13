package com.vrms.domain;

public class Car extends Vehicle {

    public Car(String id, String name, String model, VehicleStatus status) {
        super(id, name, model, status);
    }

    @Override
    public String getType() {
        return "Car";
    }
}