package com.vrms.application;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.vrms.domain.Manager;
import com.vrms.persistence.InMemoryManagerRepository;
import com.vrms.persistence.ManagerRepository;

class AuthServiceTest {
	private AuthService authService;
	@BeforeAll
	static void setUpBeforeClass() throws Exception {
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
	}

	@BeforeEach
    void setUp() {
        ManagerRepository repository = new InMemoryManagerRepository();

        repository.save( new Manager("admin", "1234")   );

        authService = new AuthService(repository);
    }


	@AfterEach
	void tearDown() throws Exception {
	}


    @Test
    void usernameExistsShouldReturnTrueWhenUsernameExists() {
        ManagerRepository repository =
                new InMemoryManagerRepository();

        repository.save(new Manager("admin", "1234"));

        AuthService authService =
                new AuthService(repository);

        assertTrue(authService.usernameExists("admin"));
    }

    @Test
    void usernameExistsShouldReturnFalseWhenUsernameDoesNotExist() {
        ManagerRepository repository =
                new InMemoryManagerRepository();

        repository.save(new Manager("admin", "1234"));

        AuthService authService =
                new AuthService(repository);

        assertFalse(authService.usernameExists("ali"));
    }

    @Test
    void loginShouldReturnTrueForCorrectPassword() {
        ManagerRepository repository =
                new InMemoryManagerRepository();

        repository.save(new Manager("admin", "1234"));

        AuthService authService =
                new AuthService(repository);

        assertTrue(authService.login("admin", "1234"));
    }

    @Test
    void loginShouldReturnFalseForIncorrectPassword() {
        ManagerRepository repository =
                new InMemoryManagerRepository();

        repository.save(new Manager("admin", "1234"));

        AuthService authService =
                new AuthService(repository);

        assertFalse(authService.login("admin", "5555"));
    }

    @Test
    void loginShouldReturnFalseForUnknownUsername() {
        ManagerRepository repository =
                new InMemoryManagerRepository();

        repository.save(new Manager("admin", "1234"));

        AuthService authService =
                new AuthService(repository);

        assertFalse(authService.login("ali", "1234"));
    }

}
