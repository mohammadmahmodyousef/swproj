package com.vrms.persistence;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.vrms.domain.Manager;
class InMemoryManagerRepositoryTest {
	private InMemoryManagerRepository repository;
	@BeforeAll
	static void setUpBeforeClass() throws Exception {
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
	}

	@BeforeEach
    void setUp() {
        repository = new InMemoryManagerRepository();
    }

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void test() {
		fail("Not yet implemented");
	}
	@Test
    void saveAndFindByUsernameShouldReturnSavedManager() {
        Manager manager = new Manager("admin", "1234");

        repository.save(manager);

        assertSame(manager, repository.findByUsername("admin"));
    }

    @Test
    void findByUsernameShouldReturnNullWhenManagerDoesNotExist() {
        assertNull(repository.findByUsername("unknown"));
    }

    @Test
    void saveShouldReplaceManagerWithSameUsername() {
        Manager oldManager = new Manager("admin", "oldPassword");
        Manager newManager = new Manager("admin", "newPassword");

        repository.save(oldManager);
        repository.save(newManager);

        assertSame(newManager, repository.findByUsername("admin"));
    }

}
