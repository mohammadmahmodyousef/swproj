package com.vrms.domain;

public class Motorcycle extends Vehicle {

    private static final int MINIMUM_AGE = 18;

    public Motorcycle(String id, String name, String model, VehicleStatus status) {
        super(id, name, model, status);
    }

    @Override
    public String getType() {
        return "Motorcycle";
    }

    @Override
    public void validateRental(int customerAge, boolean hasSpecialLicense) {
        if (customerAge < MINIMUM_AGE) {
            throw new IllegalArgumentException("Customer must be at least 18 years old to rent a motorcycle");
        }
    }
}