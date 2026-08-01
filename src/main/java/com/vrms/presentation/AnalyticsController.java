package com.vrms.presentation;

import java.util.Map;

import com.vrms.application.AnalyticsReport;
import com.vrms.application.AuthService;
import com.vrms.application.RentalAnalyticsService;

/**
 * Controller class handling presentation logic for manager analytics and reporting dashboard.
 */
public class AnalyticsController {

    private final RentalAnalyticsService analyticsService;
    private final AuthService authService;

    public AnalyticsController(RentalAnalyticsService analyticsService, AuthService authService) {
        if (analyticsService == null) {
            throw new IllegalArgumentException("RentalAnalyticsService cannot be null");
        }
        if (authService == null) {
            throw new IllegalArgumentException("AuthService cannot be null");
        }

        this.analyticsService = analyticsService;
        this.authService = authService;
    }

    /**
     * Generates a formatted analytics dashboard string for presentation.
     *
     * @return formatted analytics summary or login required message
     */
    public String viewAnalyticsReport() {
        if (!authService.isLoggedIn()) {
            return "Manager must login first";
        }

        AnalyticsReport report = analyticsService.generateReport();

        StringBuilder sb = new StringBuilder();
        sb.append("====================================================\n");
        sb.append("         FLEET ANALYTICS & MANAGER DASHBOARD         \n");
        sb.append("====================================================\n");
        sb.append(String.format("Total Vehicles Fleet     : %d\n", report.getTotalVehicles()));
        sb.append(String.format("  - Available Vehicles   : %d\n", report.getAvailableVehicles()));
        sb.append(String.format("  - Rented Vehicles      : %d\n", report.getRentedVehicles()));
        sb.append(String.format("Fleet Utilization Rate   : %.2f%%\n", report.getUtilizationRate()));
        sb.append("----------------------------------------------------\n");
        sb.append(String.format("Total Rental Records     : %d\n", report.getTotalRentals()));
        sb.append(String.format("  - Active Rentals       : %d\n", report.getActiveRentals()));
        sb.append(String.format("  - Closed Rentals       : %d\n", report.getClosedRentals()));
        sb.append(String.format("Most Popular Vehicle Type: %s\n", report.getMostPopularVehicleType()));
        sb.append("----------------------------------------------------\n");
        sb.append("Rentals Breakdown by Vehicle Type:\n");

        if (report.getRentalsByType().isEmpty()) {
            sb.append("  (No rental history available)\n");
        } else {
            for (Map.Entry<String, Integer> entry : report.getRentalsByType().entrySet()) {
                sb.append(String.format("  - %-18s: %d rental(s)\n", entry.getKey(), entry.getValue()));
            }
        }
        sb.append("====================================================");

        return sb.toString();
    }
}
