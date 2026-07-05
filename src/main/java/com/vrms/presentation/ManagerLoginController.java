package com.vrms.presentation;

import com.vrms.application.AuthService;

public class ManagerLoginController {

    private final AuthService authService;

    public ManagerLoginController(AuthService authService) {
        this.authService = authService;
    }

    public boolean usernameExists(String username) {
        return authService.usernameExists(username);
    }

    public String login(String username, String password) {
        if (authService.login(username, password)) {
            return "Login successful";
        }

        return "Incorrect password";
    }
}