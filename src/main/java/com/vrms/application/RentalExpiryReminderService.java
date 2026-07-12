package com.vrms.application;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.vrms.domain.Rental;
import com.vrms.notification.NotificationService;
import com.vrms.persistence.RentalRepository;

public class RentalExpiryReminderService {

    private final RentalRepository rentalRepository;
    private final NotificationService notificationService;
    private final int daysBeforeExpiry;

    public RentalExpiryReminderService(RentalRepository rentalRepository, NotificationService notificationService, int daysBeforeExpiry) {
        if (daysBeforeExpiry < 0) {
            throw new IllegalArgumentException("Reminder days cannot be negative");
        }

        this.rentalRepository = rentalRepository;
        this.notificationService = notificationService;
        this.daysBeforeExpiry = daysBeforeExpiry;
    }

    public List<String> generateExpiryReminders(LocalDate currentDate) {
        if (currentDate == null) {
            throw new IllegalArgumentException("Current date is required");
        }

        List<String> reminders = new ArrayList<>();
        LocalDate reminderDate = currentDate.plusDays(daysBeforeExpiry);

        for (Rental rental : rentalRepository.findAll()) {
            if (rental.isActive() && !rental.isExpiryReminderSent() && rental.getEndDate().equals(reminderDate)) {
                String message = "Hello " + rental.getCustomerName() + ", your rental " + rental.getRentalId() + " for vehicle " + rental.getVehicleId() + " expires tomorrow on " + rental.getEndDate() + ".";
                notificationService.sendExpiryReminder(rental, message);
                rental.setExpiryReminderSent(true);
                rentalRepository.save(rental);
                reminders.add(message);
            }
        }

        return reminders;
    }

    public List<String> generateExpirationNotifications(LocalDate currentDate) {
        if (currentDate == null) {
            throw new IllegalArgumentException("Current date is required");
        }

        List<String> notifications = new ArrayList<>();

        for (Rental rental : rentalRepository.findAll()) {
            if (rental.isActive() && !rental.isExpirationEmailSent() && rental.getEndDate().equals(currentDate)) {
                String message = "Hello " + rental.getCustomerName() + ", your rental " + rental.getRentalId() + " for vehicle " + rental.getVehicleId() + " has ended today on " + rental.getEndDate() + ".";
                notificationService.sendRentalExpired(rental, message);
                rental.setExpirationEmailSent(true);
                rentalRepository.save(rental);
                notifications.add(message);
            }
        }

        return notifications;
    }
}