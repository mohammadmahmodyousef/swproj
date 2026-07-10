package com.vrms.application;


import org.junit.jupiter.api.io.TempDir;
import com.vrms.domain.Rental;
import com.vrms.domain.Vehicle;
import com.vrms.domain.VehicleStatus;
import com.vrms.persistence.FileRentalRepository;
import com.vrms.persistence.FileVehicleRepository;
import com.vrms.persistence.RentalRepository;
import com.vrms.persistence.VehicleRepository;
import java.nio.file.Path;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RentalServiceTest {

	@TempDir
    Path tempDir;

    private RentalService rentalService;
    private RentalRepository rentalRepository;
    private VehicleRepository vehicleRepository;
	
	
	@BeforeAll
	static void setUpBeforeClass() throws Exception {
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
	}

	@BeforeEach
    void setUp() {
        rentalRepository = new FileRentalRepository(tempDir.resolve("rentals.txt"));
        vehicleRepository = new FileVehicleRepository(tempDir.resolve("vehicles.txt"));

        vehicleRepository.save(new Vehicle("V001", "Toyota Corolla", "Car", VehicleStatus.AVAILABLE));

        rentalService = new RentalService(rentalRepository, vehicleRepository);
    }

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
    void rentVehicleShouldCreateRentalRecord() {
        Rental rental = rentalService.rentVehicle("R001", "V001", "Ali", LocalDate.of(2026, 7, 10), LocalDate.of(2026, 7, 15));

        assertNotNull(rental);
        assertEquals(1, rentalRepository.findAll().size());
        assertEquals("R001", rentalRepository.findAll().get(0).getRentalId());
        assertEquals("V001", rentalRepository.findAll().get(0).getVehicleId());
    }

    @Test
    void rentVehicleShouldChangeVehicleStatusToRented() {
        rentalService.rentVehicle("R001", "V001", "Ali", LocalDate.of(2026, 7, 10), LocalDate.of(2026, 7, 15));

        Vehicle vehicle = vehicleRepository.findAll().get(0);

        assertEquals(VehicleStatus.RENTED, vehicle.getStatus());
    }

    @Test
    void rentVehicleShouldRejectDuplicateRental() {
        rentalService.rentVehicle("R001", "V001", "Ali", LocalDate.of(2026, 7, 10), LocalDate.of(2026, 7, 15));

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            rentalService.rentVehicle("R002", "V001", "Ahmad", LocalDate.of(2026, 7, 16), LocalDate.of(2026, 7, 20));
        });

        assertEquals("Vehicle is already rented", exception.getMessage());
        assertEquals(1, rentalRepository.findAll().size());
    }

}
