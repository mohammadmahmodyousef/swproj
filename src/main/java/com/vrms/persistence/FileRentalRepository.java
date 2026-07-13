package com.vrms.persistence;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

import com.vrms.domain.Rental;

/**
 * Stores and manages rental records inside a text file.
 * Each rental is saved as one line in the file.
 */

public class FileRentalRepository implements RentalRepository {

 
    private final Path filePath;

    /**
     * Creates the repository and prepares the rental file.
     *
     * @param filePath path of the rental data file
     */
    public FileRentalRepository(Path filePath) {
        this.filePath = filePath;
        createFile();
    }

    /**
     * Creates the required folders and rental file if they do not exist.
     */
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

    /**
     * Saves a new rental or updates an existing rental with the same ID.
     *
     * @param rental rental record to save
     */
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

    /**
     * Reads and returns all rental records from the file.
     *
     * @return list containing all rentals
     */
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

    /**
     * Searches for a rental using its rental ID.
     *
     * @param rentalId ID of the rental
     * @return matching rental, or null when it is not found
     */
    @Override
    public Rental findById(String rentalId) {
        for (Rental rental : findAll()) {
            if (rental.getRentalId().equals(rentalId)) {
                return rental;
            }
        }

        return null;
    }

    /**
     * Checks whether a vehicle currently has an active rental.
     *
     * @param vehicleId ID of the vehicle
     * @return true when an active rental exists, otherwise false
     */
    @Override
    public boolean hasActiveRentalForVehicle(String vehicleId) {
        for (Rental rental : findAll()) {
            if (rental.getVehicleId().equals(vehicleId) && rental.isActive()) {
                return true;
            }
        }

        return false;
    }

    /**
     * Rewrites the rental file using the provided rental list.
     *
     * @param rentals rentals that will be written to the file
     */
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