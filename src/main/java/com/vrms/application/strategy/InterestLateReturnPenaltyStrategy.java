package com.vrms.application.strategy;

/**
 * Strategy implementation that calculates late return penalties based on a daily
 * base penalty and an additional daily interest percentage.
 */
public class InterestLateReturnPenaltyStrategy implements LateReturnPenaltyStrategy {

    private final double baseDailyPenalty;
    private final double dailyInterestRate;

    /**
     * Constructs an InterestLateReturnPenaltyStrategy.
     *
     * @param baseDailyPenalty the fixed penalty per late day
     * @param dailyInterestRate the additional percentage rate applied per day (e.g., 0.05 for 5%)
     */
    public InterestLateReturnPenaltyStrategy(double baseDailyPenalty, double dailyInterestRate) {
        if (baseDailyPenalty < 0) {
            throw new IllegalArgumentException("Base daily penalty cannot be negative");
        }
        if (dailyInterestRate < 0) {
            throw new IllegalArgumentException("Daily interest rate cannot be negative");
        }

        this.baseDailyPenalty = baseDailyPenalty;
        this.dailyInterestRate = dailyInterestRate;
    }

    @Override
    public double calculatePenalty(long lateDays) {
        if (lateDays < 0) {
            throw new IllegalArgumentException("Late days cannot be negative");
        }
        if (lateDays == 0) {
            return 0.0;
        }

        double basePenaltyTotal = lateDays * baseDailyPenalty;
        double interestMultiplier = 1.0 + (lateDays * dailyInterestRate);

        return basePenaltyTotal * interestMultiplier;
    }

    public double getBaseDailyPenalty() {
        return baseDailyPenalty;
    }

    public double getDailyInterestRate() {
        return dailyInterestRate;
    }
}
