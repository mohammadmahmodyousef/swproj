package com.vrms.persistence;

import static org.junit.jupiter.api.Assertions.*;
import java.nio.file.Path;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.vrms.domain.Manager;
class FileManagerRepositoryTest {
	@TempDir
    Path tempDir;

    private ManagerRepository repository;

    
	@BeforeAll
	static void setUpBeforeClass() throws Exception {
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
	}

	@BeforeEach
    void setUp() {
        repository = new FileManagerRepository(tempDir.resolve("managers.txt"));
    }

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
    void saveAndFindByUsernameShouldReturnSavedManager() {
        Manager manager = new Manager("admin", "1234");

        repository.save(manager);

        Manager savedManager = repository.findByUsername("admin");

        assertNotNull(savedManager);
        assertEquals("admin", savedManager.getUsername());
        assertTrue(savedManager.hasPassword("1234"));
    }

    @Test
    void findByUsernameShouldReturnNullWhenManagerDoesNotExist() {
        assertNull(repository.findByUsername("unknown"));
    }

    @Test
    void saveShouldRejectDuplicateUsername() {
        repository.save(new Manager("admin", "1234"));

        assertThrows(IllegalArgumentException.class, () -> {
            repository.save(new Manager("admin", "5678"));
        });
    }

}
