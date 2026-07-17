package com.vrms.persistence;

import static org.junit.jupiter.api.Assertions.*;


import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.vrms.domain.Vehicle;
import com.vrms.domain.VehicleStatus;
import com.vrms.persistence.VehicleFileRepository;

import org.junit.jupiter.api.io.TempDir;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
class VehicleFileRepositoryTest {
    @TempDir
    Path tempDir;

    private Path filePath;
    private VehicleFileRepository repository;

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
	}

    @BeforeEach
    void setUp() {
        filePath = tempDir.resolve("nested/data/vehicles.txt");
        repository = new VehicleFileRepository(filePath);
    }


	@AfterEach
	void tearDown() throws Exception {
	}
    @Test
    void constructorShouldCreateFileAndParentDirectories() {
        assertTrue(Files.exists(filePath));
        assertTrue(Files.exists(filePath.getParent()));
    }

    @Test
    void saveShouldAddAndFindVehicle() {
        Vehicle vehicle = new Vehicle("V001","Toyota Corolla","2022",VehicleStatus.AVAILABLE);

        repository.save(vehicle);

        Vehicle savedVehicle = repository.findById("V001");

        assertNotNull(savedVehicle);
        assertEquals("V001",savedVehicle.getId());
        assertEquals("Toyota Corolla",savedVehicle.getName());
        assertEquals("2022",savedVehicle.getModel());
        assertEquals(VehicleStatus.AVAILABLE,savedVehicle.getStatus());
    }

    @Test
    void saveShouldReplaceVehicleWithSameId() {
        repository.save(new Vehicle("V001","Old Vehicle","2020",VehicleStatus.AVAILABLE));
        repository.save(new Vehicle("V001","New Vehicle","2025",VehicleStatus.RENTED));

        List<Vehicle> vehicles = repository.findAll();

        assertEquals(1,vehicles.size());
        assertEquals("New Vehicle",vehicles.get(0).getName());
        assertEquals("2025",vehicles.get(0).getModel());
        assertEquals(VehicleStatus.RENTED,vehicles.get(0).getStatus());
    }

    @Test
    void findAllShouldIgnoreBlankLines() throws Exception {
        Files.writeString(filePath,"\nV001|Toyota|2022|AVAILABLE|Vehicle|\n\nV002|Kia|2021|RENTED|Vehicle|\n",StandardCharsets.UTF_8);

        List<Vehicle> vehicles = repository.findAll();

        assertEquals(2,vehicles.size());
        assertEquals("V001",vehicles.get(0).getId());
        assertEquals("V002",vehicles.get(1).getId());
    }

    @Test
    void findAvailableVehiclesShouldReturnOnlyAvailableVehicles() {
        repository.save(new Vehicle("V001","Toyota","2022",VehicleStatus.AVAILABLE));
        repository.save(new Vehicle("V002","Kia","2021",VehicleStatus.RENTED));
        repository.save(new Vehicle("V003","BMW","2025",VehicleStatus.AVAILABLE));

        List<Vehicle> availableVehicles = repository.findAvailableVehicles();

        assertEquals(2,availableVehicles.size());
        assertEquals("V001",availableVehicles.get(0).getId());
        assertEquals("V003",availableVehicles.get(1).getId());
    }

    @Test
    void findByIdShouldReturnNullWhenVehicleDoesNotExist() {
        repository.save(new Vehicle("V001","Toyota","2022",VehicleStatus.AVAILABLE));

        assertNull(repository.findById("UNKNOWN"));
    }

    @Test
    void updateStatusShouldChangeVehicleStatus() {
        repository.save(new Vehicle("V001","Toyota","2022",VehicleStatus.AVAILABLE));

        repository.updateStatus("V001",VehicleStatus.RENTED);

        Vehicle vehicle = repository.findById("V001");

        assertNotNull(vehicle);
        assertEquals(VehicleStatus.RENTED,vehicle.getStatus());
    }

    @Test
    void updateStatusShouldRejectUnknownVehicle() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,() -> repository.updateStatus("UNKNOWN",VehicleStatus.RENTED));

        assertEquals("Vehicle not found",exception.getMessage());
    }
}
