package com.carrental.model.vehicle;

public class Bus extends Vehicle {
    private int numberOfDoors;
    private Bus() {}
    public int getNumberOfDoors() { return numberOfDoors; }

    public static class Builder {
        private Long id; private String brand; private String model;
        private String description; private double pricePerHour;
        private VehicleStatus status = VehicleStatus.AVAILABLE;
        private int numberOfDoors;

        public Builder id(long id) { this.id = id; return this; }
        public Builder brand(String b) { this.brand = b; return this; }
        public Builder model(String m) { this.model = m; return this; }
        public Builder description(String d) { this.description = d; return this; }
        public Builder pricePerHour(double p) { this.pricePerHour = p; return this; }
        public Builder status(VehicleStatus s) { this.status = s; return this; }
        public Builder numberOfDoors(int n) { this.numberOfDoors = n; return this; }

        public Bus build() {
            if (brand == null || brand.isBlank()) throw new IllegalStateException("Marka jest wymagana");
            Bus b = new Bus();
            b.id = id; b.brand = brand; b.model = model;
            b.description = description; b.pricePerHour = pricePerHour;
            b.status = status; b.numberOfDoors = numberOfDoors;
            return b;
        }
    }
}