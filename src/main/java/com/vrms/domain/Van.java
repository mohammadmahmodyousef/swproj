package com.vrms.domain;

public class Van extends Vehicle {

    public Van(String id, String name, String model, VehicleStatus status) {
        super(id, name, model, status);
    }

    @Override
    public String getType() {
        return "Van";
    }
}