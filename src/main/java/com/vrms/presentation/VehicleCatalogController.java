package com.vrms.presentation;

import java.util.List;

import com.vrms.application.AuthService;
import com.vrms.application.VehicleService;
import com.vrms.domain.Vehicle;

public class VehicleCatalogController {

    private final VehicleService vehicleService;
    private final AuthService authService;

    public VehicleCatalogController(VehicleService vehicleService, AuthService authService) {
        this.vehicleService = vehicleService;
        this.authService = authService;
    }

    public String viewAvailableVehicles() {
        if (!authService.isLoggedIn()) {
            return "Manager must login first";
        }

        List<Vehicle> availableVehicles = vehicleService.getAvailableVehicles();

        if (availableVehicles.isEmpty()) {
            return "No available vehicles";
        }

        StringBuilder result = new StringBuilder("Available vehicles:\n");

        for (Vehicle vehicle : availableVehicles) {
            result.append(vehicle).append("\n");
        }

        return result.toString().trim();
    }
}
