package com.vrms.application;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import com.vrms.domain.Vehicle;
import com.vrms.domain.VehicleStatus;
import com.vrms.persistence.InMemoryVehicleRepository;
import com.vrms.persistence.VehicleRepository;


class VehicleServiceTest {
	private VehicleService vehicleService;
	@BeforeAll
	static void setUpBeforeClass() throws Exception {
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
	}

	@BeforeEach
    void setUp() {
        VehicleRepository repository = new InMemoryVehicleRepository();
        repository.save(new Vehicle("V001", "Toyota Corolla", "Car", VehicleStatus.AVAILABLE));
        repository.save(new Vehicle("V002", "BMW X5", "Car", VehicleStatus.RENTED));
        repository.save(new Vehicle("V003", "Ford Transit", "Van", VehicleStatus.AVAILABLE));
        vehicleService = new VehicleService(repository);
    }

    @AfterEach
    void tearDown() throws Exception {
        vehicleService = null;
    }

    @Test
    void getAvailableVehiclesShouldReturnAvailableVehicles() {
        List<Vehicle> vehicles = vehicleService.getAvailableVehicles();

        assertEquals(2, vehicles.size());
        assertEquals("V001", vehicles.get(0).getId());
        assertEquals("V003", vehicles.get(1).getId());
    }

    @Test
    void getAvailableVehiclesShouldHideRentedVehicles() {
        List<Vehicle> vehicles = vehicleService.getAvailableVehicles();

        assertFalse(vehicles.stream().anyMatch(vehicle -> vehicle.getId().equals("V002")));
    }

}
