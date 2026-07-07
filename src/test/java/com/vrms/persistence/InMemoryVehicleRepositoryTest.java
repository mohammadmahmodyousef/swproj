
package com.vrms.persistence;

import static org.junit.jupiter.api.Assertions.*;

import com.vrms.domain.Vehicle;
import com.vrms.domain.VehicleStatus;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class InMemoryVehicleRepositoryTest {
	private VehicleRepository repository;
	@BeforeAll
	static void setUpBeforeClass() throws Exception {
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
	}

	@BeforeEach
    void setUp() {
        repository = new InMemoryVehicleRepository();
    }

    @AfterEach
    void tearDown() throws Exception {
        repository = null;
    }
    @Test
    void saveShouldAddVehicleToRepository() {
        Vehicle vehicle = new Vehicle("V001", "Toyota Corolla", "Car", VehicleStatus.AVAILABLE);

        repository.save(vehicle);

        assertEquals(1, repository.findAll().size());
        assertSame(vehicle, repository.findAll().get(0));
    }

    @Test
    void findAllShouldReturnAllSavedVehicles() {
        repository.save(new Vehicle("V001", "Toyota Corolla", "Car", VehicleStatus.AVAILABLE));
        repository.save(new Vehicle("V002", "BMW X5", "Car", VehicleStatus.RENTED));

        assertEquals(2, repository.findAll().size());
    }

    @Test
    void saveShouldReplaceVehicleWithSameId() {
        Vehicle oldVehicle = new Vehicle("V001", "Old Vehicle", "Car", VehicleStatus.AVAILABLE);
        Vehicle newVehicle = new Vehicle("V001", "New Vehicle", "Car", VehicleStatus.RENTED);

        repository.save(oldVehicle);
        repository.save(newVehicle);

        assertEquals(1, repository.findAll().size());
        assertSame(newVehicle, repository.findAll().get(0));
    }
}
