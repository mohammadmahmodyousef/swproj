
package com.vrms.persistence;

import static org.junit.jupiter.api.Assertions.*;

import com.vrms.domain.Vehicle;
import com.vrms.domain.VehicleStatus;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.nio.file.Path;
import java.util.List;
import com.vrms.domain.Vehicle;
import com.vrms.domain.VehicleStatus;
class FileVehicleRepositoryTest {
	 @TempDir
	    Path tempDir;

	    private VehicleRepository repository;	@BeforeAll
	static void setUpBeforeClass() throws Exception {
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
	}

    @BeforeEach
    void setUp() {
        repository = new FileVehicleRepository(tempDir.resolve("vehicles.txt"));
    }

    @AfterEach
    void tearDown() throws Exception {
        repository = null;
    }
    @Test
    void saveShouldAddVehicleToRepository() {
        Vehicle vehicle = new Vehicle("V001", "Toyota Corolla", "Car", VehicleStatus.AVAILABLE);

        repository.save(vehicle);

        List<Vehicle> vehicles = repository.findAll();

        assertEquals(1, vehicles.size());
        assertEquals("V001", vehicles.get(0).getId());
        assertEquals("Toyota Corolla", vehicles.get(0).getName());
        assertEquals("Car", vehicles.get(0).getModel());
        assertEquals(VehicleStatus.AVAILABLE, vehicles.get(0).getStatus());
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

        List<Vehicle> vehicles = repository.findAll();

        assertEquals(1, vehicles.size());
        assertEquals("New Vehicle", vehicles.get(0).getName());
        assertEquals(VehicleStatus.RENTED, vehicles.get(0).getStatus());
    }
}
