package com.vrms.persistence;

import com.vrms.domain.Manager;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

public class ManagerFileRepository {

    private Path filePath;

    public ManagerFileRepository(Path filePath) {
        this.filePath = filePath;
        createFile();
    }

    private void createFile() {
        try {
            if (filePath.getParent() != null) {
                Files.createDirectories(filePath.getParent());
            }

            if (!Files.exists(filePath)) {
                Files.createFile(filePath);
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not create managers file", e);
        }
    }

    public List<Manager> findAll() {
        List<Manager> managers = new ArrayList<>();

        try {
            List<String> lines = Files.readAllLines(filePath, StandardCharsets.UTF_8);

            for (String line : lines) {
                if (!line.trim().isEmpty()) {
                    managers.add(Manager.fromFileLine(line));
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not read managers file", e);
        }

        return managers;
    }

    public Manager findByUsername(String username) {
        for (Manager manager : findAll()) {
            if (manager.getUsername().equals(username)) {
                return manager;
            }
        }

        return null;
    }

    public void save(Manager manager) {
        if (findByUsername(manager.getUsername()) != null) {
            throw new IllegalArgumentException("Username already exists");
        }

        try (BufferedWriter writer = Files.newBufferedWriter(filePath, StandardCharsets.UTF_8, StandardOpenOption.APPEND)) {
            writer.write(manager.toFileLine());
            writer.newLine();
        } catch (IOException e) {
            throw new RuntimeException("Could not save manager", e);
        }
    }
}