package com.vrms.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.vrms.domain.Car;
import com.vrms.domain.ElectricVehicle;
import com.vrms.domain.Rental;
import com.vrms.domain.Vehicle;
import com.vrms.domain.VehicleStatus;
import com.vrms.persistence.RentalRepository;
import com.vrms.persistence.VehicleRepository;

class RentalAnalyticsServiceTest {

    private RentalRepository rentalRepository;
    private VehicleRepository vehicleRepository;
    private RentalAnalyticsService analyticsService;

    @BeforeEach
    void setUp() {
        rentalRepository = mock(RentalRepository.class);
        vehicleRepository = mock(VehicleRepository.class);
        analyticsService = new RentalAnalyticsService(rentalRepository, vehicleRepository);
    }

    @Test
    void constructorShouldRejectNullArguments() {
        assertThrows(IllegalArgumentException.class, () -> new RentalAnalyticsService(null, vehicleRepository));
        assertThrows(IllegalArgumentException.class, () -> new RentalAnalyticsService(rentalRepository, null));
    }

    @Test
    void shouldGenerateEmptyReportWhenNoDataExists() {
        when(vehicleRepository.findAll()).thenReturn(Collections.emptyList());
        when(rentalRepository.findAll()).thenReturn(Collections.emptyList());

        AnalyticsReport report = analyticsService.generateReport();

        assertNotNull(report);
        assertEquals(0, report.getTotalVehicles());
        assertEquals(0, report.getAvailableVehicles());
        assertEquals(0, report.getRentedVehicles());
        assertEquals(0.0, report.getUtilizationRate(), 0.001);
        assertEquals(0, report.getTotalRentals());
        assertEquals(0, report.getActiveRentals());
        assertEquals(0, report.getClosedRentals());
        assertEquals("None", report.getMostPopularVehicleType());
    }

    @Test
    void shouldGenerateAccurateReportForFleetAndRentals() {
        Vehicle car1 = new Car("V001", "Toyota", "2024", VehicleStatus.AVAILABLE);
        Vehicle car2 = new Car("V002", "Honda", "2023", VehicleStatus.RENTED);
        Vehicle ev1 = new ElectricVehicle("V003", "Tesla", "2025", VehicleStatus.RENTED, 90);

        when(vehicleRepository.findAll()).thenReturn(Arrays.asList(car1, car2, ev1));

        Rental r1 = new Rental("R001", "V002", "Alice", "alice@example.com", LocalDate.now(), LocalDate.now().plusDays(3), true);
        Rental r2 = new Rental("R002", "V003", "Bob", "bob@example.com", LocalDate.now().minusDays(5), LocalDate.now().minusDays(2), false);
        Rental r3 = new Rental("R003", "V002", "Charlie", "charlie@example.com", LocalDate.now().minusDays(10), LocalDate.now().minusDays(7), false);

        when(rentalRepository.findAll()).thenReturn(Arrays.asList(r1, r2, r3));

        AnalyticsReport report = analyticsService.generateReport();

        assertEquals(3, report.getTotalVehicles());
        assertEquals(1, report.getAvailableVehicles());
        assertEquals(2, report.getRentedVehicles());
        assertEquals(66.666, report.getUtilizationRate(), 0.01);

        assertEquals(3, report.getTotalRentals());
        assertEquals(1, report.getActiveRentals());
        assertEquals(2, report.getClosedRentals());

        // Car had 2 rentals, Electric Vehicle had 1
        assertEquals("Car", report.getMostPopularVehicleType());
        assertEquals(2, report.getRentalsByType().get("Car"));
        assertEquals(1, report.getRentalsByType().get("Electric Vehicle"));
    }

    @Test
    void shouldHandleRentalsWithUnknownVehicles() {
        Vehicle car1 = new Car("V001", "Toyota", "2024", VehicleStatus.AVAILABLE);
        when(vehicleRepository.findAll()).thenReturn(Collections.singletonList(car1));

        // Rental references V999 which does not exist in repository
        Rental r1 = new Rental("R001", "V999", "Ghost", "ghost@example.com", LocalDate.now(), LocalDate.now().plusDays(1), true);
        when(rentalRepository.findAll()).thenReturn(Collections.singletonList(r1));

        AnalyticsReport report = analyticsService.generateReport();

        assertEquals(1, report.getTotalVehicles());
        assertEquals(1, report.getAvailableVehicles());
        assertEquals(0, report.getRentedVehicles());
        assertEquals(0.0, report.getUtilizationRate(), 0.001);
        assertEquals(1, report.getTotalRentals());
        assertEquals(1, report.getActiveRentals());
        assertEquals(0, report.getClosedRentals());
        assertEquals("Unknown", report.getMostPopularVehicleType());
        assertEquals(1, report.getRentalsByType().get("Unknown"));
    }
}
