package com.vrms.persistence;

import com.vrms.domain.Manager;

public interface ManagerRepository {

    void save(Manager manager);

    Manager findByUsername(String username);
}