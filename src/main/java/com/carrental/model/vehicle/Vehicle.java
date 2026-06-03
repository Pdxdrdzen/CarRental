package com.carrental.model.vehicle;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public abstract class Vehicle implements Rentable, Serviceable {
    protected Long id;
    protected String brand;
    protected String model;
    protected String description;
    protected double pricePerHour;
    protected VehicleStatus status;

    @Override
    public double calculateCost(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null || !end.isAfter(start))
            throw new IllegalArgumentException("Data końcowa musi być późniejsza niż startowa");
        long hours = ChronoUnit.HOURS.between(start, end);
        if (hours == 0) hours = 1;
        return hours * pricePerHour;
    }

    @Override public boolean isAvailable() { return VehicleStatus.AVAILABLE.equals(status); }
    @Override public void sendToService() { this.status = VehicleStatus.IN_SERVICE; }
    @Override public void returnFromService() { this.status = VehicleStatus.AVAILABLE; }

    public Long getId() { return id; }
    public String getBrand() { return brand; }
    public String getModel() { return model; }
    public String getDescription() { return description; }
    public double getPricePerHour() { return pricePerHour; }
    public void setPricePerHour(double p) {
        if (p < 0) throw new IllegalArgumentException("Cena nie może być ujemna");
        this.pricePerHour = p;
    }
    public VehicleStatus getStatus() { return status; }
    public void setStatus(VehicleStatus status) { this.status = status; }
}