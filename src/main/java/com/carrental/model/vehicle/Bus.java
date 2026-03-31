package com.carrental.model.vehicle;

public class Bus extends Vehicle {

    private int passengerCapacity;

    public Bus(Long id, String brand, String model, String description,
               double pricePerHour, VehicleStatus status, int passengerCapacity) {
        super(id, brand, model, description, pricePerHour, status, VehicleType.BUS);
        this.passengerCapacity = passengerCapacity;
    }

    public int getPassengerCapacity() { return passengerCapacity; }
    public void setPassengerCapacity(int passengerCapacity) { this.passengerCapacity = passengerCapacity; }
}

