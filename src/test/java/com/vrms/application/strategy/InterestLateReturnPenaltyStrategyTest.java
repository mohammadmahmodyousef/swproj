package com.vrms.application.strategy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class InterestLateReturnPenaltyStrategyTest {

    @Test
    void shouldCalculateInterestBasedPenaltyCorrectly() {
        // Base penalty $20/day, 5% (0.05) daily interest
        InterestLateReturnPenaltyStrategy strategy = new InterestLateReturnPenaltyStrategy(20.0, 0.05);

        // 0 late days -> 0 penalty
        assertEquals(0.0, strategy.calculatePenalty(0), 0.001);

        // 1 late day: base = 1 * 20 = 20, multiplier = 1 + (1 * 0.05) = 1.05 -> 20 * 1.05 = 21.0
        assertEquals(21.0, strategy.calculatePenalty(1), 0.001);

        // 2 late days: base = 2 * 20 = 40, multiplier = 1 + (2 * 0.05) = 1.10 -> 40 * 1.10 = 44.0
        assertEquals(44.0, strategy.calculatePenalty(2), 0.001);
    }

    @Test
    void gettersShouldReturnConstructorValues() {
        InterestLateReturnPenaltyStrategy strategy = new InterestLateReturnPenaltyStrategy(20.0, 0.05);
        assertEquals(20.0, strategy.getBaseDailyPenalty(), 0.001);
        assertEquals(0.05, strategy.getDailyInterestRate(), 0.001);
    }

    @Test
    void shouldRejectNegativeBaseDailyPenalty() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new InterestLateReturnPenaltyStrategy(-10.0, 0.05)
        );
        assertEquals("Base daily penalty cannot be negative", exception.getMessage());
    }

    @Test
    void shouldRejectNegativeDailyInterestRate() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new InterestLateReturnPenaltyStrategy(20.0, -0.05)
        );
        assertEquals("Daily interest rate cannot be negative", exception.getMessage());
    }

    @Test
    void shouldRejectNegativeLateDays() {
        InterestLateReturnPenaltyStrategy strategy = new InterestLateReturnPenaltyStrategy(20.0, 0.05);
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> strategy.calculatePenalty(-1)
        );
        assertEquals("Late days cannot be negative", exception.getMessage());
    }
}
