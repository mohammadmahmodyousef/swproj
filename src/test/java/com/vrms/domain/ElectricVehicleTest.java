package com.vrms.domain;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ElectricVehicleTest {

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
	}

	@BeforeEach
	void setUp() throws Exception {
	}

	@AfterEach
	void tearDown() throws Exception {
	}


    @Test
    void constructorShouldCreateElectricVehicle() {
        ElectricVehicle vehicle = new ElectricVehicle("EV001","Tesla Model 3","2025",VehicleStatus.AVAILABLE,80);

        assertEquals("EV001",vehicle.getId());
        assertEquals("Tesla Model 3",vehicle.getName());
        assertEquals("2025",vehicle.getModel());
        assertEquals(VehicleStatus.AVAILABLE,vehicle.getStatus());
        assertEquals(80,vehicle.getBatteryLevel());
        assertEquals("Electric Vehicle",vehicle.getType());
    }

    @Test
    void constructorShouldRejectNegativeBatteryLevel() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,() -> new ElectricVehicle("EV001","Tesla","2025",VehicleStatus.AVAILABLE,-1));
        assertEquals("Battery level must be between 0 and 100",exception.getMessage());
    }

    @Test
    void constructorShouldRejectBatteryAboveOneHundred() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,() -> new ElectricVehicle("EV001","Tesla","2025",VehicleStatus.AVAILABLE,101));
        assertEquals("Battery level must be between 0 and 100",exception.getMessage());
    }

    @Test
    void constructorShouldAcceptBatteryBoundaryValues() {
        ElectricVehicle emptyBatteryVehicle = new ElectricVehicle("EV001","Tesla","2025",VehicleStatus.AVAILABLE,0);
        ElectricVehicle fullBatteryVehicle = new ElectricVehicle("EV002","Tesla","2025",VehicleStatus.AVAILABLE,100);

        assertEquals(0,emptyBatteryVehicle.getBatteryLevel());
        assertEquals(100,fullBatteryVehicle.getBatteryLevel());
    }

    @Test
    void setBatteryLevelShouldUpdateBattery() {
        ElectricVehicle vehicle = createVehicle(80);
        vehicle.setBatteryLevel(55);
        assertEquals(55,vehicle.getBatteryLevel());
    }

    @Test
    void setBatteryLevelShouldRejectNegativeValue() {
        ElectricVehicle vehicle = createVehicle(80);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,() -> vehicle.setBatteryLevel(-1));

        assertEquals("Battery level must be between 0 and 100",exception.getMessage());
    }

    @Test
    void setBatteryLevelShouldRejectValueAboveOneHundred() {
        ElectricVehicle vehicle = createVehicle(80);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,() -> vehicle.setBatteryLevel(101));

        assertEquals("Battery level must be between 0 and 100",exception.getMessage());
    }

    @Test
    void validateRentalShouldAcceptBatteryAtLeastTwentyPercent() {
        ElectricVehicle vehicle = createVehicle(20);
        assertDoesNotThrow(() -> vehicle.validateRental(18,false));
    }

    @Test
    void validateRentalShouldRejectLowBattery() {
        ElectricVehicle vehicle = createVehicle(19);

        IllegalStateException exception = assertThrows(IllegalStateException.class,() -> vehicle.validateRental(30,true));

        assertEquals("Electric vehicle battery level must be at least 20%",exception.getMessage());
    }

    @Test
    void toFileLineShouldIncludeVehicleTypeAndBattery() {
        ElectricVehicle vehicle = createVehicle(80);
        assertEquals("EV001|Tesla Model 3|2025|AVAILABLE|Electric Vehicle|80",vehicle.toFileLine());
    }

    @Test
    void toStringShouldIncludeBatteryLevel() {
        ElectricVehicle vehicle = createVehicle(80);
        assertEquals("EV001 - Tesla Model 3 - 2025 - Electric Vehicle - AVAILABLE - Battery: 80%",vehicle.toString());
    }

    private ElectricVehicle createVehicle(int batteryLevel) {
        return new ElectricVehicle("EV001","Tesla Model 3","2025",VehicleStatus.AVAILABLE,batteryLevel);
    }
}
