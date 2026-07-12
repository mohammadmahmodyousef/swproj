package com.vrms.application;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import com.vrms.domain.Rental;
import com.vrms.domain.Vehicle;
import com.vrms.domain.VehicleStatus;
import com.vrms.persistence.RentalRepository;
import com.vrms.persistence.VehicleRepository;

public class RentalService {

    private static final int MAX_RENTAL_DAYS = 30;

    private final RentalRepository rentalRepository;
    private final VehicleRepository vehicleRepository;

    public RentalService(RentalRepository rentalRepository, VehicleRepository vehicleRepository) {
        this.rentalRepository = rentalRepository;
        this.vehicleRepository = vehicleRepository;
    }

    public Rental rentVehicle(String rentalId, String vehicleId, String customerName, LocalDate startDate, LocalDate endDate) {
        if (rentalRepository.findById(rentalId) != null) {
            throw new IllegalArgumentException("Rental ID already exists");
        }

        validateRentalPeriod(startDate, endDate);

        Vehicle vehicle = findVehicleById(vehicleId);

        if (vehicle == null) {
            throw new IllegalArgumentException("Vehicle not found");
        }

        if (vehicle.getStatus() != VehicleStatus.AVAILABLE || rentalRepository.hasActiveRentalForVehicle(vehicleId)) {
            throw new IllegalStateException("Vehicle is already rented");
        }

        Rental rental = new Rental(rentalId, vehicleId, customerName, startDate, endDate, true);
        rentalRepository.save(rental);

        vehicle.setStatus(VehicleStatus.RENTED);
        vehicleRepository.save(vehicle);

        return rental;
    }

    public List<Rental> getAllRentals() {
        return rentalRepository.findAll();
    }

    private void validateRentalPeriod(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("Rental dates are required");
        }

        if (endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("Rental end date cannot be before start date");
        }
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