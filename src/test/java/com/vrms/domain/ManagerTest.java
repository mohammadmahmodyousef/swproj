package com.vrms.domain;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ManagerTest {

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
	}

	@BeforeEach
	void setUp() throws Exception {
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void getUsernameShouldReturnManagerUsername() {
	        Manager manager = new Manager("admin", "1234");

	        assertEquals("admin", manager.getUsername());
    }

	@Test
	void hasPasswordShouldReturnTrueForCorrectPassword() {
	        Manager manager = new Manager("admin", "1234");

	        assertTrue(manager.hasPassword("1234"));
	}

    @Test
	void hasPasswordShouldReturnFalseForIncorrectPassword() {
	       Manager manager = new Manager("admin", "1234");

	        assertFalse(manager.hasPassword("wrong"));
	}

	@Test
	void hasPasswordShouldReturnFalseForNullPassword() {
	        Manager manager = new Manager("admin", "1234");

	        assertFalse(manager.hasPassword(null));
	}

}
