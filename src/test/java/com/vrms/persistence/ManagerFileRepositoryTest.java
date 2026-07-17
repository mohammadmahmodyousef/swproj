package com.vrms.persistence;

import static org.junit.jupiter.api.Assertions.*;


import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.vrms.domain.Manager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
class ManagerFileRepositoryTest {
    @TempDir
    Path tempDir;

    private Path filePath;
    private ManagerFileRepository repository;
	@BeforeAll
	static void setUpBeforeClass() throws Exception {
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
	}

    @BeforeEach
    void setUp() {
        filePath = tempDir.resolve("nested/data/managers.txt");
        repository = new ManagerFileRepository(filePath);
    }
	@AfterEach
	void tearDown() throws Exception {
	}

    @Test
    void constructorShouldCreateFileAndParentDirectories() {
        assertTrue(Files.exists(filePath));
        assertTrue(Files.exists(filePath.getParent()));
    }

    @Test
    void saveAndFindByUsernameShouldReturnManager() {
        repository.save(new Manager("admin","1234"));

        Manager manager = repository.findByUsername("admin");

        assertNotNull(manager);
        assertEquals("admin",manager.getUsername());
        assertEquals("1234",manager.getPassword());
        assertTrue(manager.hasPassword("1234"));
    }

    @Test
    void findAllShouldReturnManagersAndIgnoreBlankLines() throws Exception {
        Files.writeString(filePath,"\nadmin|1234\n\nali|5678\n",StandardCharsets.UTF_8);

        List<Manager> managers = repository.findAll();

        assertEquals(2,managers.size());
        assertEquals("admin",managers.get(0).getUsername());
        assertEquals("ali",managers.get(1).getUsername());
    }

    @Test
    void findByUsernameShouldSkipNonMatchingManager() {
        repository.save(new Manager("admin","1234"));
        repository.save(new Manager("ali","5678"));

        Manager manager = repository.findByUsername("ali");

        assertNotNull(manager);
        assertEquals("ali",manager.getUsername());
    }

    @Test
    void findByUsernameShouldReturnNullWhenManagerDoesNotExist() {
        repository.save(new Manager("admin","1234"));

        assertNull(repository.findByUsername("unknown"));
    }

    @Test
    void saveShouldRejectDuplicateUsername() {
        repository.save(new Manager("admin","1234"));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,() -> repository.save(new Manager("admin","5678")));

        assertEquals("Username already exists",exception.getMessage());
    }
}
