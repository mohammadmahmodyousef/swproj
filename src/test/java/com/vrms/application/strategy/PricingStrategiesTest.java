package com.vrms.application.strategy;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PricingStrategiesTest {

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
    void dailyRentalPricingShouldCalculateCost() {
        DailyRentalPricingStrategy strategy = new DailyRentalPricingStrategy(50);

        assertEquals(50.0,strategy.calculateCost(1),0.001);
        assertEquals(250.0,strategy.calculateCost(5),0.001);
    }

    @Test
    void dailyRentalPricingConstructorShouldRejectZeroRate() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,() -> new DailyRentalPricingStrategy(0));
        assertEquals("Daily rate must be greater than zero",exception.getMessage());
    }

    @Test
    void dailyRentalPricingConstructorShouldRejectNegativeRate() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,() -> new DailyRentalPricingStrategy(-10));
        assertEquals("Daily rate must be greater than zero",exception.getMessage());
    }

    @Test
    void dailyRentalPricingShouldRejectZeroRentalDays() {
        DailyRentalPricingStrategy strategy = new DailyRentalPricingStrategy(50);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,() -> strategy.calculateCost(0));

        assertEquals("Rental days must be greater than zero",exception.getMessage());
    }

    @Test
    void dailyRentalPricingShouldRejectNegativeRentalDays() {
        DailyRentalPricingStrategy strategy = new DailyRentalPricingStrategy(50);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,() -> strategy.calculateCost(-1));

        assertEquals("Rental days must be greater than zero",exception.getMessage());
    }

    @Test
    void lateReturnPenaltyShouldCalculatePenalty() {
        DailyLateReturnPenaltyStrategy strategy = new DailyLateReturnPenaltyStrategy(20);

        assertEquals(0.0,strategy.calculatePenalty(0),0.001);
        assertEquals(20.0,strategy.calculatePenalty(1),0.001);
        assertEquals(60.0,strategy.calculatePenalty(3),0.001);
    }

    @Test
    void penaltyConstructorShouldAcceptZeroPenalty() {
        DailyLateReturnPenaltyStrategy strategy = new DailyLateReturnPenaltyStrategy(0);
        assertEquals(0.0,strategy.calculatePenalty(5),0.001);
    }

    @Test
    void penaltyConstructorShouldRejectNegativePenalty() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,() -> new DailyLateReturnPenaltyStrategy(-1));
        assertEquals("Penalty per day cannot be negative",exception.getMessage());
    }

    @Test
    void penaltyCalculationShouldRejectNegativeLateDays() {
        DailyLateReturnPenaltyStrategy strategy = new DailyLateReturnPenaltyStrategy(20);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,() -> strategy.calculatePenalty(-1));

        assertEquals("Late days cannot be negative",exception.getMessage());
    }
}
