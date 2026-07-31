package com.vrms.application;

import java.time.LocalDate;

public class RentalRequest {
    private final String rentalId;
    private final String vehicleId;
    private final String customerName;
    private final String customerEmail;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final int customerAge;
    private final boolean hasSpecialLicense;

    public RentalRequest(String rentalId, String vehicleId, String customerName, 
                         String customerEmail, LocalDate startDate, LocalDate endDate, 
                         int customerAge, boolean hasSpecialLicense) {
        this.rentalId = rentalId;
        this.vehicleId = vehicleId;
        this.customerName = customerName;
        this.customerEmail = customerEmail;
        this.startDate = startDate;
        this.endDate = endDate;
        this.customerAge = customerAge;
        this.hasSpecialLicense = hasSpecialLicense;
    }

    public String getRentalId() { return rentalId; }
    public String getVehicleId() { return vehicleId; }
    public String getCustomerName() { return customerName; }
    public String getCustomerEmail() { return customerEmail; }
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getEndDate() { return endDate; }
    public int getCustomerAge() { return customerAge; }
    public boolean isHasSpecialLicense() { return hasSpecialLicense; }
}