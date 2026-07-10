package com.vrms.domain;

import java.time.LocalDate;

public class Rental {

    private String rentalId;
    private String vehicleId;
    private String customerName;
    private LocalDate startDate;
    private LocalDate endDate;
    private boolean active;

    public Rental(String rentalId, String vehicleId, String customerName, LocalDate startDate, LocalDate endDate, boolean active) {
        this.rentalId = rentalId;
        this.vehicleId = vehicleId;
        this.customerName = customerName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.active = active;
    }

    public String getRentalId() {
        return rentalId;
    }

    public String getVehicleId() {
        return vehicleId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String toFileLine() {
        return rentalId + "|" + vehicleId + "|" + customerName + "|" + startDate + "|" + endDate + "|" + active;
    }

    public static Rental fromFileLine(String line) {
        String[] data = line.split("\\|");

        if (data.length != 6) {
            throw new IllegalArgumentException("Invalid rental data");
        }

        return new Rental(data[0], data[1], data[2], LocalDate.parse(data[3]), LocalDate.parse(data[4]), Boolean.parseBoolean(data[5]));
    }

    @Override
    public String toString() {
        return rentalId + " - " + vehicleId + " - " + customerName + " - " + startDate + " - " + endDate;
    }
}