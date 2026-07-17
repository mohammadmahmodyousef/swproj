package com.vrms.presentation;

import static org.junit.jupiter.api.Assertions.*;


import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.vrms.application.AuthService;
import com.vrms.application.RentalService;
import com.vrms.domain.Rental;
import java.time.LocalDate;
import java.util.List;
import static org.mockito.Mockito.*;
class RentalControllerFeaturesTest {
    private RentalService rentalService;
    private AuthService authService;
    private RentalController controller;


	@BeforeAll
	static void setUpBeforeClass() throws Exception {
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
	}

    @BeforeEach
    void setUp() {
        rentalService = mock(RentalService.class);
        authService = mock(AuthService.class);
        controller = new RentalController(rentalService,authService);
    }

	@AfterEach
	void tearDown() throws Exception {
	}

    @Test
    void viewActiveRentalsShouldRequireLogin() {
        when(authService.isLoggedIn()).thenReturn(false);

        String result = controller.viewActiveRentals();

        assertEquals("Please login first",result);
        verifyNoInteractions(rentalService);
    }

    @Test
    void viewActiveRentalsShouldReturnNoActiveRentalsMessage() {
        when(authService.isLoggedIn()).thenReturn(true);
        when(rentalService.getActiveRentals()).thenReturn(List.of());

        String result = controller.viewActiveRentals();

        assertEquals("No active rentals",result);
    }

    @Test
    void viewActiveRentalsShouldDisplayRentalInformation() {
        Rental rental = new Rental("R001","V001","Ali","ali@gmail.com",LocalDate.of(2026,7,1),LocalDate.of(2026,7,10),true);

        when(authService.isLoggedIn()).thenReturn(true);
        when(rentalService.getActiveRentals()).thenReturn(List.of(rental));

        String result = controller.viewActiveRentals();

        assertTrue(result.contains("Active rentals:"));
        assertTrue(result.contains("Rental ID: R001"));
        assertTrue(result.contains("Customer name: Ali"));
        assertTrue(result.contains("Vehicle ID: V001"));
        assertTrue(result.contains("Start date: 2026-07-01"));
        assertTrue(result.contains("End date: 2026-07-10"));
    }

    @Test
    void extendRentalShouldRequireLogin() {
        when(authService.isLoggedIn()).thenReturn(false);

        String result = controller.extendRental("R001",LocalDate.of(2026,7,20));

        assertEquals("Please login first",result);
        verifyNoInteractions(rentalService);
    }

    @Test
    void extendRentalShouldReturnSuccessfulMessage() {
        Rental rental = new Rental("R001","V001","Ali","ali@gmail.com",LocalDate.of(2026,7,1),LocalDate.of(2026,7,20),true);

        when(authService.isLoggedIn()).thenReturn(true);
        when(rentalService.extendRental("R001",LocalDate.of(2026,7,20))).thenReturn(rental);

        String result = controller.extendRental("R001",LocalDate.of(2026,7,20));

        assertEquals("Rental extended successfully\nRental ID: R001\nNew end date: 2026-07-20",result);
        verify(rentalService).extendRental("R001",LocalDate.of(2026,7,20));
    }

    @Test
    void extendRentalShouldReturnValidationMessage() {
        when(authService.isLoggedIn()).thenReturn(true);
        when(rentalService.extendRental("R001",LocalDate.of(2026,8,1))).thenThrow(new IllegalArgumentException("Rental period cannot exceed 30 days"));

        String result = controller.extendRental("R001",LocalDate.of(2026,8,1));

        assertEquals("Rental period cannot exceed 30 days",result);
    }

    @Test
    void extendRentalShouldReturnInactiveRentalMessage() {
        when(authService.isLoggedIn()).thenReturn(true);
        when(rentalService.extendRental("R001",LocalDate.of(2026,7,20))).thenThrow(new IllegalStateException("Only active rentals can be extended"));

        String result = controller.extendRental("R001",LocalDate.of(2026,7,20));

        assertEquals("Only active rentals can be extended",result);
    }
}
