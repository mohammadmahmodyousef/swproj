package com.vrms.presentation;

import java.time.LocalDate;

import com.vrms.application.AuthService;
import com.vrms.application.RentalReturnResult;
import com.vrms.application.RentalReturnService;

public class RentalReturnController {

    private final RentalReturnService returnService;
    private final AuthService authService;

    public RentalReturnController(RentalReturnService returnService, AuthService authService) {
        this.returnService = returnService;
        this.authService = authService;
    }

    public String returnVehicle(String rentalId, LocalDate returnDate) {
        if (!authService.isLoggedIn()) {
            return "Please login first";
        }

        try {
            RentalReturnResult result = returnService.returnVehicle(rentalId, returnDate);

            return "Vehicle returned successfully"
                    + "\nRental ID: " + result.getRentalId()
                    + "\nVehicle ID: " + result.getVehicleId()
                    + "\nRental days: " + result.getRentalDays()
                    + "\nLate days: " + result.getLateDays()
                    + "\nRental cost: $" + String.format("%.2f", result.getRentalCost())
                    + "\nLate penalty: $" + String.format("%.2f", result.getLatePenalty())
                    + "\nTotal cost: $" + String.format("%.2f", result.getTotalCost());
        } catch (IllegalArgumentException | IllegalStateException e) {
            return e.getMessage();
        }
    }
}