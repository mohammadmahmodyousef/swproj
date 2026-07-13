package com.vrms.application;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import com.vrms.application.strategy.LateReturnPenaltyStrategy;
import com.vrms.application.strategy.RentalPricingStrategy;
import com.vrms.domain.Rental;
import com.vrms.domain.Vehicle;
import com.vrms.domain.VehicleStatus;
import com.vrms.persistence.RentalRepository;
import com.vrms.persistence.VehicleRepository;

public class RentalReturnService {

    private final RentalRepository rentalRepository;
    private final VehicleRepository vehicleRepository;
    private final RentalPricingStrategy pricingStrategy;
    private final LateReturnPenaltyStrategy penaltyStrategy;

    public RentalReturnService(RentalRepository rentalRepository, VehicleRepository vehicleRepository, RentalPricingStrategy pricingStrategy, LateReturnPenaltyStrategy penaltyStrategy) {
        this.rentalRepository = rentalRepository;
        this.vehicleRepository = vehicleRepository;
        this.pricingStrategy = pricingStrategy;
        this.penaltyStrategy = penaltyStrategy;
    }

    public RentalReturnResult returnVehicle(String rentalId, LocalDate returnDate) {
        if (rentalId == null || rentalId.trim().isEmpty()) {
            throw new IllegalArgumentException("Rental ID is required");
        }

        if (returnDate == null) {
            throw new IllegalArgumentException("Return date is required");
        }

        Rental rental = findRentalById(rentalId);

        if (rental == null) {
            throw new IllegalArgumentException("Rental not found");
        }

        if (!rental.isActive()) {
            throw new IllegalStateException("Rental is already closed");
        }

        if (returnDate.isBefore(rental.getStartDate())) {
            throw new IllegalArgumentException("Return date cannot be before rental start date");
        }

        Vehicle vehicle = findVehicleById(rental.getVehicleId());

        if (vehicle == null) {
            throw new IllegalArgumentException("Vehicle not found");
        }

        long rentalDays = ChronoUnit.DAYS.between(rental.getStartDate(), returnDate);

        if (rentalDays == 0) {
            rentalDays = 1;
        }

        long lateDays = 0;

        if (returnDate.isAfter(rental.getEndDate())) {
            lateDays = ChronoUnit.DAYS.between(rental.getEndDate(), returnDate);
        }

        double rentalCost = pricingStrategy.calculateCost(rentalDays);
        double latePenalty = penaltyStrategy.calculatePenalty(lateDays);
        double totalCost = rentalCost + latePenalty;

        rental.setActive(false);
        rentalRepository.save(rental);

        vehicle.setStatus(VehicleStatus.AVAILABLE);
        vehicleRepository.save(vehicle);

        return new RentalReturnResult(rental.getRentalId(), rental.getVehicleId(), rentalDays, lateDays, rentalCost, latePenalty, totalCost);
    }

    private Rental findRentalById(String rentalId) {
        for (Rental rental : rentalRepository.findAll()) {
            if (rental.getRentalId().equals(rentalId)) {
                return rental;
            }
        }

        return null;
    }

    private Vehicle findVehicleById(String vehicleId) {
        for (Vehicle vehicle : vehicleRepository.findAll()) {
            if (vehicle.getId().equals(vehicleId)) {
                return vehicle;
            }
        }

        return null;
    }
}