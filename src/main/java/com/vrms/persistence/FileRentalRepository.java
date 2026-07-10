package com.vrms.persistence;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

import com.vrms.domain.Rental;

public class FileRentalRepository implements RentalRepository {

    private final Path filePath;

    public FileRentalRepository(Path filePath) {
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
            throw new RuntimeException("Could not create rentals file", e);
        }
    }

    @Override
    public void save(Rental rental) {
        List<Rental> rentals = findAll();
        boolean rentalExists = false;

        for (int i = 0; i < rentals.size(); i++) {
            if (rentals.get(i).getRentalId().equals(rental.getRentalId())) {
                rentals.set(i, rental);
                rentalExists = true;
                break;
            }
        }

        if (!rentalExists) {
            rentals.add(rental);
        }

        writeAll(rentals);
    }

    @Override
    public List<Rental> findAll() {
        List<Rental> rentals = new ArrayList<>();

        try {
            List<String> lines = Files.readAllLines(filePath, StandardCharsets.UTF_8);

            for (String line : lines) {
                if (!line.trim().isEmpty()) {
                    rentals.add(Rental.fromFileLine(line));
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not read rentals file", e);
        }

        return rentals;
    }

    @Override
    public Rental findById(String rentalId) {
        for (Rental rental : findAll()) {
            if (rental.getRentalId().equals(rentalId)) {
                return rental;
            }
        }

        return null;
    }

    @Override
    public boolean hasActiveRentalForVehicle(String vehicleId) {
        for (Rental rental : findAll()) {
            if (rental.getVehicleId().equals(vehicleId) && rental.isActive()) {
                return true;
            }
        }

        return false;
    }

    private void writeAll(List<Rental> rentals) {
        List<String> lines = new ArrayList<>();

        for (Rental rental : rentals) {
            lines.add(rental.toFileLine());
        }

        try {
            Files.write(filePath, lines, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Could not save rentals", e);
        }
    }
}