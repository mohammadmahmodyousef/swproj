package com.vrms.bootstrap;

import com.vrms.domain.Manager;
import com.vrms.domain.Vehicle;
import com.vrms.domain.VehicleStatus;
import com.vrms.persistence.ManagerFileRepository;
import com.vrms.persistence.VehicleFileRepository;

public class DataInitializer {

    private ManagerFileRepository managerRepository;
    private VehicleFileRepository vehicleRepository;

    public DataInitializer(ManagerFileRepository managerRepository, VehicleFileRepository vehicleRepository) {
        this.managerRepository = managerRepository;
        this.vehicleRepository = vehicleRepository;
    }

    public void initialize() {
        if (managerRepository.findAll().isEmpty()) {
            managerRepository.save(new Manager("admin", "admin123"));
        }

        if (vehicleRepository.findAll().isEmpty()) {
            vehicleRepository.save(new Vehicle("V001", "Toyota Corolla", "2022", VehicleStatus.AVAILABLE));
            vehicleRepository.save(new Vehicle("V002", "Kia Sportage", "2021", VehicleStatus.RENTED));
            vehicleRepository.save(new Vehicle("V003", "Hyundai Elantra", "2023", VehicleStatus.AVAILABLE));
        }
    }
}