package com.carrental.model.vehicle;

public class Car extends Vehicle {

    private int numberOfSeats;

    private Car(Builder builder) {
        super(builder.id, builder.brand, builder.model, builder.description,
                builder.pricePerHour, builder.status, VehicleType.CAR);
        this.numberOfSeats = builder.numberOfSeats;
    }

    public int getNumberOfSeats() { return numberOfSeats; }
    public void setNumberOfSeats(int numberOfSeats) { this.numberOfSeats = numberOfSeats; }

    public static class Builder {
        private Long id;
        private String brand;
        private String model;
        private String description;
        private double pricePerHour;
        private VehicleStatus status = VehicleStatus.AVAILABLE;
        private int numberOfSeats;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder brand(String brand) { this.brand = brand; return this; }
        public Builder model(String model) { this.model = model; return this; }
        public Builder description(String description) { this.description = description; return this; }
        public Builder pricePerHour(double pricePerHour) { this.pricePerHour = pricePerHour; return this; }
        public Builder status(VehicleStatus status) { this.status = status; return this; }
        public Builder numberOfSeats(int numberOfSeats) { this.numberOfSeats = numberOfSeats; return this; }

        public Car build() {
            if (brand == null || model == null) throw new IllegalStateException("Marka i model są wymagane");
            return new Car(this);
        }
    }
}