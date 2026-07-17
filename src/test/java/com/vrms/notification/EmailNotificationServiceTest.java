package com.vrms.notification;

import com.vrms.domain.Rental;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.mockito.Mockito.*;

class EmailNotificationServiceTest {

    private EmailService emailService;
    private EmailNotificationService notificationService;
    private Rental rental;

    @BeforeEach
    void setUp() {
        emailService = mock(EmailService.class);
        notificationService = new EmailNotificationService(emailService);
        rental = new Rental("R001","V001","Ali","ali@gmail.com",LocalDate.of(2026,7,10),LocalDate.of(2026,7,15),true);
    }

    @Test
    void sendRentalAcceptedShouldSendConfirmationEmail() {
        notificationService.sendRentalAccepted(rental,"Rental accepted message");

        verify(emailService).sendEmail("ali@gmail.com","Vehicle Rental Confirmation - R001","Rental accepted message");
    }

    @Test
    void sendExpiryReminderShouldSendReminderEmail() {
        notificationService.sendExpiryReminder(rental,"Expiry reminder message");

        verify(emailService).sendEmail("ali@gmail.com","Rental Expiry Reminder - R001","Expiry reminder message");
    }

    @Test
    void sendRentalExpiredShouldSendExpirationEmail() {
        notificationService.sendRentalExpired(rental,"Rental expired message");

        verify(emailService).sendEmail("ali@gmail.com","Rental Period Ended - R001","Rental expired message");
    }

    @Test
    void sendRentalExtendedShouldSendExtensionEmail() {
        notificationService.sendRentalExtended(rental,"Rental extended message");

        verify(emailService).sendEmail("ali@gmail.com","Rental Period Extended - R001","Rental extended message");
    }

    @Test
    void sendRentalReturnedShouldSendReturnEmail() {
        notificationService.sendRentalReturned(rental,"Vehicle returned message");

        verify(emailService).sendEmail("ali@gmail.com","Vehicle Return Confirmation - R001","Vehicle returned message");
    }
}