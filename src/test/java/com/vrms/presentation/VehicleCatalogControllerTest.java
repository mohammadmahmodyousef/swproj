package com.vrms.presentation;

import static org.junit.jupiter.api.Assertions.*;


import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.vrms.application.AuthService;
import com.vrms.application.VehicleService;
import com.vrms.domain.Manager;
import com.vrms.domain.Vehicle;
import com.vrms.domain.VehicleStatus;
import com.vrms.persistence.FileManagerRepository;
import com.vrms.persistence.FileVehicleRepository;
import com.vrms.persistence.ManagerRepository;
import com.vrms.persistence.VehicleRepository;
import java.nio.file.Path;
class VehicleCatalogControllerTest {

    @TempDir
    Path tempDir;

    private AuthService authService;
    private VehicleCatalogController controller;

    @BeforeAll
    static void setUpBeforeClass() throws Exception {
    }

    @AfterAll
    static void tearDownAfterClass() throws Exception {
    }

    @BeforeEach
    void setUp() {
        ManagerRepository managerRepository = new FileManagerRepository(tempDir.resolve("managers.txt"));
        managerRepository.save(new Manager("admin", "1234"));
        authService = new AuthService(managerRepository);

        VehicleRepository vehicleRepository = new FileVehicleRepository(tempDir.resolve("vehicles.txt"));
        vehicleRepository.save(new Vehicle("V001", "Toyota Corolla", "Car", VehicleStatus.AVAILABLE));
        vehicleRepository.save(new Vehicle("V002", "BMW X5", "Car", VehicleStatus.RENTED));
        vehicleRepository.save(new Vehicle("V003", "Ford Transit", "Van", VehicleStatus.AVAILABLE));

        VehicleService vehicleService = new VehicleService(vehicleRepository);
        controller = new VehicleCatalogController(vehicleService, authService);
    }

    @AfterEach
    void tearDown() throws Exception {
        authService = null;
        controller = null;
    }
    @Test
    void viewAvailableVehiclesShouldRequireManagerLogin() {
        String result = controller.viewAvailableVehicles();

        assertEquals("Manager must login first", result);
    }

    @Test
    void viewAvailableVehiclesShouldDisplayAvailableVehicles() {
        authService.login("admin", "1234");

        String result = controller.viewAvailableVehicles();

        assertTrue(result.contains("Toyota Corolla"));
        assertTrue(result.contains("Ford Transit"));
    }

    @Test
    void viewAvailableVehiclesShouldHideRentedVehicles() {
        authService.login("admin", "1234");

        String result = controller.viewAvailableVehicles();

        assertFalse(result.contains("BMW X5"));
    }

    @Test
    void viewAvailableVehiclesShouldRequireLoginAgainAfterLogout() {
        authService.login("admin", "1234");
        authService.logout();

        String result = controller.viewAvailableVehicles();

        assertEquals("Manager must login first", result);
    }

    @Test
    void findVehicleByIdShouldReturnVehicleOrNull() {
        assertNotNull(controller.findVehicleById("V001"));
        assertNull(controller.findVehicleById("V999"));
    }
}