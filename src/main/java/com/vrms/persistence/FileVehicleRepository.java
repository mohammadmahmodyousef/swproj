package com.vrms.persistence;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

import com.vrms.domain.Vehicle;

public class FileVehicleRepository implements VehicleRepository {

    private final Path filePath;

    public FileVehicleRepository(Path filePath) {
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

    @Override
    public void save(Vehicle vehicle) {
        List<Vehicle> vehicles = findAll();
        boolean vehicleExists = false;

        for (int i = 0; i < vehicles.size(); i++) {
            if (vehicles.get(i).getId().equals(vehicle.getId())) {
                vehicles.set(i, vehicle);
                vehicleExists = true;
                break;
            }
        }

        if (!vehicleExists) {
            vehicles.add(vehicle);
        }

        writeAll(vehicles);
    }

    @Override
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