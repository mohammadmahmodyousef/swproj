package com.vrms.application;

import com.vrms.domain.Rental;
import com.vrms.domain.Vehicle;
import com.vrms.domain.VehicleStatus;
import com.vrms.persistence.RentalRepository;
import com.vrms.persistence.VehicleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RentalDurationLimitTest {

    private RentalRepository rentalRepository;
    private VehicleRepository vehicleRepository;
    private Vehicle vehicle;
    private RentalService rentalService;

    @BeforeEach
    void setUp() {
        rentalRepository = mock(RentalRepository.class);
        vehicleRepository = mock(VehicleRepository.class);
        vehicle = mock(Vehicle.class);

        when(rentalRepository.findById(anyString())).thenReturn(null);
        when(rentalRepository.hasActiveRentalForVehicle("V001")).thenReturn(false);
        when(vehicleRepository.findAll()).thenReturn(List.of(vehicle));
        when(vehicle.getId()).thenReturn("V001");
        when(vehicle.getStatus()).thenReturn(VehicleStatus.AVAILABLE);

        rentalService = new RentalService(rentalRepository,vehicleRepository);
    }

    @Test
    void shouldAcceptRentalForExactlyThirtyDays() {
        Rental rental = rentalService.rentVehicle("R001","V001","Ali","ali@gmail.com",LocalDate.of(2026,7,1),LocalDate.of(2026,7,31));

        assertNotNull(rental);
        assertEquals("R001",rental.getRentalId());
        assertEquals(LocalDate.of(2026,7,1),rental.getStartDate());
        assertEquals(LocalDate.of(2026,7,31),rental.getEndDate());

        verify(vehicle).validateRental(18,false);
        verify(rentalRepository).save(rental);
        verify(vehicle).setStatus(VehicleStatus.RENTED);
        verify(vehicleRepository).save(vehicle);
    }

    @Test
    void shouldRejectRentalLongerThanThirtyDays() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,() -> rentalService.rentVehicle("R002","V001","Ali","ali@gmail.com",LocalDate.of(2026,7,1),LocalDate.of(2026,8,1)));

        assertEquals("Rental period cannot exceed 30 days",exception.getMessage());

        verify(rentalRepository,never()).save(any(Rental.class));
        verify(vehicleRepository,never()).save(any(Vehicle.class));
        verify(vehicle,never()).setStatus(any(VehicleStatus.class));
    }

    @Test
    void shouldRejectRentalForOneYear() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,() -> rentalService.rentVehicle("R003","V001","Ali","ali@gmail.com",LocalDate.of(2026,7,1),LocalDate.of(2027,7,1)));

        assertEquals("Rental period cannot exceed 30 days",exception.getMessage());

        verify(rentalRepository,never()).save(any(Rental.class));
        verifyNoInteractions(vehicleRepository);
    }

    @Test
    void shouldStillAcceptSameDayRental() {
        Rental rental = rentalService.rentVehicle("R004","V001","Ali","ali@gmail.com",LocalDate.of(2026,7,1),LocalDate.of(2026,7,1));

        assertNotNull(rental);
        assertEquals(LocalDate.of(2026,7,1),rental.getStartDate());
        assertEquals(LocalDate.of(2026,7,1),rental.getEndDate());
        verify(rentalRepository).save(rental);
    }
}