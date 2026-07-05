package com.vrms.presentation;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.vrms.application.AuthService;
import com.vrms.domain.Manager;
import com.vrms.persistence.InMemoryManagerRepository;
import com.vrms.persistence.ManagerRepository;

class ManagerLoginControllerTest {

    private ManagerLoginController controller;

    @BeforeAll
    static void setUpBeforeClass() throws Exception {
    }

    @AfterAll
    static void tearDownAfterClass() throws Exception {
    }

    @BeforeEach
    void setUp() {
        ManagerRepository repository = new InMemoryManagerRepository();

        repository.save(new Manager("admin", "1234")   );

        AuthService authService = new AuthService(repository);

        controller =   new ManagerLoginController(authService);
    }

    @AfterEach
    void tearDown() throws Exception {
        controller = null;
    }

    @Test
    void usernameExistsShouldReturnTrueForExistingUsername() {
        assertTrue(controller.usernameExists("admin")  );
    }

    @Test
    void usernameExistsShouldReturnFalseForUnknownUsername() {
        assertFalse( controller.usernameExists("ali") );
    }

    @Test
    void loginShouldReturnSuccessMessageForCorrectPassword() {
        String result = controller.login("admin", "1234");

        assertEquals(  "Login successful",   result);
    }

    @Test
    void loginShouldReturnIncorrectPasswordMessage() {
        String result = controller.login("admin", "5555");

        assertEquals( "Incorrect password",  result );
    }
}