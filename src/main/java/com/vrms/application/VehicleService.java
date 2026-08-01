package com.vrms.application;

import java.util.ArrayList;
import java.util.List;

import com.vrms.domain.Vehicle;
import com.vrms.domain.VehicleStatus;
import com.vrms.persistence.VehicleRepository;

public class VehicleService {

    private final VehicleRepository vehicleRepository;

    public VehicleService(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    public List<Vehicle> getAvailableVehicles() {
        List<Vehicle> availableVehicles = new ArrayList<>();

        for (Vehicle vehicle : vehicleRepository.findAll()) {
            if (vehicle.getStatus() == VehicleStatus.AVAILABLE) {
                availableVehicles.add(vehicle);
            }
        }

        return availableVehicles;
    }

    public Vehicle findVehicleById(String id) {
        if (id == null || id.trim().isEmpty()) {
            return null;
        }

        for (Vehicle vehicle : vehicleRepository.findAll()) {
            if (vehicle.getId().equalsIgnoreCase(id.trim())) {
                return vehicle;
            }
        }

        return null;
    }
}
