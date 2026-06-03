package com.carrental.model.vehicle;
import java.time.LocalDateTime;

public interface Rentable {
    double calculateCost(LocalDateTime start, LocalDateTime end);
    boolean isAvailable();
}