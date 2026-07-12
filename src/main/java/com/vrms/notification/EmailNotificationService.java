package com.vrms.notification;

import com.vrms.domain.Rental;

public class EmailNotificationService implements NotificationService {

    private final EmailService emailService;

    public EmailNotificationService(EmailService emailService) {
        this.emailService = emailService;
    }

    @Override
    public void sendRentalAccepted(Rental rental, String message) {
        emailService.sendEmail(rental.getCustomerEmail(), "Vehicle Rental Confirmation - " + rental.getRentalId(), message);
    }

    @Override
    public void sendExpiryReminder(Rental rental, String message) {
        emailService.sendEmail(rental.getCustomerEmail(), "Rental Expiry Reminder", message);
    }

    @Override
    public void sendRentalExpired(Rental rental, String message) {
        emailService.sendEmail(rental.getCustomerEmail(), "Rental Period Ended", message);
    }
}