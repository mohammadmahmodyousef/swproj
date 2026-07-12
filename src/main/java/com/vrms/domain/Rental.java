package com.vrms.domain;

import java.time.LocalDate;

public class Rental {

    private String rentalId;
    private String vehicleId;
    private String customerName;
    private LocalDate startDate;
    private LocalDate endDate;
    private boolean active;
    private String customerEmail;
    private boolean expiryReminderSent;
    private boolean expirationEmailSent;
    public Rental(String rentalId, String vehicleId, String customerName, String customerEmail, LocalDate startDate, LocalDate endDate, boolean active, boolean expiryReminderSent, boolean expirationEmailSent) {
        this.rentalId = rentalId;
        this.vehicleId = vehicleId;
        this.customerName = customerName;
        this.customerEmail = customerEmail;
        this.startDate = startDate;
        this.endDate = endDate;
        this.active = active;
        this.expiryReminderSent = expiryReminderSent;
        this.expirationEmailSent = expirationEmailSent;
    }
    public Rental(String rentalId, String vehicleId, String customerName, String customerEmail, LocalDate startDate, LocalDate endDate, boolean active) {
        this(rentalId, vehicleId, customerName, customerEmail, startDate, endDate, active, false, false);
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
    
    public String getCustomerEmail() {
        return customerEmail;
    }
    public boolean isExpiryReminderSent() {
        return expiryReminderSent;
    }

    public void setExpiryReminderSent(boolean expiryReminderSent) {
        this.expiryReminderSent = expiryReminderSent;
    }

    public boolean isExpirationEmailSent() {
        return expirationEmailSent;
    }

    public void setExpirationEmailSent(boolean expirationEmailSent) {
        this.expirationEmailSent = expirationEmailSent;
    }
    public String toFileLine() {
        return rentalId + "|" + vehicleId + "|" + customerName + "|" + customerEmail + "|" + startDate + "|" + endDate + "|" + active + "|" + expiryReminderSent + "|" + expirationEmailSent;
    }

    public static Rental fromFileLine(String line) {
        String[] data = line.split("\\|", -1);

        if (data.length == 7) {
        	return new Rental(data[0], data[1], data[2], data[3], LocalDate.parse(data[4]), LocalDate.parse(data[5]), Boolean.parseBoolean(data[6]), false, false);
        }

        if (data.length == 9) {
            return new Rental(data[0], data[1], data[2], data[3], LocalDate.parse(data[4]), LocalDate.parse(data[5]), Boolean.parseBoolean(data[6]), Boolean.parseBoolean(data[7]), Boolean.parseBoolean(data[8]));
        }

        throw new IllegalArgumentException("Invalid rental data");
    }

    @Override
    public String toString() {
        return rentalId + " - " + vehicleId + " - " + customerName + " - " + startDate + " - " + endDate;
    }
}