package com.vrms.domain;

public class ElectricVehicle extends Vehicle {

    private static final int MINIMUM_BATTERY_LEVEL = 20;

    private int batteryLevel;

    public ElectricVehicle(String id, String name, String model, VehicleStatus status, int batteryLevel) {
        super(id, name, model, status);

        if (batteryLevel < 0 || batteryLevel > 100) {
            throw new IllegalArgumentException("Battery level must be between 0 and 100");
        }

        this.batteryLevel = batteryLevel;
    }

    public int getBatteryLevel() {
        return batteryLevel;
    }

    public void setBatteryLevel(int batteryLevel) {
        if (batteryLevel < 0 || batteryLevel > 100) {
            throw new IllegalArgumentException("Battery level must be between 0 and 100");
        }

        this.batteryLevel = batteryLevel;
    }

    @Override
    public String getType() {
        return "Electric Vehicle";
    }

    @Override
    public void validateRental(int customerAge, boolean hasSpecialLicense) {
        if (batteryLevel < MINIMUM_BATTERY_LEVEL) {
            throw new IllegalStateException("Electric vehicle battery level must be at least 20%");
        }
    }

    @Override
    protected String getExtraData() {
        return String.valueOf(batteryLevel);
    }

    @Override
    public String toString() {
        return super.toString() + " - Battery: " + batteryLevel + "%";
    }
}