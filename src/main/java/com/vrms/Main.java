package com.vrms;

import java.util.Scanner;

import com.vrms.application.AuthService;
import com.vrms.domain.Manager;
import com.vrms.persistence.InMemoryManagerRepository;
import com.vrms.persistence.ManagerRepository;
import com.vrms.presentation.ManagerLoginController;
import com.vrms.presentation.ManagerLogoutController;

public class Main {

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);

        ManagerRepository repository = new InMemoryManagerRepository();
        repository.save(new Manager("admin", "1234"));

        AuthService authService = new AuthService(repository);
        ManagerLoginController loginController = new ManagerLoginController(authService);
        ManagerLogoutController logoutController = new ManagerLogoutController(authService);

        while (true) {
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
            System.out.print("Enter logout to logout or exit to close: ");
            String choice = input.nextLine();

            if (choice.equalsIgnoreCase("logout")) {
                System.out.println(logoutController.logout());
                System.out.println();
            } else if (choice.equalsIgnoreCase("exit")) {
                break;
            } else {
                System.out.println("Invalid choice");
            }
        }

        
    }
}