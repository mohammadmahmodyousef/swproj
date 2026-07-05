package com.vrms.presentation;

import com.vrms.application.AuthService;

public class ManagerLogoutController {

    private final AuthService authService;

    public ManagerLogoutController(AuthService authService) {
        this.authService = authService;
    }

    public String logout() {
        if (!authService.isLoggedIn()) {
            return "No manager is logged in";
        }

        authService.logout();
        return "Logout successful";
    }
}