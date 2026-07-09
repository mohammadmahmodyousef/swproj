package com.vrms.application;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.vrms.domain.Manager;
import com.vrms.persistence.FileManagerRepository;
import org.junit.jupiter.api.io.TempDir;
import com.vrms.persistence.ManagerRepository;
import java.nio.file.Path;
import java.nio.file.Path;
class AuthServiceTest {
	@TempDir
    Path tempDir;

    private AuthService authService;
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
    }


	@AfterEach
	void tearDown() throws Exception {
	}


	

    @Test
    void usernameExistsShouldReturnTrueWhenUsernameExists() {
        assertTrue(authService.usernameExists("admin"));
    }

    @Test
    void usernameExistsShouldReturnFalseWhenUsernameDoesNotExist() {
        assertFalse(authService.usernameExists("ali"));
    }

    @Test
    void loginShouldReturnTrueForValidCredentials() {
        assertTrue(authService.login("admin", "1234"));
    }

    @Test
    void loginShouldReturnFalseForIncorrectPassword() {
        assertFalse(authService.login("admin", "5555"));
    }

    @Test
    void loginShouldReturnFalseForUnknownUsername() {
        assertFalse(authService.login("ali", "1234"));
    }

    @Test
    void isLoggedInShouldReturnTrueAfterSuccessfulLogin() {
        authService.login("admin", "1234");
        assertTrue(authService.isLoggedIn());
    }

    @Test
    void isLoggedInShouldReturnFalseBeforeLogin() {
        assertFalse(authService.isLoggedIn());
    }

    @Test
    void logoutShouldRemoveLoggedInManager() {
        authService.login("admin", "1234");
        authService.logout();
        assertFalse(authService.isLoggedIn());
    }

    @Test
    void failedLoginShouldNotCreateLoginSession() {
        authService.login("admin", "5555");
        assertFalse(authService.isLoggedIn());
    }

    @Test
    void protectedActionsShouldRequireLoginAgainAfterLogout() {
        authService.login("admin", "1234");
        authService.logout();

        assertFalse(authService.isLoggedIn());

        authService.login("admin", "1234");

        assertTrue(authService.isLoggedIn());
    }
}