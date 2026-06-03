package com.carrental.model.reservation;
import com.carrental.model.user.Client;
import com.carrental.model.vehicle.Vehicle;
import java.time.LocalDateTime;

public class Reservation {
    private Long id; private Client client; private Vehicle vehicle;
    private LocalDateTime startDate, endDate;
    private double totalCost; private ReservationStatus status;

    private Reservation() {}

    public Long getId() { return id; }
    public Client getClient() { return client; }
    public Vehicle getVehicle() { return vehicle; }
    public LocalDateTime getStartDate() { return startDate; }
    public LocalDateTime getEndDate() { return endDate; }
    public double getTotalCost() { return totalCost; }
    public ReservationStatus getStatus() { return status; }

    public static class Builder {
        private Long id; private Client client; private Vehicle vehicle;
        private LocalDateTime startDate, endDate;

        public Builder id(long id) { this.id = id; return this; }
        public Builder client(Client c) { this.client = c; return this; }
        public Builder vehicle(Vehicle v) { this.vehicle = v; return this; }
        public Builder startDate(LocalDateTime d) { this.startDate = d; return this; }
        public Builder endDate(LocalDateTime d) { this.endDate = d; return this; }

        public Reservation build() {
            if (client == null) throw new IllegalStateException("Klient jest wymagany");
            if (vehicle == null) throw new IllegalStateException("Pojazd jest wymagany");
            if (startDate == null || endDate == null) throw new IllegalStateException("Daty są wymagane");
            if (!endDate.isAfter(startDate)) throw new IllegalArgumentException("Data końcowa musi być późniejsza");
            if (!vehicle.isAvailable()) throw new IllegalStateException("Pojazd jest niedostępny");

            Reservation r = new Reservation();
            r.id = id; r.client = client; r.vehicle = vehicle;
            r.startDate = startDate; r.endDate = endDate;
            r.totalCost = vehicle.calculateCost(startDate, endDate);
            r.status = ReservationStatus.PENDING;
            return r;
        }
    }
}