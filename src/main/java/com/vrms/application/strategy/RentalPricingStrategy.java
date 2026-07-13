package com.vrms.application.strategy;

public interface RentalPricingStrategy {

    double calculateCost(long rentalDays);
}