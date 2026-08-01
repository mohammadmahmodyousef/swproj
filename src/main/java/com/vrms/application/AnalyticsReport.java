package com.vrms.application;

import java.util.Collections;
import java.util.Map;

/**
 * Data transfer object representing computed analytics and fleet statistics.
 */
public class AnalyticsReport {

    private final int totalVehicles;
    private final int availableVehicles;
    private final int rentedVehicles;
    private final double utilizationRate;

    private final int totalRentals;
    private final int activeRentals;
    private final int closedRentals;

    private final String mostPopularVehicleType;
    private final Map<String, Integer> rentalsByType;

    public AnalyticsReport(int totalVehicles,
                           int availableVehicles,
                           int rentedVehicles,
                           double utilizationRate,
                           int totalRentals,
                           int activeRentals,
                           int closedRentals,
                           String mostPopularVehicleType,
                           Map<String, Integer> rentalsByType) {
        this.totalVehicles = totalVehicles;
        this.availableVehicles = availableVehicles;
        this.rentedVehicles = rentedVehicles;
        this.utilizationRate = utilizationRate;
        this.totalRentals = totalRentals;
        this.activeRentals = activeRentals;
        this.closedRentals = closedRentals;
        this.mostPopularVehicleType = mostPopularVehicleType;
        this.rentalsByType = rentalsByType != null ? Collections.unmodifiableMap(rentalsByType) : Collections.emptyMap();
    }

    public int getTotalVehicles() {
        return totalVehicles;
    }

    public int getAvailableVehicles() {
        return availableVehicles;
    }

    public int getRentedVehicles() {
        return rentedVehicles;
    }

    public double getUtilizationRate() {
        return utilizationRate;
    }

    public int getTotalRentals() {
        return totalRentals;
    }

    public int getActiveRentals() {
        return activeRentals;
    }

    public int getClosedRentals() {
        return closedRentals;
    }

    public String getMostPopularVehicleType() {
        return mostPopularVehicleType;
    }

    public Map<String, Integer> getRentalsByType() {
        return rentalsByType;
    }
}
