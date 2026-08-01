package com.vrms.notification;

import com.vrms.domain.Rental;

/**
 * Implementation of NotificationService that handles email notifications via EmailService.
 */
public class EmailNotificationService implements NotificationService {

    private final EmailService emailService;

    public EmailNotificationService(EmailService emailService) {
        this.emailService = emailService;
    }

    @Override
    public void sendRentalAccepted(Rental rental, String message) {
        try {
            emailService.sendEmail(rental.getCustomerEmail(), "Vehicle Rental Confirmation - " + rental.getRentalId(), message);
        } catch (Exception e) {
            System.err.println("[Notification Notice] Could not send email confirmation: " + e.getMessage());
        }
    }

    @Override
    public void sendExpiryReminder(Rental rental, String message) {
        try {
            emailService.sendEmail(rental.getCustomerEmail(), "Rental Expiry Reminder - " + rental.getRentalId(), message);
        } catch (Exception e) {
            System.err.println("[Notification Notice] Could not send expiry reminder: " + e.getMessage());
        }
    }

    @Override
    public void sendRentalExpired(Rental rental, String message) {
        try {
            emailService.sendEmail(rental.getCustomerEmail(), "Rental Period Ended - " + rental.getRentalId(), message);
        } catch (Exception e) {
            System.err.println("[Notification Notice] Could not send expiration notice: " + e.getMessage());
        }
    }

    @Override
    public void sendRentalExtended(Rental rental, String message) {
        try {
            emailService.sendEmail(rental.getCustomerEmail(), "Rental Period Extended - " + rental.getRentalId(), message);
        } catch (Exception e) {
            System.err.println("[Notification Notice] Could not send extension email: " + e.getMessage());
        }
    }

    @Override
    public void sendRentalReturned(Rental rental, String message) {
        try {
            emailService.sendEmail(rental.getCustomerEmail(), "Vehicle Return Confirmation - " + rental.getRentalId(), message);
        } catch (Exception e) {
            System.err.println("[Notification Notice] Could not send return email: " + e.getMessage());
        }
    }
}