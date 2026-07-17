package com.vrms.presentation;

import static org.junit.jupiter.api.Assertions.*;


import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.vrms.application.AuthService;
import com.vrms.application.RentalReturnResult;
import com.vrms.application.RentalReturnService;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RentalReturnControllerTest {
    private RentalReturnService returnService;
    private AuthService authService;
    private RentalReturnController controller;
	@BeforeAll
	static void setUpBeforeClass() throws Exception {
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
	}

    @BeforeEach
    void setUp() {
        returnService = mock(RentalReturnService.class);
        authService = mock(AuthService.class);
        controller = new RentalReturnController(returnService,authService);
    }

	@AfterEach
	void tearDown() throws Exception {
	}


    @Test
    void returnVehicleShouldRequireLogin() {
        when(authService.isLoggedIn()).thenReturn(false);

        String result = controller.returnVehicle("R001",LocalDate.of(2026,7,15));

        assertEquals("Please login first",result);
        verifyNoInteractions(returnService);
    }

    @Test
    void returnVehicleShouldReturnFormattedResult() {
        when(authService.isLoggedIn()).thenReturn(true);

        RentalReturnResult returnResult = new RentalReturnResult("R001","V001",5,2,250.0,40.0,290.0);

        when(returnService.returnVehicle("R001",LocalDate.of(2026,7,17))).thenReturn(returnResult);

        String result = controller.returnVehicle("R001",LocalDate.of(2026,7,17));

        String expected = "Vehicle returned successfully"
                + "\nRental ID: R001"
                + "\nVehicle ID: V001"
                + "\nRental days: 5"
                + "\nLate days: 2"
                + "\nRental cost: $" + String.format("%.2f",250.0)
                + "\nLate penalty: $" + String.format("%.2f",40.0)
                + "\nTotal cost: $" + String.format("%.2f",290.0);

        assertEquals(expected,result);
    }

    @Test
    void returnVehicleShouldReturnIllegalArgumentExceptionMessage() {
        when(authService.isLoggedIn()).thenReturn(true);
        when(returnService.returnVehicle("UNKNOWN",LocalDate.of(2026,7,15))).thenThrow(new IllegalArgumentException("Rental not found"));

        String result = controller.returnVehicle("UNKNOWN",LocalDate.of(2026,7,15));

        assertEquals("Rental not found",result);
    }

    @Test
    void returnVehicleShouldReturnIllegalStateExceptionMessage() {
        when(authService.isLoggedIn()).thenReturn(true);
        when(returnService.returnVehicle("R001",LocalDate.of(2026,7,15))).thenThrow(new IllegalStateException("Rental is already closed"));

        String result = controller.returnVehicle("R001",LocalDate.of(2026,7,15));

        assertEquals("Rental is already closed",result);
    }
}
