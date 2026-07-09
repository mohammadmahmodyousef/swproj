package com.vrms.presentation;

import static org.junit.jupiter.api.Assertions.*;


import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.nio.file.Path;
import com.vrms.application.AuthService;
import com.vrms.domain.Manager;
import com.vrms.persistence.FileManagerRepository;
import com.vrms.persistence.ManagerRepository;

class ManagerLogoutControllerTest {
    @TempDir
    Path tempDir;

    private AuthService authService;
    private ManagerLogoutController controller;


	
	@BeforeAll
	static void setUpBeforeClass() throws Exception {
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
	}

	 @BeforeEach
	    void setUp() {
	        ManagerRepository repository = new FileManagerRepository(tempDir.resolve("managers.txt"));
	        repository.save(new Manager("admin", "1234"));

	        authService = new AuthService(repository);
	        controller = new ManagerLogoutController(authService);
	    }

    @AfterEach
    void tearDown() throws Exception {
        authService = null;
        controller = null;
    }

    @Test
    void logoutShouldReturnSuccessMessageWhenManagerIsLoggedIn() {
        authService.login("admin", "1234");

        String result = controller.logout();

        assertEquals("Logout successful", result);
    }

    @Test
    void logoutShouldRemoveManagerLoginSession() {
        authService.login("admin", "1234");

        controller.logout();

        assertFalse(authService.isLoggedIn());
    }

    @Test
    void logoutShouldReturnErrorWhenNoManagerIsLoggedIn() {
        String result = controller.logout();

        assertEquals("No manager is logged in", result);
    }

    @Test
    void managerShouldBeAbleToLoginAgainAfterLogout() {
        authService.login("admin", "1234");
        controller.logout();

        boolean result = authService.login("admin", "1234");

        assertTrue(result);
        assertTrue(authService.isLoggedIn());
    }
}
