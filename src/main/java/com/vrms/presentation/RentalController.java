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

    public String rentVehicle(String rentalId, String vehicleId, String customerName, LocalDate startDate, LocalDate endDate) {
        if (!authService.isLoggedIn()) {
            return "Manager must login first";
        }

        try {
            rentalService.rentVehicle(rentalId, vehicleId, customerName, startDate, endDate);
            return "Rental created successfully";
        } catch (IllegalArgumentException | IllegalStateException e) {
            return e.getMessage();
        }
    }
}