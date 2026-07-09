package com.vrms.domain;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class VehicleTest {

    private Vehicle vehicle;

    @BeforeAll
    static void setUpBeforeClass() throws Exception {
    }

    @AfterAll
    static void tearDownAfterClass() throws Exception {
    }

    @BeforeEach
    void setUp() {
        vehicle = new Vehicle("V001", "Toyota Corolla", "Car", VehicleStatus.AVAILABLE);
    }

    @AfterEach
    void tearDown() throws Exception {
        vehicle = null;
    }

    @Test
    void getIdShouldReturnVehicleId() {
        assertEquals("V001", vehicle.getId());
    }

    @Test
    void getNameShouldReturnVehicleName() {
        assertEquals("Toyota Corolla", vehicle.getName());
    }

    @Test
    void getTypeShouldReturnVehicleType() {
    	assertEquals("Car", vehicle.getModel());
    }

    @Test
    void getStatusShouldReturnVehicleStatus() {
        assertEquals(VehicleStatus.AVAILABLE, vehicle.getStatus());
    }

    @Test
    void setStatusShouldChangeVehicleStatus() {
        vehicle.setStatus(VehicleStatus.RENTED);

        assertEquals(VehicleStatus.RENTED, vehicle.getStatus());
    }

    @Test
    void toStringShouldReturnVehicleInformation() {
    	assertEquals("V001 - Toyota Corolla - Car - AVAILABLE", vehicle.toString());
    }
}