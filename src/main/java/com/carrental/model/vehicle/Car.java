package com.carrental.model.vehicle;

public class Car extends Vehicle {
    private int numberOfSeats;
    private Car() {}
    public int getNumberOfSeats() { return numberOfSeats; }

    public static class Builder {
        private Long id; private String brand; private String model;
        private String description; private double pricePerHour;
        private VehicleStatus status = VehicleStatus.AVAILABLE;
        private int numberOfSeats;

        public Builder id(long id) { this.id = id; return this; }
        public Builder brand(String b) { this.brand = b; return this; }
        public Builder model(String m) { this.model = m; return this; }
        public Builder description(String d) { this.description = d; return this; }
        public Builder pricePerHour(double p) { this.pricePerHour = p; return this; }
        public Builder status(VehicleStatus s) { this.status = s; return this; }
        public Builder numberOfSeats(int n) { this.numberOfSeats = n; return this; }

        public Car build() {
            if (brand == null || brand.isBlank()) throw new IllegalStateException("Marka jest wymagana");
            if (model == null || model.isBlank()) throw new IllegalStateException("Model jest wymagany");
            Car c = new Car();
            c.id = id; c.brand = brand; c.model = model;
            c.description = description; c.pricePerHour = pricePerHour;
            c.status = status; c.numberOfSeats = numberOfSeats;
            return c;
        }
    }
}