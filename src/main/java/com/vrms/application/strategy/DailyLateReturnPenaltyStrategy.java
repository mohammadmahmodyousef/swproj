package com.vrms.application.strategy;

public class DailyLateReturnPenaltyStrategy implements LateReturnPenaltyStrategy {

    private final double penaltyPerDay;

    public DailyLateReturnPenaltyStrategy(double penaltyPerDay) {
        if (penaltyPerDay < 0) {
            throw new IllegalArgumentException("Penalty per day cannot be negative");
        }

        this.penaltyPerDay = penaltyPerDay;
    }

    @Override
    public double calculatePenalty(long lateDays) {
        if (lateDays < 0) {
            throw new IllegalArgumentException("Late days cannot be negative");
        }

        return lateDays * penaltyPerDay;
    }
}