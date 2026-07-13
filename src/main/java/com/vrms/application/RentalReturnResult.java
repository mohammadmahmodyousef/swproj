package com.vrms.application;

public class RentalReturnResult {

    private final String rentalId;
    private final String vehicleId;
    private final long rentalDays;
    private final double totalCost;

    public RentalReturnResult(String rentalId, String vehicleId, long rentalDays, double totalCost) {
        this.rentalId = rentalId;
        this.vehicleId = vehicleId;
        this.rentalDays = rentalDays;
        this.totalCost = totalCost;
    }

    public String getRentalId() {
        return rentalId;
    }

    public String getVehicleId() {
        return vehicleId;
    }

    public long getRentalDays() {
        return rentalDays;
    }

    public double getTotalCost() {
        return totalCost;
    }
}