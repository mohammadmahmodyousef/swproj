package com.vrms.presentation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.HashMap;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.vrms.application.AnalyticsReport;
import com.vrms.application.AuthService;
import com.vrms.application.RentalAnalyticsService;

class AnalyticsControllerTest {

    private RentalAnalyticsService analyticsService;
    private AuthService authService;
    private AnalyticsController controller;

    @BeforeEach
    void setUp() {
        analyticsService = mock(RentalAnalyticsService.class);
        authService = mock(AuthService.class);
        controller = new AnalyticsController(analyticsService, authService);
    }

    @Test
    void constructorShouldRejectNullArguments() {
        assertThrows(IllegalArgumentException.class, () -> new AnalyticsController(null, authService));
        assertThrows(IllegalArgumentException.class, () -> new AnalyticsController(analyticsService, null));
    }

    @Test
    void viewAnalyticsReportShouldRequireManagerLogin() {
        when(authService.isLoggedIn()).thenReturn(false);

        String result = controller.viewAnalyticsReport();
        assertEquals("Manager must login first", result);
    }

    @Test
    void viewAnalyticsReportShouldReturnFormattedDashboardWhenLoggedIn() {
        when(authService.isLoggedIn()).thenReturn(true);

        AnalyticsReport mockReport = new AnalyticsReport(
                5, 3, 2, 40.0,
                10, 2, 8, "Car",
                Collections.singletonMap("Car", 10)
        );

        when(analyticsService.generateReport()).thenReturn(mockReport);

        String result = controller.viewAnalyticsReport();

        assertTrue(result.contains("FLEET ANALYTICS & MANAGER DASHBOARD"));
        assertTrue(result.contains("Total Vehicles Fleet     : 5"));
        assertTrue(result.contains("Fleet Utilization Rate   : 40.00%"));
        assertTrue(result.contains("Most Popular Vehicle Type: Car"));
    }

    @Test
    void viewAnalyticsReportShouldHandleEmptyRentalsBreakdown() {
        when(authService.isLoggedIn()).thenReturn(true);

        AnalyticsReport mockReport = new AnalyticsReport(
                0, 0, 0, 0.0,
                0, 0, 0, "None",
                Collections.emptyMap()
        );

        when(analyticsService.generateReport()).thenReturn(mockReport);

        String result = controller.viewAnalyticsReport();

        assertTrue(result.contains("No rental history available"));
    }
}
