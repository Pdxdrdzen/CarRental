package com.carrental.model.vehicle;

import com.carrental.model.Rentable;
import com.carrental.model.Serviceable;
import java.time.LocalDateTime;
import java.time.Duration;

public abstract class Vehicle implements Rentable, Serviceable {

    private Long id;
    private String brand;
    private String model;
    private String description;
    private double pricePerHour;
    private VehicleStatus status;
    private VehicleType type;

    protected Vehicle(Long id, String brand, String model, String description,
                      double pricePerHour, VehicleStatus status, VehicleType type) {
        this.id = id;
        this.brand = brand;
        this.model = model;
        this.description = description;
        this.pricePerHour = pricePerHour;
        this.status = status;
        this.type = type;
    }

    @Override
    public double calculateCost(LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate == null || endDate == null || !endDate.isAfter(startDate)) {
            throw new IllegalArgumentException("Nieprawidłowe daty rezerwacji");
        }
        long hours = Duration.between(startDate, endDate).toHours();
        if (hours == 0) hours = 1; // minimum 1 godzina
        return hours * pricePerHour;
    }

    @Override
    public boolean isAvailable() {
        return this.status == VehicleStatus.AVAILABLE;
    }

    @Override
    public void sendToService() {
        this.status = VehicleStatus.IN_SERVICE;
    }

    @Override
    public void returnFromService() {
        this.status = VehicleStatus.AVAILABLE;
    }

    // gettery
    public Long getId() { return id; }
    public String getBrand() { return brand; }
    public String getModel() { return model; }
    public String getDescription() { return description; }
    public double getPricePerHour() { return pricePerHour; }
    public VehicleStatus getStatus() { return status; }
    public VehicleType getType() { return type; }

    // settery
    public void setId(Long id) { this.id = id; }
    public void setBrand(String brand) { this.brand = brand; }
    public void setModel(String model) { this.model = model; }
    public void setDescription(String description) { this.description = description; }
    public void setPricePerHour(double pricePerHour) {
        if (pricePerHour < 0) throw new IllegalArgumentException("Cena nie może być ujemna");
        this.pricePerHour = pricePerHour;
    }
    public void setStatus(VehicleStatus status) { this.status = status; }

    @Override
    public String toString() {
        return brand + " " + model + " | " + pricePerHour + " zł/h | " + status;
    }
}