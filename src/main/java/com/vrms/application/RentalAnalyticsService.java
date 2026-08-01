package com.vrms.application;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.vrms.domain.Rental;
import com.vrms.domain.Vehicle;
import com.vrms.domain.VehicleStatus;
import com.vrms.persistence.RentalRepository;
import com.vrms.persistence.VehicleRepository;

/**
 * Application service responsible for aggregating fleet analytics and generating management reports.
 */
public class RentalAnalyticsService {

    private final RentalRepository rentalRepository;
    private final VehicleRepository vehicleRepository;

    public RentalAnalyticsService(RentalRepository rentalRepository, VehicleRepository vehicleRepository) {
        if (rentalRepository == null) {
            throw new IllegalArgumentException("RentalRepository cannot be null");
        }
        if (vehicleRepository == null) {
            throw new IllegalArgumentException("VehicleRepository cannot be null");
        }

        this.rentalRepository = rentalRepository;
        this.vehicleRepository = vehicleRepository;
    }

    /**
     * Generates a comprehensive analytics report of the current fleet and rentals.
     *
     * @return populated AnalyticsReport instance
     */
    public AnalyticsReport generateReport() {
        List<Vehicle> vehicles = vehicleRepository.findAll();
        List<Rental> rentals = rentalRepository.findAll();

        int totalVehicles = vehicles.size();
        int availableVehicles = 0;
        int rentedVehicles = 0;

        Map<String, Vehicle> vehicleMap = new HashMap<>();
        for (Vehicle vehicle : vehicles) {
            vehicleMap.put(vehicle.getId(), vehicle);
            if (vehicle.getStatus() == VehicleStatus.AVAILABLE) {
                availableVehicles++;
            } else if (vehicle.getStatus() == VehicleStatus.RENTED) {
                rentedVehicles++;
            }
        }

        double utilizationRate = totalVehicles > 0 ? ((double) rentedVehicles / totalVehicles) * 100.0 : 0.0;

        int totalRentals = rentals.size();
        int activeRentals = 0;
        int closedRentals = 0;

        Map<String, Integer> rentalsByType = new HashMap<>();

        for (Rental rental : rentals) {
            if (rental.isActive()) {
                activeRentals++;
            } else {
                closedRentals++;
            }

            Vehicle vehicle = vehicleMap.get(rental.getVehicleId());
            String type = vehicle != null ? vehicle.getType() : "Unknown";
            rentalsByType.put(type, rentalsByType.getOrDefault(type, 0) + 1);
        }

        String mostPopularType = "None";
        int maxCount = 0;

        for (Map.Entry<String, Integer> entry : rentalsByType.entrySet()) {
            if (entry.getValue() > maxCount) {
                maxCount = entry.getValue();
                mostPopularType = entry.getKey();
            }
        }

        return new AnalyticsReport(
                totalVehicles,
                availableVehicles,
                rentedVehicles,
                utilizationRate,
                totalRentals,
                activeRentals,
                closedRentals,
                mostPopularType,
                rentalsByType
        );
    }
}
