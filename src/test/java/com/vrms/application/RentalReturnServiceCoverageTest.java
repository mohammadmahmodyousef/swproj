package com.vrms.application;

import static org.junit.jupiter.api.Assertions.*;


import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.vrms.application.strategy.LateReturnPenaltyStrategy;
import com.vrms.application.strategy.RentalPricingStrategy;
import com.vrms.domain.Rental;
import com.vrms.persistence.RentalRepository;
import com.vrms.persistence.VehicleRepository;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.*;
class RentalReturnServiceCoverageTest {
    private RentalRepository rentalRepository;
    private VehicleRepository vehicleRepository;
    private RentalReturnService returnService;
	@BeforeAll
	static void setUpBeforeClass() throws Exception {
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
	}

    @BeforeEach
    void setUp() {
        rentalRepository = mock(RentalRepository.class);
        vehicleRepository = mock(VehicleRepository.class);

        RentalPricingStrategy pricingStrategy = mock(RentalPricingStrategy.class);
        LateReturnPenaltyStrategy penaltyStrategy = mock(LateReturnPenaltyStrategy.class);

        returnService = new RentalReturnService(rentalRepository,vehicleRepository,pricingStrategy,penaltyStrategy);
    }

	@AfterEach
	void tearDown() throws Exception {
	}

    @Test
    void returnVehicleShouldRejectNullRentalId() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,() -> returnService.returnVehicle(null,LocalDate.of(2026,7,15)));

        assertEquals("Rental ID is required",exception.getMessage());
    }

    @Test
    void returnVehicleShouldRejectBlankRentalId() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,() -> returnService.returnVehicle("   ",LocalDate.of(2026,7,15)));

        assertEquals("Rental ID is required",exception.getMessage());
    }

    @Test
    void returnVehicleShouldRejectNullReturnDate() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,() -> returnService.returnVehicle("R001",null));

        assertEquals("Return date is required",exception.getMessage());
    }

    @Test
    void returnVehicleShouldRejectUnknownRental() {
        when(rentalRepository.findAll()).thenReturn(List.of());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,() -> returnService.returnVehicle("UNKNOWN",LocalDate.of(2026,7,15)));

        assertEquals("Rental not found",exception.getMessage());
    }

    @Test
    void returnVehicleShouldRejectDateBeforeRentalStartDate() {
        Rental rental = new Rental("R001","V001","Ali","ali@gmail.com",LocalDate.of(2026,7,10),LocalDate.of(2026,7,15),true);

        when(rentalRepository.findAll()).thenReturn(List.of(rental));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,() -> returnService.returnVehicle("R001",LocalDate.of(2026,7,9)));

        assertEquals("Return date cannot be before rental start date",exception.getMessage());
    }

    @Test
    void returnVehicleShouldRejectUnknownVehicle() {
        Rental rental = new Rental("R001","V001","Ali","ali@gmail.com",LocalDate.of(2026,7,10),LocalDate.of(2026,7,15),true);

        when(rentalRepository.findAll()).thenReturn(List.of(rental));
        when(vehicleRepository.findAll()).thenReturn(List.of());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,() -> returnService.returnVehicle("R001",LocalDate.of(2026,7,15)));

        assertEquals("Vehicle not found",exception.getMessage());
    }

}
