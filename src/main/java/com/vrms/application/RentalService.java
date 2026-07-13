package com.vrms.application;

import java.time.LocalDate;
import java.util.List;

import com.vrms.domain.Rental;
import com.vrms.domain.Vehicle;
import com.vrms.domain.VehicleStatus;
import com.vrms.notification.NotificationService;
import com.vrms.persistence.RentalRepository;
import com.vrms.persistence.VehicleRepository;

public class RentalService {

    private final RentalRepository rentalRepository;
    private final VehicleRepository vehicleRepository;
    private final NotificationService notificationService;

    public RentalService(RentalRepository rentalRepository, VehicleRepository vehicleRepository) {
        this(rentalRepository, vehicleRepository, null);
    }

    public RentalService(RentalRepository rentalRepository, VehicleRepository vehicleRepository, NotificationService notificationService) {
        this.rentalRepository = rentalRepository;
        this.vehicleRepository = vehicleRepository;
        this.notificationService = notificationService;
    }

    public Rental rentVehicle(String rentalId, String vehicleId, String customerName, String customerEmail, LocalDate startDate, LocalDate endDate) {
        return rentVehicle(rentalId, vehicleId, customerName, customerEmail, startDate, endDate, 18, false);
    }

    public Rental rentVehicle(String rentalId, String vehicleId, String customerName, String customerEmail, LocalDate startDate, LocalDate endDate, int customerAge, boolean hasSpecialLicense) {
        if (rentalRepository.findById(rentalId) != null) {
            throw new IllegalArgumentException("Rental ID already exists");
        }

        validateRentalPeriod(startDate, endDate);

        if (customerEmail == null || customerEmail.trim().isEmpty()) {
            throw new IllegalArgumentException("Customer email is required");
        }

        Vehicle vehicle = findVehicleById(vehicleId);

        if (vehicle == null) {
            throw new IllegalArgumentException("Vehicle not found");
        }

        if (vehicle.getStatus() != VehicleStatus.AVAILABLE || rentalRepository.hasActiveRentalForVehicle(vehicleId)) {
            throw new IllegalStateException("Vehicle is already rented");
        }

        vehicle.validateRental(customerAge, hasSpecialLicense);

        Rental rental = new Rental(rentalId, vehicleId, customerName, customerEmail, startDate, endDate, true);
        rentalRepository.save(rental);

        vehicle.setStatus(VehicleStatus.RENTED);
        vehicleRepository.save(vehicle);

        if (notificationService != null) {
            String message = "Hello " + customerName
                    + ",\n\nYour rental has been successfully confirmed."
                    + "\nRental ID: " + rentalId
                    + "\nVehicle ID: " + vehicleId
                    + "\nVehicle type: " + vehicle.getType()
                    + "\nStart date: " + startDate
                    + "\nEnd date: " + endDate
                    + "\n\nThank you for using VRMS.";

            notificationService.sendRentalAccepted(rental, message);
        }

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