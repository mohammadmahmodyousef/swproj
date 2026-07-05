package com.vrms.application;

import com.vrms.domain.Manager;
import com.vrms.persistence.ManagerRepository;

public class AuthService {

    private final ManagerRepository managerRepository;
    private Manager loggedInManager;

    public AuthService(ManagerRepository managerRepository) {
        this.managerRepository = managerRepository;
    }

    public boolean usernameExists(String username) {
        return managerRepository.findByUsername(username) != null;
    }

    public boolean login(String username, String password) {
        Manager manager = managerRepository.findByUsername(username);

        if (manager != null && manager.hasPassword(password)) {
            loggedInManager = manager;
            return true;
        }

        loggedInManager = null;
        return false;
    }
}