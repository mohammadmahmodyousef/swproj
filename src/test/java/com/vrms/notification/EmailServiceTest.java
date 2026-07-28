package com.vrms.notification;

import static org.junit.jupiter.api.Assertions.*;


import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Transport;
import org.mockito.MockedStatic;
import java.util.concurrent.atomic.AtomicReference;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
class EmailServiceTest {

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
    void constructorShouldAcceptValidCredentials() {
        assertDoesNotThrow(() -> new EmailService("sender@gmail.com","password"));
    }

    @Test
    void constructorShouldRejectNullUsername() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,() -> new EmailService(null,"password"));
        assertEquals("Email username is required",exception.getMessage());
    }

    @Test
    void constructorShouldRejectBlankUsername() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,() -> new EmailService("   ","password"));
        assertEquals("Email username is required",exception.getMessage());
    }

    @Test
    void constructorShouldRejectNullPassword() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,() -> new EmailService("sender@gmail.com",null));
        assertEquals("Email password is required",exception.getMessage());
    }

    @Test
    void constructorShouldRejectBlankPassword() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,() -> new EmailService("sender@gmail.com","   "));
        assertEquals("Email password is required",exception.getMessage());
    }

    @Test
    void fromEnvironmentShouldCreateEmailService() {
        EmailService emailService = assertDoesNotThrow(EmailService::fromEnvironment);

        assertNotNull(emailService);
    }

    @Test
    void sendEmailShouldRejectNullRecipient() {
        EmailService emailService = new EmailService("sender@gmail.com","password");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,() -> emailService.sendEmail(null,"Subject","Body"));

        assertEquals("Recipient email is required",exception.getMessage());
    }

    @Test
    void sendEmailShouldRejectBlankRecipient() {
        EmailService emailService = new EmailService("sender@gmail.com","password");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,() -> emailService.sendEmail("   ","Subject","Body"));

        assertEquals("Recipient email is required",exception.getMessage());
    }

    @Test
    void sendEmailShouldBuildAndSendCorrectMessage() throws Exception {
        EmailService emailService = new EmailService("sender@gmail.com","password");
        AtomicReference<Message> sentMessage = new AtomicReference<>();

        try (MockedStatic<Transport> transportMock = mockStatic(Transport.class)) {
            transportMock.when(() -> Transport.send(any(Message.class))).thenAnswer(invocation -> {
                sentMessage.set(invocation.getArgument(0));
                return null;
            });

            emailService.sendEmail("receiver@gmail.com","Rental confirmation","Your rental is confirmed");

            transportMock.verify(() -> Transport.send(any(Message.class)),times(1));
        }

        Message message = sentMessage.get();

        assertNotNull(message);
        assertEquals("sender@gmail.com", ((jakarta.mail.internet.InternetAddress) message.getFrom()[0]).getAddress());
        assertEquals("receiver@gmail.com",message.getAllRecipients()[0].toString());
        assertEquals("Rental confirmation",message.getSubject());
        assertEquals("Your rental is confirmed",message.getContent());
    }

    @Test
    void sendEmailShouldWrapMessagingException() {
        EmailService emailService = new EmailService("sender@gmail.com","password");

        try (MockedStatic<Transport> transportMock = mockStatic(Transport.class)) {
            transportMock.when(() -> Transport.send(any(Message.class))).thenThrow(new MessagingException("SMTP failure"));

            RuntimeException exception = assertThrows(RuntimeException.class,() -> emailService.sendEmail("receiver@gmail.com","Subject","Body"));

            assertEquals("Failed to send email",exception.getMessage());
            assertInstanceOf(MessagingException.class,exception.getCause());
        }
    }
}
