package com.carrental.reservation;

import com.carrental.model.user.Client;
import com.carrental.model.vehicle.Vehicle;
import java.time.LocalDateTime;

public class Reservation {

    private Long id;
    private Client client;
    private Vehicle vehicle;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private ReservationStatus status;
    private double totalCost;

    private Reservation(Builder builder) {
        this.id = builder.id;
        this.client = builder.client;
        this.vehicle = builder.vehicle;
        this.startDate = builder.startDate;
        this.endDate = builder.endDate;
        this.status = builder.status;
        this.totalCost = vehicle.calculateCost(startDate, endDate);
    }

    public Long getId() { return id; }
    public Client getClient() { return client; }
    public Vehicle getVehicle() { return vehicle; }
    public LocalDateTime getStartDate() { return startDate; }
    public LocalDateTime getEndDate() { return endDate; }
    public ReservationStatus getStatus() { return status; }
    public double getTotalCost() { return totalCost; }
    public void setStatus(ReservationStatus status) { this.status = status; }

    public static class Builder {
        private Long id;
        private Client client;
        private Vehicle vehicle;
        private LocalDateTime startDate;
        private LocalDateTime endDate;
        private ReservationStatus status = ReservationStatus.PENDING;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder client(Client client) { this.client = client; return this; }
        public Builder vehicle(Vehicle vehicle) { this.vehicle = vehicle; return this; }
        public Builder startDate(LocalDateTime startDate) { this.startDate = startDate; return this; }
        public Builder endDate(LocalDateTime endDate) { this.endDate = endDate; return this; }
        public Builder status(ReservationStatus status) { this.status = status; return this; }

        public Reservation build() {
            if (client == null || vehicle == null || startDate == null || endDate == null)
                throw new IllegalStateException("Klient, pojazd i daty są wymagane");
            if (!vehicle.isAvailable())
                throw new IllegalStateException("Pojazd jest niedostępny");
            return new Reservation(this);
        }
    }
}