package com.vrms.persistence;

import java.util.HashMap;
import java.util.Map;

import com.vrms.domain.Manager;

public class InMemoryManagerRepository implements ManagerRepository {

    private final Map<String, Manager> managers = new HashMap<>();
    
    @Override
    public void save(Manager manager) {
        managers.put(manager.getUsername(), manager);
    }

    @Override
    public Manager findByUsername(String username) {
        return managers.get(username);
    }
}