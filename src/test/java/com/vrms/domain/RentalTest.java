package com.vrms.domain;

import static org.junit.jupiter.api.Assertions.*;


import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
class RentalTest {

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
    void sevenArgumentConstructorShouldSetFieldsAndDefaultFlags() {
        Rental rental = new Rental("R001","V001","Ali","ali@gmail.com",LocalDate.of(2026,7,10),LocalDate.of(2026,7,15),true);

        assertEquals("R001",rental.getRentalId());
        assertEquals("V001",rental.getVehicleId());
        assertEquals("Ali",rental.getCustomerName());
        assertEquals("ali@gmail.com",rental.getCustomerEmail());
        assertEquals(LocalDate.of(2026,7,10),rental.getStartDate());
        assertEquals(LocalDate.of(2026,7,15),rental.getEndDate());
        assertTrue(rental.isActive());
        assertFalse(rental.isExpiryReminderSent());
        assertFalse(rental.isExpirationEmailSent());
    }

    @Test
    void nineArgumentConstructorShouldSetNotificationFlags() {
        Rental rental = new Rental("R002","V002","Ahmad","ahmad@gmail.com",LocalDate.of(2026,8,1),LocalDate.of(2026,8,5),false,true,true);

        assertFalse(rental.isActive());
        assertTrue(rental.isExpiryReminderSent());
        assertTrue(rental.isExpirationEmailSent());
    }

    @Test
    void settersShouldChangeRentalState() {
        Rental rental = new Rental("R001","V001","Ali","ali@gmail.com",LocalDate.of(2026,7,10),LocalDate.of(2026,7,15),true);

        rental.setActive(false);
        rental.setExpiryReminderSent(true);
        rental.setExpirationEmailSent(true);

        assertFalse(rental.isActive());
        assertTrue(rental.isExpiryReminderSent());
        assertTrue(rental.isExpirationEmailSent());
    }

    @Test
    void toFileLineShouldReturnAllRentalFields() {
        Rental rental = new Rental("R001","V001","Ali","ali@gmail.com",LocalDate.of(2026,7,10),LocalDate.of(2026,7,15),true,true,false);

        assertEquals("R001|V001|Ali|ali@gmail.com|2026-07-10|2026-07-15|true|true|false",rental.toFileLine());
    }

    @Test
    void fromFileLineShouldReadLegacySevenFieldFormat() {
        Rental rental = Rental.fromFileLine("R001|V001|Ali|ali@gmail.com|2026-07-10|2026-07-15|true");

        assertEquals("R001",rental.getRentalId());
        assertEquals("V001",rental.getVehicleId());
        assertEquals("Ali",rental.getCustomerName());
        assertEquals("ali@gmail.com",rental.getCustomerEmail());
        assertEquals(LocalDate.of(2026,7,10),rental.getStartDate());
        assertEquals(LocalDate.of(2026,7,15),rental.getEndDate());
        assertTrue(rental.isActive());
        assertFalse(rental.isExpiryReminderSent());
        assertFalse(rental.isExpirationEmailSent());
    }

    @Test
    void fromFileLineShouldReadNineFieldFormat() {
        Rental rental = Rental.fromFileLine("R002|V002|Mona|mona@gmail.com|2026-08-01|2026-08-09|false|true|true");

        assertEquals("R002",rental.getRentalId());
        assertEquals("V002",rental.getVehicleId());
        assertEquals("Mona",rental.getCustomerName());
        assertEquals("mona@gmail.com",rental.getCustomerEmail());
        assertFalse(rental.isActive());
        assertTrue(rental.isExpiryReminderSent());
        assertTrue(rental.isExpirationEmailSent());
    }

    @Test
    void fromFileLineShouldRejectInvalidFieldCount() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,() -> Rental.fromFileLine("R001|V001|Ali"));
        assertEquals("Invalid rental data",exception.getMessage());
    }

    @Test
    void toStringShouldReturnReadableRentalInformation() {
        Rental rental = new Rental("R001","V001","Ali","ali@gmail.com",LocalDate.of(2026,7,10),LocalDate.of(2026,7,15),true);

        assertEquals("R001 - V001 - Ali - 2026-07-10 - 2026-07-15",rental.toString());
    }

}
