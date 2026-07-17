package com.vrms.application;

import static org.junit.jupiter.api.Assertions.*;


import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.vrms.domain.Rental;
import com.vrms.persistence.FileRentalRepository;
import com.vrms.persistence.FileVehicleRepository;
import com.vrms.persistence.RentalRepository;
import com.vrms.persistence.VehicleRepository;
import org.junit.jupiter.api.io.TempDir;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;


class RentalManagementFeaturesTest {
    @TempDir
    Path tempDir;

    private Path rentalsFile;
    private RentalRepository rentalRepository;
    private VehicleRepository vehicleRepository;
    private RentalService rentalService;

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
	}

    @BeforeEach
    void setUp() {
        rentalsFile = tempDir.resolve("rentals.txt");
        rentalRepository = new FileRentalRepository(rentalsFile);
        vehicleRepository = new FileVehicleRepository(tempDir.resolve("vehicles.txt"));
        rentalService = new RentalService(rentalRepository,vehicleRepository);
    }

	@AfterEach
	void tearDown() throws Exception {
	}

    @Test
    void getActiveRentalsShouldReturnOnlyActiveRentals() {
        rentalRepository.save(new Rental("R001","V001","Ali","ali@gmail.com",LocalDate.of(2026,7,1),LocalDate.of(2026,7,10),true));
        rentalRepository.save(new Rental("R002","V002","Omar","omar@gmail.com",LocalDate.of(2026,6,1),LocalDate.of(2026,6,5),false));

        List<Rental> activeRentals = rentalService.getActiveRentals();

        assertEquals(1,activeRentals.size());
        assertEquals("R001",activeRentals.get(0).getRentalId());
        assertEquals("Ali",activeRentals.get(0).getCustomerName());
        assertEquals("V001",activeRentals.get(0).getVehicleId());
        assertEquals(LocalDate.of(2026,7,1),activeRentals.get(0).getStartDate());
        assertEquals(LocalDate.of(2026,7,10),activeRentals.get(0).getEndDate());
        assertTrue(activeRentals.get(0).isActive());
    }

    @Test
    void getActiveRentalsShouldReturnEmptyListWhenNoActiveRentalsExist() {
        rentalRepository.save(new Rental("R001","V001","Ali","ali@gmail.com",LocalDate.of(2026,7,1),LocalDate.of(2026,7,10),false));

        List<Rental> activeRentals = rentalService.getActiveRentals();

        assertTrue(activeRentals.isEmpty());
    }

    @Test
    void extendRentalShouldUpdateAndPersistNewEndDate() {
        Rental rental = new Rental("R001","V001","Ali","ali@gmail.com",LocalDate.of(2026,7,1),LocalDate.of(2026,7,10),true);
        rental.setExpiryReminderSent(true);
        rentalRepository.save(rental);

        Rental updatedRental = rentalService.extendRental("R001",LocalDate.of(2026,7,20));

        RentalRepository reloadedRepository = new FileRentalRepository(rentalsFile);
        Rental persistedRental = reloadedRepository.findById("R001");

        assertEquals(LocalDate.of(2026,7,20),updatedRental.getEndDate());
        assertNotNull(persistedRental);
        assertEquals(LocalDate.of(2026,7,20),persistedRental.getEndDate());
        assertFalse(persistedRental.isExpiryReminderSent());
        assertFalse(persistedRental.isExpirationEmailSent());
        assertTrue(persistedRental.isActive());
    }

    @Test
    void extendRentalShouldAcceptExactlyThirtyDays() {
        rentalRepository.save(new Rental("R001","V001","Ali","ali@gmail.com",LocalDate.of(2026,7,1),LocalDate.of(2026,7,10),true));

        Rental rental = rentalService.extendRental("R001",LocalDate.of(2026,7,31));

        assertEquals(LocalDate.of(2026,7,31),rental.getEndDate());
        assertEquals(LocalDate.of(2026,7,31),rentalRepository.findById("R001").getEndDate());
    }

    @Test
    void extendRentalShouldRejectInactiveRental() {
        rentalRepository.save(new Rental("R001","V001","Ali","ali@gmail.com",LocalDate.of(2026,7,1),LocalDate.of(2026,7,10),false));

        IllegalStateException exception = assertThrows(IllegalStateException.class,() -> rentalService.extendRental("R001",LocalDate.of(2026,7,15)));

        assertEquals("Only active rentals can be extended",exception.getMessage());
        assertEquals(LocalDate.of(2026,7,10),rentalRepository.findById("R001").getEndDate());
    }

    @Test
    void extendRentalShouldRejectSameEndDate() {
        rentalRepository.save(new Rental("R001","V001","Ali","ali@gmail.com",LocalDate.of(2026,7,1),LocalDate.of(2026,7,10),true));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,() -> rentalService.extendRental("R001",LocalDate.of(2026,7,10)));

        assertEquals("New end date must be after current end date",exception.getMessage());
    }

    @Test
    void extendRentalShouldRejectEarlierEndDate() {
        rentalRepository.save(new Rental("R001","V001","Ali","ali@gmail.com",LocalDate.of(2026,7,1),LocalDate.of(2026,7,10),true));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,() -> rentalService.extendRental("R001",LocalDate.of(2026,7,9)));

        assertEquals("New end date must be after current end date",exception.getMessage());
    }

    @Test
    void extendRentalShouldRejectTotalDurationOverThirtyDays() {
        rentalRepository.save(new Rental("R001","V001","Ali","ali@gmail.com",LocalDate.of(2026,7,1),LocalDate.of(2026,7,10),true));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,() -> rentalService.extendRental("R001",LocalDate.of(2026,8,1)));

        assertEquals("Rental period cannot exceed 30 days",exception.getMessage());
        assertEquals(LocalDate.of(2026,7,10),rentalRepository.findById("R001").getEndDate());
    }

    @Test
    void extendRentalShouldRejectUnknownRental() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,() -> rentalService.extendRental("UNKNOWN",LocalDate.of(2026,7,20)));

        assertEquals("Rental not found",exception.getMessage());
    }

    @Test
    void extendRentalShouldRejectNullRentalId() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,() -> rentalService.extendRental(null,LocalDate.of(2026,7,20)));

        assertEquals("Rental ID is required",exception.getMessage());
    }

    @Test
    void extendRentalShouldRejectBlankRentalId() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,() -> rentalService.extendRental("   ",LocalDate.of(2026,7,20)));

        assertEquals("Rental ID is required",exception.getMessage());
    }

    @Test
    void extendRentalShouldRejectNullNewEndDate() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,() -> rentalService.extendRental("R001",null));

        assertEquals("New end date is required",exception.getMessage());
    }
    
}
