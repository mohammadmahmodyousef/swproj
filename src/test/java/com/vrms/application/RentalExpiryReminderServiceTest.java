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
import com.vrms.persistence.FileRentalRepository;
import com.vrms.persistence.RentalRepository;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.io.TempDir;
class RentalExpiryReminderServiceTest {
    @TempDir
	Path tempDir;
    
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
	      rentalRepository = new FileRentalRepository(tempDir.resolve("rentals.txt"));
	      notificationService = mock(NotificationService.class);
	      reminderService = new RentalExpiryReminderService(rentalRepository, notificationService, 1);
	    }


	@AfterEach
	void tearDown() throws Exception {
	}


    @Test
    void generateExpiryRemindersShouldGenerateReminderForExpiringRental() {
        Rental rental = new Rental("R001", "V001", "Ali", "ali@gmail.com", LocalDate.of(2026, 7, 10), LocalDate.of(2026, 7, 13), true);
        rentalRepository.save(rental);

        List<String> reminders = reminderService.generateExpiryReminders(LocalDate.of(2026, 7, 12));

        assertEquals(1, reminders.size());
        assertTrue(reminders.get(0).contains("R001"));
        assertTrue(reminders.get(0).contains("V001"));
        assertTrue(reminders.get(0).contains("2026-07-13"));
    }

    @Test
    void generateExpiryRemindersShouldUseMockNotificationService() {
        Rental rental = new Rental("R001", "V001", "Ali", "ali@gmail.com", LocalDate.of(2026, 7, 10), LocalDate.of(2026, 7, 13), true);
        rentalRepository.save(rental);

        reminderService.generateExpiryReminders(LocalDate.of(2026, 7, 12));

        verify(notificationService, times(1)).sendExpiryReminder(any(Rental.class), contains("expires on 2026-07-13"));
    }

    @Test
    void generateExpiryRemindersShouldIgnoreRentalsNotExpiringSoon() {
        Rental rental = new Rental("R001", "V001", "Ali", "ali@gmail.com", LocalDate.of(2026, 7, 10), LocalDate.of(2026, 7, 20), true);
        rentalRepository.save(rental);

        List<String> reminders = reminderService.generateExpiryReminders(LocalDate.of(2026, 7, 12));

        assertTrue(reminders.isEmpty());
        verifyNoInteractions(notificationService);
    }

    @Test
    void generateExpiryRemindersShouldIgnoreInactiveRentals() {
        Rental rental = new Rental("R001", "V001", "Ali", "ali@gmail.com", LocalDate.of(2026, 7, 10), LocalDate.of(2026, 7, 13), false);
        rentalRepository.save(rental);

        List<String> reminders = reminderService.generateExpiryReminders(LocalDate.of(2026, 7, 12));

        assertTrue(reminders.isEmpty());
        verifyNoInteractions(notificationService);
    }

    @Test
    void constructorShouldRejectNegativeReminderDays() {
        assertThrows(IllegalArgumentException.class, () -> new RentalExpiryReminderService(rentalRepository, notificationService, -1));
    }
}
