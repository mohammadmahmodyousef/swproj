package com.vrms.persistence;

import com.vrms.domain.Vehicle;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

public class VehicleFileRepository {

    private Path filePath;

    public VehicleFileRepository(Path filePath) {
        this.filePath = filePath;
        createFile();
    }

    private void createFile() {
        try {
            if (filePath.getParent() != null) {
                Files.createDirectories(filePath.getParent());
            }

            if (!Files.exists(filePath)) {
                Files.createFile(filePath);
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not create vehicles file", e);
        }
    }

    public List<Vehicle> findAll() {
        List<Vehicle> vehicles = new ArrayList<>();

        try {
            List<String> lines = Files.readAllLines(filePath, StandardCharsets.UTF_8);

            for (String line : lines) {
                if (!line.trim().isEmpty()) {
                    vehicles.add(Vehicle.fromFileLine(line));
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not read vehicles file", e);
        }

        return vehicles;
    }

    public List<Vehicle> findAvailableVehicles() {
        List<Vehicle> availableVehicles = new ArrayList<>();

        for (Vehicle vehicle : findAll()) {
            if (vehicle.isAvailable()) {
                availableVehicles.add(vehicle);
            }
        }

        return availableVehicles;
    }

    public Vehicle findById(String id) {
        for (Vehicle vehicle : findAll()) {
            if (vehicle.getId().equals(id)) {
                return vehicle;
            }
        }

        return null;
    }

    public void save(Vehicle vehicle) {
        List<Vehicle> vehicles = findAll();
        boolean found = false;

        for (int i = 0; i < vehicles.size(); i++) {
            if (vehicles.get(i).getId().equals(vehicle.getId())) {
                vehicles.set(i, vehicle);
                found = true;
                break;
            }
        }

        if (!found) {
            vehicles.add(vehicle);
        }

        writeAll(vehicles);
    }

    public void updateStatus(String id, com.vrms.domain.VehicleStatus status) {
        Vehicle vehicle = findById(id);

        if (vehicle == null) {
            throw new IllegalArgumentException("Vehicle not found");
        }

        vehicle.setStatus(status);
        save(vehicle);
    }

    private void writeAll(List<Vehicle> vehicles) {
        List<String> lines = new ArrayList<>();

        for (Vehicle vehicle : vehicles) {
            lines.add(vehicle.toFileLine());
        }

        try {
            Files.write(filePath, lines, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Could not save vehicles", e);
        }
    }
}