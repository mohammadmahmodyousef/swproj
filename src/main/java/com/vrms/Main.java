package com.vrms;

import java.nio.file.Paths;
import java.util.Scanner;

import com.vrms.application.AuthService;
import com.vrms.application.VehicleService;
import com.vrms.domain.Manager;
import com.vrms.domain.Vehicle;
import com.vrms.domain.VehicleStatus;
import com.vrms.persistence.FileManagerRepository;
import com.vrms.persistence.FileVehicleRepository;
import com.vrms.persistence.ManagerRepository;
import com.vrms.persistence.VehicleRepository;
import com.vrms.presentation.ManagerLoginController;
import com.vrms.presentation.ManagerLogoutController;
import com.vrms.presentation.VehicleCatalogController;

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

        AuthService authService = new AuthService(managerRepository);
        VehicleService vehicleService = new VehicleService(vehicleRepository);

        ManagerLoginController loginController = new ManagerLoginController(authService);
        ManagerLogoutController logoutController = new ManagerLogoutController(authService);
        VehicleCatalogController catalogController = new VehicleCatalogController(vehicleService, authService);

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

            while (authService.isLoggedIn() && running) {
                System.out.println();
                System.out.println("1. View available vehicles");
                System.out.println("2. Logout");
                System.out.println("3. Exit");
                System.out.print("Enter choice: ");

                String choice = input.nextLine();

                if (choice.equals("1")) {
                    System.out.println(catalogController.viewAvailableVehicles());
                } else if (choice.equals("2")) {
                    System.out.println(logoutController.logout());
                } else if (choice.equals("3")) {
                    running = false;
                } else {
                    System.out.println("Invalid choice");
                }
            }
        }
    }
}