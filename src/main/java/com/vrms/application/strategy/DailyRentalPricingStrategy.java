package com.vrms.application.strategy;

public class DailyRentalPricingStrategy implements RentalPricingStrategy {

    private final double dailyRate;

    public DailyRentalPricingStrategy(double dailyRate) {
        if (dailyRate <= 0) {
            throw new IllegalArgumentException("Daily rate must be greater than zero");
        }

        this.dailyRate = dailyRate;
    }

    @Override
    public double calculateCost(long rentalDays) {
        if (rentalDays <= 0) {
            throw new IllegalArgumentException("Rental days must be greater than zero");
        }

        return rentalDays * dailyRate;
    }
}