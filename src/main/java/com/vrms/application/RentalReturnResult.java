package com.vrms.application;

public class RentalReturnResult {

    private final String rentalId;
    private final String vehicleId;
    private final long rentalDays;
    private final long lateDays;
    private final double rentalCost;
    private final double latePenalty;
    private final double totalCost;

    public RentalReturnResult(String rentalId, String vehicleId, long rentalDays, long lateDays, double rentalCost, double latePenalty, double totalCost) {
        this.rentalId = rentalId;
        this.vehicleId = vehicleId;
        this.rentalDays = rentalDays;
        this.lateDays = lateDays;
        this.rentalCost = rentalCost;
        this.latePenalty = latePenalty;
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

    public long getLateDays() {
        return lateDays;
    }

    public double getRentalCost() {
        return rentalCost;
    }

    public double getLatePenalty() {
        return latePenalty;
    }

    public double getTotalCost() {
        return totalCost;
    }
}