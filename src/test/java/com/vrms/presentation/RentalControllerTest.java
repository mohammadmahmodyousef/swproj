package com.vrms.presentation;

import static org.junit.jupiter.api.Assertions.*;


import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.vrms.application.AuthService;
import com.vrms.application.RentalService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
class RentalControllerTest {
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
    void rentVehicleShouldRequireLogin() {
        when(authService.isLoggedIn()).thenReturn(false);

        String result = controller.rentVehicle("R001","V001","Ali","ali@gmail.com",LocalDate.of(2026,7,10),LocalDate.of(2026,7,15));

        assertEquals("Please login first",result);
        verifyNoInteractions(rentalService);
    }

    @Test
    void defaultOverloadShouldRentVehicleWithDefaultAgeAndLicense() {
        when(authService.isLoggedIn()).thenReturn(true);

        String result = controller.rentVehicle("R001","V001","Ali","ali@gmail.com",LocalDate.of(2026,7,10),LocalDate.of(2026,7,15));

        assertEquals("Vehicle rented successfully",result);
        verify(rentalService).rentVehicle("R001","V001","Ali","ali@gmail.com",LocalDate.of(2026,7,10),LocalDate.of(2026,7,15),18,false);
    }

    @Test
    void fullOverloadShouldRentVehicleSuccessfully() {
        when(authService.isLoggedIn()).thenReturn(true);

        String result = controller.rentVehicle("R002","V002","Ahmad","ahmad@gmail.com",LocalDate.of(2026,8,1),LocalDate.of(2026,8,5),30,true);

        assertEquals("Vehicle rented successfully",result);
        verify(rentalService).rentVehicle("R002","V002","Ahmad","ahmad@gmail.com",LocalDate.of(2026,8,1),LocalDate.of(2026,8,5),30,true);
    }

    @Test
    void rentVehicleShouldReturnIllegalArgumentExceptionMessage() {
        when(authService.isLoggedIn()).thenReturn(true);
        doThrow(new IllegalArgumentException("Vehicle not found")).when(rentalService).rentVehicle(anyString(),anyString(),anyString(),anyString(),any(LocalDate.class),any(LocalDate.class),anyInt(),anyBoolean());

        String result = controller.rentVehicle("R001","UNKNOWN","Ali","ali@gmail.com",LocalDate.of(2026,7,10),LocalDate.of(2026,7,15),20,false);

        assertEquals("Vehicle not found",result);
    }

    @Test
    void rentVehicleShouldReturnIllegalStateExceptionMessage() {
        when(authService.isLoggedIn()).thenReturn(true);
        doThrow(new IllegalStateException("Vehicle is already rented")).when(rentalService).rentVehicle(anyString(),anyString(),anyString(),anyString(),any(LocalDate.class),any(LocalDate.class),anyInt(),anyBoolean());

        String result = controller.rentVehicle("R001","V001","Ali","ali@gmail.com",LocalDate.of(2026,7,10),LocalDate.of(2026,7,15),20,false);

        assertEquals("Vehicle is already rented",result);
    }

}
