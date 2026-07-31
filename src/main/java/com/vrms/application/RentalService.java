package com.vrms.application;

import java.time.LocalDate;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import com.vrms.domain.Rental;

import com.vrms.domain.Vehicle;
import com.vrms.domain.VehicleStatus;
import com.vrms.notification.NotificationService;
import com.vrms.persistence.RentalRepository;
import com.vrms.persistence.VehicleRepository;

public class RentalService {

    private static final long MAX_RENTAL_DAYS = 30;

    private final RentalRepository rentalRepository;
    private final VehicleRepository vehicleRepository;
    // قائمة المستمعين (Observers)
    private final List<NotificationService> notificationListeners = new ArrayList<>();

    public RentalService(RentalRepository rentalRepository, VehicleRepository vehicleRepository) {
        this(rentalRepository, vehicleRepository, null);
    }

    public RentalService(RentalRepository rentalRepository, VehicleRepository vehicleRepository, NotificationService notificationService) {
        this.rentalRepository = rentalRepository;
        this.vehicleRepository = vehicleRepository;
        if (notificationService != null) {
            this.notificationListeners.add(notificationService);
        }
    }

    // دالة إضافة مستمع جديد (Subscribe)
    public void addNotificationListener(NotificationService listener) {
        if (listener != null) {
            this.notificationListeners.add(listener);
        }
    }

    // دالة حذف مستمع (Unsubscribe)
    public void removeNotificationListener(NotificationService listener) {
        this.notificationListeners.remove(listener);
    }

    public Rental rentVehicle(String rentalId, String vehicleId, String customerName, String customerEmail, LocalDate startDate, LocalDate endDate) {
        return rentVehicle(new RentalRequest(rentalId, vehicleId, customerName, customerEmail, startDate, endDate, 18, false));
    }

    public Rental rentVehicle(String rentalId, String vehicleId, String customerName, String customerEmail, LocalDate startDate, LocalDate endDate, int customerAge, boolean hasSpecialLicense) {
        return rentVehicle(new RentalRequest(rentalId, vehicleId, customerName, customerEmail, startDate, endDate, customerAge, hasSpecialLicense));
    }

    public Rental rentVehicle(RentalRequest request) {
        if (rentalRepository.findById(request.getRentalId()) != null) {
            throw new IllegalArgumentException("Rental ID already exists");
        }

        validateRentalPeriod(request.getStartDate(), request.getEndDate());

        if (request.getCustomerEmail() == null || request.getCustomerEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Customer email is required");
        }

        Vehicle vehicle = findVehicleById(request.getVehicleId());

        if (vehicle == null) {
            throw new IllegalArgumentException("Vehicle not found");
        }

        if (vehicle.getStatus() != VehicleStatus.AVAILABLE || rentalRepository.hasActiveRentalForVehicle(request.getVehicleId())) {
            throw new IllegalStateException("Vehicle is already rented");
        }

        vehicle.validateRental(request.getCustomerAge(), request.isHasSpecialLicense());

        Rental rental = new Rental(
                request.getRentalId(),
                request.getVehicleId(),
                request.getCustomerName(),
                request.getCustomerEmail(),
                request.getStartDate(),
                request.getEndDate(),
                true
        );
        rentalRepository.save(rental);

        vehicle.setStatus(VehicleStatus.RENTED);
        vehicleRepository.save(vehicle);

        // تجهيز نص الإشعار
        String message = "Hello " + request.getCustomerName()
                + ",\n\nYour rental has been successfully confirmed."
                + "\nRental ID: " + request.getRentalId()
                + "\nVehicle ID: " + request.getVehicleId()
                + "\nVehicle type: " + vehicle.getType()
                + "\nStart date: " + request.getStartDate()
                + "\nEnd date: " + request.getEndDate()
                + "\n\nThank you for using VRMS.";

        // إطلاق الحدث لجميع المستمعين (Notify Observers)
        for (NotificationService listener : notificationListeners) {
            listener.sendRentalAccepted(rental, message);
        }

        return rental;
    }

    public List<Rental> getAllRentals() {
        return rentalRepository.findAll();
    }

    public List<Rental> getActiveRentals() {
        List<Rental> activeRentals = new ArrayList<>();

        for (Rental rental : rentalRepository.findAll()) {
            if (rental.isActive()) {
                activeRentals.add(rental);
            }
        }

        return activeRentals;
    }

    public Rental extendRental(String rentalId, LocalDate newEndDate) {
        if (rentalId == null || rentalId.trim().isEmpty()) {
            throw new IllegalArgumentException("Rental ID is required");
        }

        if (newEndDate == null) {
            throw new IllegalArgumentException("New end date is required");
        }

        Rental rental = rentalRepository.findById(rentalId);

        if (rental == null) {
            throw new IllegalArgumentException("Rental not found");
        }

        if (!rental.isActive()) {
            throw new IllegalStateException("Only active rentals can be extended");
        }

        LocalDate previousEndDate = rental.getEndDate();

        if (!newEndDate.isAfter(previousEndDate)) {
            throw new IllegalArgumentException("New end date must be after current end date");
        }

        long totalRentalDays = ChronoUnit.DAYS.between(rental.getStartDate(), newEndDate);

        if (totalRentalDays > MAX_RENTAL_DAYS) {
            throw new IllegalArgumentException("Rental period cannot exceed 30 days");
        }

        rental.setEndDate(newEndDate);
        rental.setExpiryReminderSent(false);
        rental.setExpirationEmailSent(false);
        rentalRepository.save(rental);

        String message = "Hello " + rental.getCustomerName()
                + ",\n\nYour vehicle rental period has been successfully extended."
                + "\nRental ID: " + rental.getRentalId()
                + "\nVehicle ID: " + rental.getVehicleId()
                + "\nPrevious end date: " + previousEndDate
                + "\nNew end date: " + newEndDate
                + "\n\nThank you for using VRMS.";

        // إطلاق الحدث لجميع المستمعين عند التمديد (Notify Observers)
        for (NotificationService listener : notificationListeners) {
            listener.sendRentalExtended(rental, message);
        }

        return rental;
    }

    private void validateRentalPeriod(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("Rental dates are required");
        }

        if (endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("Rental end date cannot be before start date");
        }

        long rentalDays = ChronoUnit.DAYS.between(startDate, endDate);

        if (rentalDays > MAX_RENTAL_DAYS) {
            throw new IllegalArgumentException("Rental period cannot exceed 30 days");
        }
    }

    private Vehicle findVehicleById(String vehicleId) {
        for (Vehicle vehicle : vehicleRepository.findAll()) {
            if (vehicle.getId().equals(vehicleId)) {
                return vehicle;
            }
        }

        return null;
    }
}