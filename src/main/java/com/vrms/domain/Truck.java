package com.vrms.domain;

public class Truck extends Vehicle {

    public Truck(String id, String name, String model, VehicleStatus status) {
        super(id, name, model, status);
    }

    @Override
    public String getType() {
        return "Truck";
    }

    @Override
    public void validateRental(int customerAge, boolean hasSpecialLicense) {
        if (!hasSpecialLicense) {
            throw new IllegalArgumentException("Special truck license is required");
        }
    }
}