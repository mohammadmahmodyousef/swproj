package com.vrms.application;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Path;
import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.vrms.application.strategy.DailyLateReturnPenaltyStrategy;
import com.vrms.application.strategy.DailyRentalPricingStrategy;
import com.vrms.application.strategy.LateReturnPenaltyStrategy;
import com.vrms.application.strategy.RentalPricingStrategy;
import com.vrms.domain.Rental;
import com.vrms.domain.Vehicle;
import com.vrms.domain.VehicleStatus;
import com.vrms.persistence.FileRentalRepository;
import com.vrms.persistence.FileVehicleRepository;
import com.vrms.persistence.RentalRepository;
import com.vrms.persistence.VehicleRepository;

class RentalReturnServiceTest {

    @TempDir
    Path tempDir;

    private RentalReturnService returnService;
    private RentalRepository rentalRepository;
    private VehicleRepository vehicleRepository;

    @BeforeEach
    void setUp() {
        rentalRepository = new FileRentalRepository(tempDir.resolve("rentals.txt"));
        vehicleRepository = new FileVehicleRepository(tempDir.resolve("vehicles.txt"));

        Vehicle vehicle = new Vehicle("V001", "Toyota Corolla", "Car", VehicleStatus.RENTED);
        Rental rental = new Rental("R001", "V001", "Ali", "ali@gmail.com", LocalDate.of(2026, 7, 10), LocalDate.of(2026, 7, 15), true);

        vehicleRepository.save(vehicle);
        rentalRepository.save(rental);

        RentalPricingStrategy pricingStrategy = new DailyRentalPricingStrategy(50);
        LateReturnPenaltyStrategy penaltyStrategy = new DailyLateReturnPenaltyStrategy(20);

        returnService = new RentalReturnService(rentalRepository, vehicleRepository, pricingStrategy, penaltyStrategy);
    }
    @Test
    void returnVehicleShouldMakeVehicleAvailable() {
        returnService.returnVehicle("R001", LocalDate.of(2026, 7, 15));

        Vehicle vehicle = vehicleRepository.findAll().get(0);

        assertEquals(VehicleStatus.AVAILABLE, vehicle.getStatus());
    }

    @Test
    void returnVehicleShouldCloseRentalRecord() {
        returnService.returnVehicle("R001", LocalDate.of(2026, 7, 15));

        Rental rental = rentalRepository.findAll().get(0);

        assertFalse(rental.isActive());
    }

    @Test
    void returnVehicleShouldCalculateCorrectTotalWithoutPenalty() {
        RentalReturnResult result = returnService.returnVehicle("R001", LocalDate.of(2026, 7, 15));

        assertEquals(5, result.getRentalDays());
        assertEquals(0, result.getLateDays());
        assertEquals(250.0, result.getRentalCost(), 0.001);
        assertEquals(0.0, result.getLatePenalty(), 0.001);
        assertEquals(250.0, result.getTotalCost(), 0.001);
    }

    @Test
    void lateReturnShouldCalculatePenaltyCorrectly() {
        RentalReturnResult result = returnService.returnVehicle("R001", LocalDate.of(2026, 7, 17));

        assertEquals(7, result.getRentalDays());
        assertEquals(2, result.getLateDays());
        assertEquals(350.0, result.getRentalCost(), 0.001);
        assertEquals(40.0, result.getLatePenalty(), 0.001);
        assertEquals(390.0, result.getTotalCost(), 0.001);
    }

    @Test
    void oneLateDayShouldApplyOneDayPenalty() {
        RentalReturnResult result = returnService.returnVehicle("R001", LocalDate.of(2026, 7, 16));

        assertEquals(1, result.getLateDays());
        assertEquals(20.0, result.getLatePenalty(), 0.001);
        assertEquals(320.0, result.getTotalCost(), 0.001);
    }

    @Test
    void earlyReturnShouldNotApplyPenalty() {
        RentalReturnResult result = returnService.returnVehicle("R001", LocalDate.of(2026, 7, 13));

        assertEquals(3, result.getRentalDays());
        assertEquals(0, result.getLateDays());
        assertEquals(0.0, result.getLatePenalty(), 0.001);
        assertEquals(150.0, result.getTotalCost(), 0.001);
    }

    @Test
    void sameDayRentalShouldCostOneDayWithoutPenalty() {
        RentalReturnResult result = returnService.returnVehicle("R001", LocalDate.of(2026, 7, 10));

        assertEquals(1, result.getRentalDays());
        assertEquals(0, result.getLateDays());
        assertEquals(50.0, result.getTotalCost(), 0.001);
    }

    @Test
    void returnVehicleShouldRejectClosedRental() {
        returnService.returnVehicle("R001", LocalDate.of(2026, 7, 15));

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> returnService.returnVehicle("R001", LocalDate.of(2026, 7, 15)));

        assertEquals("Rental is already closed", exception.getMessage());
    }
    }