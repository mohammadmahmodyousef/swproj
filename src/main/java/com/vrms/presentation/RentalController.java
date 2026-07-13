package com.vrms.presentation;

import java.time.LocalDate;

import com.vrms.application.AuthService;
import com.vrms.application.RentalService;

public class RentalController {

    private final RentalService rentalService;
    private final AuthService authService;

    public RentalController(RentalService rentalService, AuthService authService) {
        this.rentalService = rentalService;
        this.authService = authService;
    }

    public String rentVehicle(String rentalId, String vehicleId, String customerName, String customerEmail, LocalDate startDate, LocalDate endDate) {
        return rentVehicle(rentalId, vehicleId, customerName, customerEmail, startDate, endDate, 18, false);
    }

    public String rentVehicle(String rentalId, String vehicleId, String customerName, String customerEmail, LocalDate startDate, LocalDate endDate, int customerAge, boolean hasSpecialLicense) {
        if (!authService.isLoggedIn()) {
            return "Please login first";
        }

        try {
            rentalService.rentVehicle(rentalId, vehicleId, customerName, customerEmail, startDate, endDate, customerAge, hasSpecialLicense);
            return "Vehicle rented successfully";
        } catch (IllegalArgumentException | IllegalStateException e) {
            return e.getMessage();
        }
    }
}