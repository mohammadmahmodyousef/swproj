package com.vrms.presentation;

import java.time.LocalDate;
import java.util.List;

import com.vrms.application.AuthService;
import com.vrms.application.RentalService;
import com.vrms.domain.Rental;
import com.vrms.application.RentalRequest;
public class RentalController {

    private final RentalService rentalService;
    private final AuthService authService;

    public RentalController(RentalService rentalService,AuthService authService) {
        this.rentalService = rentalService;
        this.authService = authService;
    }

    public String rentVehicle(String rentalId,String vehicleId,String customerName,String customerEmail,LocalDate startDate,LocalDate endDate) {
        return rentVehicle(rentalId,vehicleId,customerName,customerEmail,startDate,endDate,18,false);
    }

    public String rentVehicle(String rentalId,String vehicleId,String customerName,String customerEmail,LocalDate startDate,LocalDate endDate,int customerAge,boolean hasSpecialLicense) {
        if (!authService.isLoggedIn()) {
            return "Please login first";
        }

        try {
            rentalService.rentVehicle(new RentalRequest(rentalId, vehicleId, customerName, customerEmail, startDate, endDate, customerAge, hasSpecialLicense));
            return "Vehicle rented successfully";
        } catch (IllegalArgumentException | IllegalStateException e) {
            return e.getMessage();
        }
    }

    public String viewActiveRentals() {
        if (!authService.isLoggedIn()) {
            return "Please login first";
        }

        List<Rental> activeRentals = rentalService.getActiveRentals();

        if (activeRentals.isEmpty()) {
            return "No active rentals";
        }

        StringBuilder result = new StringBuilder("Active rentals:\n");

        for (Rental rental : activeRentals) {
            result.append("Rental ID: ").append(rental.getRentalId()).append("\n");
            result.append("Customer name: ").append(rental.getCustomerName()).append("\n");
            result.append("Vehicle ID: ").append(rental.getVehicleId()).append("\n");
            result.append("Start date: ").append(rental.getStartDate()).append("\n");
            result.append("End date: ").append(rental.getEndDate()).append("\n");
            result.append("--------------------\n");
        }

        return result.toString().trim();
    }

    public String extendRental(String rentalId,LocalDate newEndDate) {
        if (!authService.isLoggedIn()) {
            return "Please login first";
        }

        try {
            Rental rental = rentalService.extendRental(rentalId,newEndDate);

            return "Rental extended successfully"
                    + "\nRental ID: " + rental.getRentalId()
                    + "\nNew end date: " + rental.getEndDate();
        } catch (IllegalArgumentException | IllegalStateException e) {
            return e.getMessage();
        }
    }
}