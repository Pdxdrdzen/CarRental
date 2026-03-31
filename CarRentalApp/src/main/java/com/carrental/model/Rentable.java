package com.carrental.model;

import java.time.LocalDateTime;

public interface Rentable {
    double calculateCost(LocalDateTime startDate, LocalDateTime endDate);
    boolean isAvailable();
}