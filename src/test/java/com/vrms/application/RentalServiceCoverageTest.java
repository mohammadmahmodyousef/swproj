package com.vrms.application;

import static org.junit.jupiter.api.Assertions.*;


import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.util.List;

import com.vrms.domain.Vehicle;

import com.vrms.notification.NotificationService;
import com.vrms.persistence.RentalRepository;
import com.vrms.persistence.VehicleRepository;
import com.vrms.domain.Rental;
import com.vrms.domain.VehicleStatus;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.*;

class RentalServiceCoverageTest {
	private RentalRepository rentalRepository;
    private VehicleRepository vehicleRepository;
    private NotificationService notificationService;
    private Vehicle vehicle;
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
        notificationService = mock(NotificationService.class);
        vehicle = mock(Vehicle.class);

        when(vehicle.getId()).thenReturn("V001");
        when(vehicle.getStatus()).thenReturn(VehicleStatus.AVAILABLE);
        when(vehicle.getType()).thenReturn("Car");
        when(rentalRepository.findById(anyString())).thenReturn(null);
        when(rentalRepository.hasActiveRentalForVehicle(anyString())).thenReturn(false);
        when(vehicleRepository.findAll()).thenReturn(List.of(vehicle));
    }

	@AfterEach
	void tearDown() throws Exception {
	}
    @Test
    void defaultOverloadShouldRentVehicleWithoutNotificationService() {
        RentalService service = new RentalService(rentalRepository,vehicleRepository);

        Rental rental = service.rentVehicle("R001","V001","Ali","ali@gmail.com",LocalDate.of(2026,7,10),LocalDate.of(2026,7,15));

        assertNotNull(rental);
        assertEquals("R001",rental.getRentalId());
        assertEquals("V001",rental.getVehicleId());

        verify(vehicle).validateRental(18,false);
        verify(rentalRepository).save(rental);
        verify(vehicle).setStatus(VehicleStatus.RENTED);
        verify(vehicleRepository).save(vehicle);
        verifyNoInteractions(notificationService);
    }

    @Test
    void shouldRentVehicleAndSendConfirmationNotification() {
        RentalService service = new RentalService(rentalRepository,vehicleRepository,notificationService);

        Rental rental = service.rentVehicle("R002","V001","Ahmad","ahmad@gmail.com",LocalDate.of(2026,8,1),LocalDate.of(2026,8,5),25,true);

        assertNotNull(rental);
        assertEquals("Ahmad",rental.getCustomerName());
        assertEquals("ahmad@gmail.com",rental.getCustomerEmail());
        assertTrue(rental.isActive());

        verify(vehicle).validateRental(25,true);
        verify(rentalRepository).save(rental);
        verify(vehicle).setStatus(VehicleStatus.RENTED);
        verify(vehicleRepository).save(vehicle);
        verify(notificationService).sendRentalAccepted(same(rental),contains("Rental ID: R002"));
    }

    @Test
    void notificationMessageShouldContainRentalInformation() {
        RentalService service = new RentalService(rentalRepository,vehicleRepository,notificationService);

        Rental rental = service.rentVehicle("R100","V001","Mona","mona@gmail.com",LocalDate.of(2026,9,1),LocalDate.of(2026,9,10),30,false);

        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        verify(notificationService).sendRentalAccepted(same(rental),messageCaptor.capture());

        String message = messageCaptor.getValue();

        assertTrue(message.contains("Hello Mona"));
        assertTrue(message.contains("Rental ID: R100"));
        assertTrue(message.contains("Vehicle ID: V001"));
        assertTrue(message.contains("Vehicle type: Car"));
        assertTrue(message.contains("Start date: 2026-09-01"));
        assertTrue(message.contains("End date: 2026-09-10"));
    }

    @Test
    void shouldFindVehicleAfterSkippingNonMatchingVehicle() {
        Vehicle otherVehicle = mock(Vehicle.class);
        when(otherVehicle.getId()).thenReturn("V999");
        when(vehicleRepository.findAll()).thenReturn(List.of(otherVehicle,vehicle));

        RentalService service = new RentalService(rentalRepository,vehicleRepository);

        Rental rental = service.rentVehicle("R003","V001","Omar","omar@gmail.com",LocalDate.of(2026,7,10),LocalDate.of(2026,7,15));

        assertEquals("V001",rental.getVehicleId());
    }

    @Test
    void shouldRejectDuplicateRentalId() {
        when(rentalRepository.findById("R001")).thenReturn(mock(Rental.class));
        RentalService service = new RentalService(rentalRepository,vehicleRepository);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,() -> service.rentVehicle("R001","V001","Ali","ali@gmail.com",LocalDate.of(2026,7,10),LocalDate.of(2026,7,15)));

        assertEquals("Rental ID already exists",exception.getMessage());
    }

    @Test
    void shouldRejectNullStartDate() {
        RentalService service = new RentalService(rentalRepository,vehicleRepository);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,() -> service.rentVehicle("R001","V001","Ali","ali@gmail.com",null,LocalDate.of(2026,7,15)));

        assertEquals("Rental dates are required",exception.getMessage());
    }

    @Test
    void shouldRejectNullEndDate() {
        RentalService service = new RentalService(rentalRepository,vehicleRepository);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,() -> service.rentVehicle("R001","V001","Ali","ali@gmail.com",LocalDate.of(2026,7,10),null));

        assertEquals("Rental dates are required",exception.getMessage());
    }

    @Test
    void shouldRejectEndDateBeforeStartDate() {
        RentalService service = new RentalService(rentalRepository,vehicleRepository);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,() -> service.rentVehicle("R001","V001","Ali","ali@gmail.com",LocalDate.of(2026,7,15),LocalDate.of(2026,7,10)));

        assertEquals("Rental end date cannot be before start date",exception.getMessage());
    }

    @Test
    void shouldRejectNullCustomerEmail() {
        RentalService service = new RentalService(rentalRepository,vehicleRepository);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,() -> service.rentVehicle("R001","V001","Ali",null,LocalDate.of(2026,7,10),LocalDate.of(2026,7,15)));

        assertEquals("Customer email is required",exception.getMessage());
    }

    @Test
    void shouldRejectBlankCustomerEmail() {
        RentalService service = new RentalService(rentalRepository,vehicleRepository);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,() -> service.rentVehicle("R001","V001","Ali","   ",LocalDate.of(2026,7,10),LocalDate.of(2026,7,15)));

        assertEquals("Customer email is required",exception.getMessage());
    }

    @Test
    void shouldRejectUnknownVehicle() {
        Vehicle otherVehicle = mock(Vehicle.class);
        when(otherVehicle.getId()).thenReturn("V999");
        when(vehicleRepository.findAll()).thenReturn(List.of(otherVehicle));

        RentalService service = new RentalService(rentalRepository,vehicleRepository);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,() -> service.rentVehicle("R001","V001","Ali","ali@gmail.com",LocalDate.of(2026,7,10),LocalDate.of(2026,7,15)));

        assertEquals("Vehicle not found",exception.getMessage());
    }

    @Test
    void shouldRejectVehicleWithRentedStatus() {
        when(vehicle.getStatus()).thenReturn(VehicleStatus.RENTED);
        RentalService service = new RentalService(rentalRepository,vehicleRepository);

        IllegalStateException exception = assertThrows(IllegalStateException.class,() -> service.rentVehicle("R001","V001","Ali","ali@gmail.com",LocalDate.of(2026,7,10),LocalDate.of(2026,7,15)));

        assertEquals("Vehicle is already rented",exception.getMessage());
    }

    @Test
    void shouldRejectVehicleWithActiveRental() {
        when(vehicle.getStatus()).thenReturn(VehicleStatus.AVAILABLE);
        when(rentalRepository.hasActiveRentalForVehicle("V001")).thenReturn(true);

        RentalService service = new RentalService(rentalRepository,vehicleRepository);

        IllegalStateException exception = assertThrows(IllegalStateException.class,() -> service.rentVehicle("R001","V001","Ali","ali@gmail.com",LocalDate.of(2026,7,10),LocalDate.of(2026,7,15)));

        assertEquals("Vehicle is already rented",exception.getMessage());
    }

    @Test
    void getAllRentalsShouldReturnRepositoryRentals() {
        List<Rental> rentals = List.of(mock(Rental.class),mock(Rental.class));
        when(rentalRepository.findAll()).thenReturn(rentals);

        RentalService service = new RentalService(rentalRepository,vehicleRepository);

        assertSame(rentals,service.getAllRentals());
        verify(rentalRepository).findAll();
    }
}
