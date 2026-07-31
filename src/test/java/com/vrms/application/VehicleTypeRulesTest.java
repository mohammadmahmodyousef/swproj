package com.vrms.application;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.vrms.domain.Car;
import com.vrms.domain.ElectricVehicle;
import com.vrms.domain.Motorcycle;
import com.vrms.domain.Truck;
import com.vrms.domain.Van;
import com.vrms.domain.Vehicle;
import com.vrms.domain.VehicleStatus;
import com.vrms.persistence.FileRentalRepository;
import com.vrms.persistence.FileVehicleRepository;
import com.vrms.persistence.RentalRepository;
import com.vrms.persistence.VehicleRepository;

class VehicleTypeRulesTest {

    @TempDir
    Path tempDir;

    private RentalRepository rentalRepository;
    private VehicleRepository vehicleRepository;
    private RentalService rentalService;

    @BeforeEach
    void setUp() {
        rentalRepository = new FileRentalRepository(tempDir.resolve("rentals.txt"));
        vehicleRepository = new FileVehicleRepository(tempDir.resolve("vehicles.txt"));
        rentalService = new RentalService(rentalRepository, vehicleRepository);
    }

    @Test
    void repositoryShouldSupportMultipleVehicleTypes() {
        vehicleRepository.save(new Car("V001", "Toyota Corolla", "2024", VehicleStatus.AVAILABLE));
        vehicleRepository.save(new Motorcycle("V002", "Honda CBR", "2023", VehicleStatus.AVAILABLE));
        vehicleRepository.save(new Van("V003", "Ford Transit", "2024", VehicleStatus.AVAILABLE));
        vehicleRepository.save(new Truck("V004", "Mercedes Actros", "2022", VehicleStatus.AVAILABLE));
        vehicleRepository.save(new ElectricVehicle("V005", "Tesla Model 3", "2025", VehicleStatus.AVAILABLE, 80));

        List<Vehicle> vehicles = vehicleRepository.findAll();

        assertEquals(5, vehicles.size());
        assertTrue(vehicles.get(0) instanceof Car);
        assertTrue(vehicles.get(1) instanceof Motorcycle);
        assertTrue(vehicles.get(2) instanceof Van);
        assertTrue(vehicles.get(3) instanceof Truck);
        assertTrue(vehicles.get(4) instanceof ElectricVehicle);
    }

    @Test
    void truckShouldRequireSpecialLicense() {
        vehicleRepository.save(new Truck("V001", "Mercedes Actros", "2022", VehicleStatus.AVAILABLE));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> rentalService.rentVehicle("R001", "V001", "Ali", "ali@gmail.com", LocalDate.of(2026, 7, 20), LocalDate.of(2026, 7, 25), 30, false));

        assertEquals("Special truck license is required", exception.getMessage());
        assertEquals(0, rentalRepository.findAll().size());
    }

    @Test
    void truckShouldAcceptCustomerWithSpecialLicense() {
        vehicleRepository.save(new Truck("V001", "Mercedes Actros", "2022", VehicleStatus.AVAILABLE));

        assertNotNull(rentalService.rentVehicle("R001", "V001", "Ali", "ali@gmail.com", LocalDate.of(2026, 7, 20), LocalDate.of(2026, 7, 25), 30, true));
    }
    @Test
    void motorcycleShouldRejectCustomerUnderEighteen() {
        vehicleRepository.save(new Motorcycle("V001", "Honda CBR", "2023", VehicleStatus.AVAILABLE));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> rentalService.rentVehicle("R001", "V001", "Ali", "ali@gmail.com", LocalDate.of(2026, 7, 20), LocalDate.of(2026, 7, 25), 17, false));

        assertEquals("Customer must be at least 18 years old to rent a motorcycle", exception.getMessage());
    }

    @Test
    void motorcycleShouldAcceptCustomerWhoIsEighteen() {
        vehicleRepository.save(new Motorcycle("V001", "Honda CBR", "2023", VehicleStatus.AVAILABLE));

        assertNotNull(rentalService.rentVehicle("R001", "V001", "Ali", "ali@gmail.com", LocalDate.of(2026, 7, 20), LocalDate.of(2026, 7, 25), 18, false));
    }

    @Test
    void electricVehicleShouldRejectLowBatteryLevel() {
        vehicleRepository.save(new ElectricVehicle("V001", "Tesla Model 3", "2025", VehicleStatus.AVAILABLE, 15));

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> rentalService.rentVehicle("R001", "V001", "Ali", "ali@gmail.com", LocalDate.of(2026, 7, 20), LocalDate.of(2026, 7, 25), 25, false));

        assertEquals("Electric vehicle battery level must be at least 20%", exception.getMessage());
    }

    @Test
    void electricVehicleShouldAcceptValidBatteryLevel() {
        vehicleRepository.save(new ElectricVehicle("V001", "Tesla Model 3", "2025", VehicleStatus.AVAILABLE, 80));

        assertNotNull(rentalService.rentVehicle("R001", "V001", "Ali", "ali@gmail.com", LocalDate.of(2026, 7, 20), LocalDate.of(2026, 7, 25), 25, false));
    }
}