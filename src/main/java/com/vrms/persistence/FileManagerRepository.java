package com.vrms.persistence;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;

import com.vrms.domain.Manager;

public class FileManagerRepository implements ManagerRepository {

    private final Path filePath;

    public FileManagerRepository(Path filePath) {
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

    @Override
    public void save(Manager manager) {
        if (findByUsername(manager.getUsername()) != null) {
            throw new IllegalArgumentException("Username already exists");
        }

        try (BufferedWriter writer = Files.newBufferedWriter(filePath, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
            writer.write(manager.toFileLine());
            writer.newLine();
        } catch (IOException e) {
            throw new RuntimeException("Could not save manager", e);
        }
    }

    @Override
    public Manager findByUsername(String username) {
        try {
            List<String> lines = Files.readAllLines(filePath, StandardCharsets.UTF_8);

            for (String line : lines) {
                if (!line.trim().isEmpty()) {
                    Manager manager = Manager.fromFileLine(line);

                    if (manager.getUsername().equals(username)) {
                        return manager;
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not read managers file", e);
        }

        return null;
    }
}