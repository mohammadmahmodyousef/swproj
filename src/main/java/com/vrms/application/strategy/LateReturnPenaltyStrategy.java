package com.vrms.application.strategy;

public interface LateReturnPenaltyStrategy {

    double calculatePenalty(long lateDays);
}