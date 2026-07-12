package com.vrms;

import java.time.LocalDate;

import java.time.format.DateTimeParseException;
import java.nio.file.Paths;
import java.util.Scanner;

import com.vrms.application.AuthService;
import com.vrms.application.RentalService;
import com.vrms.application.VehicleService;
import com.vrms.domain.Manager;
import com.vrms.domain.Vehicle;
import com.vrms.domain.VehicleStatus;
import com.vrms.persistence.FileManagerRepository;
import com.vrms.persistence.FileRentalRepository;
import com.vrms.persistence.FileVehicleRepository;
import com.vrms.persistence.ManagerRepository;
import com.vrms.persistence.RentalRepository;
import com.vrms.persistence.VehicleRepository;
import com.vrms.presentation.ManagerLoginController;
import com.vrms.presentation.ManagerLogoutController;
import com.vrms.presentation.RentalController;
import com.vrms.presentation.VehicleCatalogController;
import java.util.List;
import com.vrms.application.RentalExpiryReminderService;
import com.vrms.notification.EmailNotificationService;
import com.vrms.notification.EmailService;
import com.vrms.notification.NotificationService;

public class Main {

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);

        ManagerRepository managerRepository = new FileManagerRepository(Paths.get("data", "managers.txt"));

        if (managerRepository.findByUsername("admin") == null) {
            managerRepository.save(new Manager("admin", "1234"));
        }

        VehicleRepository vehicleRepository = new FileVehicleRepository(Paths.get("data", "vehicles.txt"));

        if (vehicleRepository.findAll().isEmpty()) {
            vehicleRepository.save(new Vehicle("V001", "Toyota Corolla", "Car", VehicleStatus.AVAILABLE));
            vehicleRepository.save(new Vehicle("V002", "BMW X5", "Car", VehicleStatus.RENTED));
            vehicleRepository.save(new Vehicle("V003", "Ford Transit", "Van", VehicleStatus.AVAILABLE));
        }

        RentalRepository rentalRepository = new FileRentalRepository(Paths.get("data", "rentals.txt"));
        EmailService emailService = EmailService.fromEnvironment();
        NotificationService notificationService = new EmailNotificationService(emailService);

        AuthService authService = new AuthService(managerRepository);
        VehicleService vehicleService = new VehicleService(vehicleRepository);
        RentalService rentalService = new RentalService(rentalRepository, vehicleRepository, notificationService);
        RentalExpiryReminderService reminderService =new RentalExpiryReminderService(rentalRepository, notificationService, 1);
        
        ManagerLoginController loginController = new ManagerLoginController(authService);
        ManagerLogoutController logoutController = new ManagerLogoutController(authService);
        VehicleCatalogController catalogController = new VehicleCatalogController(vehicleService, authService);
        RentalController rentalController = new RentalController(rentalService, authService);

        boolean running = true;

        while (running) {
            while (!authService.isLoggedIn()) {
                System.out.print("Enter username: ");
                String username = input.nextLine();

                if (!loginController.usernameExists(username)) {
                    System.out.println("Username does not exist.");
                    System.out.println("Please try again.");
                    System.out.println();
                    continue;
                }

                System.out.print("Enter password: ");
                String password = input.nextLine();

                String result = loginController.login(username, password);
                System.out.println(result);

                if (!authService.isLoggedIn()) {
                    System.out.println("Please try again.");
                    System.out.println();
                }
            }

            System.out.println("Welcome to Vehicle Rental Management System");
            
            reminderService.generateExpiryReminders(LocalDate.now());
            reminderService.generateExpirationNotifications(LocalDate.now());
            while (authService.isLoggedIn() && running) {
                System.out.println();
                System.out.println("1. View available vehicles");
                System.out.println("2. Rent a vehicle");
                System.out.println("3. Generate rental notifications");
                System.out.println("4. Logout");
                System.out.println("5. Exit");
                System.out.print("Enter choice: ");

                String choice = input.nextLine();

                if (choice.equals("1")) {
                    System.out.println(catalogController.viewAvailableVehicles());
                } else if (choice.equals("2")) {
                    System.out.print("Enter rental ID: ");
                    String rentalId = input.nextLine();

                    System.out.print("Enter vehicle ID: ");
                    String vehicleId = input.nextLine();

                    System.out.print("Enter customer name: ");
                    String customerName = input.nextLine();
                    
                    System.out.print("Enter customer email: ");
                    String customerEmail = input.nextLine();
                    try {
                        System.out.print("Enter start date (yyyy-MM-dd): ");
                        LocalDate startDate = LocalDate.parse(input.nextLine());

                        System.out.print("Enter end date (yyyy-MM-dd): ");
                        LocalDate endDate = LocalDate.parse(input.nextLine());

                        String result = rentalController.rentVehicle(rentalId, vehicleId, customerName, customerEmail, startDate, endDate);
                        System.out.println(result);
                    } catch (DateTimeParseException e) {
                        System.out.println("Invalid date format");
                    }
                    
                }
                else if (choice.equals("3")) {
                List<String> reminders = reminderService.generateExpiryReminders(LocalDate.now());
                List<String> expirationNotifications = reminderService.generateExpirationNotifications(LocalDate.now());

                if (reminders.isEmpty() && expirationNotifications.isEmpty()) {
                    System.out.println("No rental notifications found");
                } else {
                    System.out.println(reminders.size() + " expiry reminder(s) generated");
                    System.out.println(expirationNotifications.size() + " expiration notification(s) generated");
                  }
                }
                else if (choice.equals("4")) {
                    System.out.println(logoutController.logout());
                } else if (choice.equals("5")) {
                    running = false;
                } else {
                    System.out.println("Invalid choice");
                }
            }
        }
    }
}