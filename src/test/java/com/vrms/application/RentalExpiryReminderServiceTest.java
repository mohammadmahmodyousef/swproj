package com.vrms.application;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.vrms.domain.Rental;
import com.vrms.notification.NotificationService;
import com.vrms.persistence.RentalRepository;

import java.time.LocalDate;
import java.util.List;

class RentalExpiryReminderServiceTest {
	private RentalRepository rentalRepository;
	private NotificationService notificationService;
	private RentalExpiryReminderService reminderService;

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
	}

    @BeforeEach
    void setUp() {
        rentalRepository = mock(RentalRepository.class);
        notificationService = mock(NotificationService.class);
        reminderService = new RentalExpiryReminderService(rentalRepository,notificationService,1);
    }


	@AfterEach
	void tearDown() throws Exception {
	}

    @Test
    void constructorShouldRejectNegativeReminderDays() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,() -> new RentalExpiryReminderService(rentalRepository,notificationService,-1));
        assertEquals("Reminder days cannot be negative",exception.getMessage());
    }

    @Test
    void constructorShouldAcceptZeroReminderDays() {
        assertDoesNotThrow(() -> new RentalExpiryReminderService(rentalRepository,notificationService,0));
    }

    @Test
    void generateExpiryRemindersShouldRejectNullDate() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,() -> reminderService.generateExpiryReminders(null));
        assertEquals("Current date is required",exception.getMessage());
    }

    @Test
    void shouldGenerateExpiryReminderAndSaveUpdatedRental() {
        Rental rental = new Rental("R001","V001","Ali","ali@gmail.com",LocalDate.of(2026,7,10),LocalDate.of(2026,7,13),true);
        when(rentalRepository.findAll()).thenReturn(List.of(rental));

        List<String> reminders = reminderService.generateExpiryReminders(LocalDate.of(2026,7,12));

        assertEquals(1,reminders.size());
        assertTrue(reminders.get(0).contains("Hello Ali"));
        assertTrue(reminders.get(0).contains("R001"));
        assertTrue(reminders.get(0).contains("V001"));
        assertTrue(reminders.get(0).contains("2026-07-13"));
        assertTrue(rental.isExpiryReminderSent());
        verify(notificationService).sendExpiryReminder(same(rental),contains("expires tomorrow on 2026-07-13"));
        verify(rentalRepository).save(rental);
    }

    @Test
    void shouldIgnoreInactiveRental() {
        Rental rental = new Rental("R001","V001","Ali","ali@gmail.com",LocalDate.of(2026,7,10),LocalDate.of(2026,7,13),false);
        when(rentalRepository.findAll()).thenReturn(List.of(rental));

        List<String> reminders = reminderService.generateExpiryReminders(LocalDate.of(2026,7,12));

        assertTrue(reminders.isEmpty());
        verifyNoInteractions(notificationService);
        verify(rentalRepository,never()).save(any(Rental.class));
    }

    @Test
    void shouldIgnoreRentalWhenReminderWasAlreadySent() {
        Rental rental = new Rental("R002","V002","Ahmad","ahmad@gmail.com",LocalDate.of(2026,7,10),LocalDate.of(2026,7,13),true);
        rental.setExpiryReminderSent(true);
        when(rentalRepository.findAll()).thenReturn(List.of(rental));

        List<String> reminders = reminderService.generateExpiryReminders(LocalDate.of(2026,7,12));

        assertTrue(reminders.isEmpty());
        verifyNoInteractions(notificationService);
        verify(rentalRepository,never()).save(any(Rental.class));
    }

    @Test
    void shouldIgnoreRentalWithDifferentExpiryDate() {
        Rental rental = new Rental("R003","V003","Omar","omar@gmail.com",LocalDate.of(2026,7,10),LocalDate.of(2026,7,20),true);
        when(rentalRepository.findAll()).thenReturn(List.of(rental));

        List<String> reminders = reminderService.generateExpiryReminders(LocalDate.of(2026,7,12));

        assertTrue(reminders.isEmpty());
        verifyNoInteractions(notificationService);
        verify(rentalRepository,never()).save(any(Rental.class));
    }

    @Test
    void generateExpirationNotificationsShouldRejectNullDate() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,() -> reminderService.generateExpirationNotifications(null));
        assertEquals("Current date is required",exception.getMessage());
    }

    @Test
    void shouldGenerateExpirationNotificationAndSaveRental() {
        Rental rental = new Rental("R010","V010","Mona","mona@gmail.com",LocalDate.of(2026,7,10),LocalDate.of(2026,7,15),true);
        when(rentalRepository.findAll()).thenReturn(List.of(rental));

        List<String> notifications = reminderService.generateExpirationNotifications(LocalDate.of(2026,7,15));

        assertEquals(1,notifications.size());
        assertTrue(notifications.get(0).contains("Hello Mona"));
        assertTrue(notifications.get(0).contains("R010"));
        assertTrue(notifications.get(0).contains("V010"));
        assertTrue(notifications.get(0).contains("has ended today"));
        assertTrue(rental.isExpirationEmailSent());
        verify(notificationService).sendRentalExpired(same(rental),contains("has ended today on 2026-07-15"));
        verify(rentalRepository).save(rental);
    }

    @Test
    void shouldIgnoreInactiveRentalForExpirationNotification() {
        Rental rental = new Rental("R011","V011","Sami","sami@gmail.com",LocalDate.of(2026,7,10),LocalDate.of(2026,7,15),false);
        when(rentalRepository.findAll()).thenReturn(List.of(rental));

        List<String> notifications = reminderService.generateExpirationNotifications(LocalDate.of(2026,7,15));

        assertTrue(notifications.isEmpty());
        verifyNoInteractions(notificationService);
        verify(rentalRepository,never()).save(any(Rental.class));
    }

    @Test
    void shouldIgnoreRentalWhenExpirationEmailWasAlreadySent() {
        Rental rental = new Rental("R012","V012","Rami","rami@gmail.com",LocalDate.of(2026,7,10),LocalDate.of(2026,7,15),true);
        rental.setExpirationEmailSent(true);
        when(rentalRepository.findAll()).thenReturn(List.of(rental));

        List<String> notifications = reminderService.generateExpirationNotifications(LocalDate.of(2026,7,15));

        assertTrue(notifications.isEmpty());
        verifyNoInteractions(notificationService);
        verify(rentalRepository,never()).save(any(Rental.class));
    }

    @Test
    void shouldIgnoreRentalWithDifferentExpirationDate() {
        Rental rental = new Rental("R013","V013","Lina","lina@gmail.com",LocalDate.of(2026,7,10),LocalDate.of(2026,7,20),true);
        when(rentalRepository.findAll()).thenReturn(List.of(rental));

        List<String> notifications = reminderService.generateExpirationNotifications(LocalDate.of(2026,7,15));

        assertTrue(notifications.isEmpty());
        verifyNoInteractions(notificationService);
        verify(rentalRepository,never()).save(any(Rental.class));
    }

    @Test
    void shouldProcessOnlyRentalsThatRequireNotifications() {
        Rental validRental = new Rental("R020","V020","Nour","nour@gmail.com",LocalDate.of(2026,7,10),LocalDate.of(2026,7,13),true);
        Rental inactiveRental = new Rental("R021","V021","Omar","omar@gmail.com",LocalDate.of(2026,7,10),LocalDate.of(2026,7,13),false);
        Rental differentDateRental = new Rental("R022","V022","Sara","sara@gmail.com",LocalDate.of(2026,7,10),LocalDate.of(2026,7,20),true);
        Rental alreadySentRental = new Rental("R023","V023","Ahmad","ahmad@gmail.com",LocalDate.of(2026,7,10),LocalDate.of(2026,7,13),true);
        alreadySentRental.setExpiryReminderSent(true);

        when(rentalRepository.findAll()).thenReturn(List.of(validRental,inactiveRental,differentDateRental,alreadySentRental));

        List<String> reminders = reminderService.generateExpiryReminders(LocalDate.of(2026,7,12));

        assertEquals(1,reminders.size());
        assertTrue(validRental.isExpiryReminderSent());
        assertFalse(inactiveRental.isExpiryReminderSent());
        assertFalse(differentDateRental.isExpiryReminderSent());
        assertTrue(alreadySentRental.isExpiryReminderSent());
        verify(notificationService,times(1)).sendExpiryReminder(any(Rental.class),anyString());
        verify(rentalRepository,times(1)).save(validRental);
    }
}
