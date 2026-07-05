package com.vrms;

import java.util.Scanner;

import com.vrms.application.AuthService;
import com.vrms.domain.Manager;
import com.vrms.persistence.InMemoryManagerRepository;
import com.vrms.persistence.ManagerRepository;
import com.vrms.presentation.ManagerLoginController;

public class Main {

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);

        ManagerRepository repository = new InMemoryManagerRepository();
        repository.save(new Manager("admin", "1234"));

        AuthService authService = new AuthService(repository);
        ManagerLoginController controller = new ManagerLoginController(authService);

        boolean loginSuccess = false;

        while (!loginSuccess) {
            System.out.print("Enter username: ");
            String username = input.nextLine();

            if (!controller.usernameExists(username)) {
                System.out.println("Username does not exist.");
                System.out.println("Please try again.");
                System.out.println();
                continue;
            }

            System.out.print("Enter password: ");
            String password = input.nextLine();

            String result = controller.login(username, password);
            System.out.println(result);

            if (result.equals("Login successful")) {
                loginSuccess = true;
            } else {
                System.out.println("Please try again.");
                System.out.println();
            }
        }

        System.out.println("Welcome to Vehicle Rental Management System");

        input.close();
    }
}