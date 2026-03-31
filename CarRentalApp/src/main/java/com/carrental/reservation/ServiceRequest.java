package com.carrental.reservation;

import com.carrental.model.vehicle.Vehicle;
import java.time.LocalDateTime;

public class ServiceRequest {

    public enum RequestStatus { OPEN, IN_PROGRESS, RESOLVED }

    private Long id;
    private Vehicle vehicle;
    private String description;
    private RequestStatus status;
    private LocalDateTime reportedAt;

    public ServiceRequest(Long id, Vehicle vehicle, String description) {
        if (vehicle == null || description == null || description.isBlank())
            throw new IllegalArgumentException("Pojazd i opis usterki są wymagane");
        this.id = id;
        this.vehicle = vehicle;
        this.description = description;
        this.status = RequestStatus.OPEN;
        this.reportedAt = LocalDateTime.now();
        vehicle.sendToService(); // zmiana statusu pojazdu
    }

    public void resolve() {
        this.status = RequestStatus.RESOLVED;
        this.vehicle.returnFromService();
    }

    public Long getId() { return id; }
    public Vehicle getVehicle() { return vehicle; }
    public String getDescription() { return description; }
    public RequestStatus getStatus() { return status; }
    public LocalDateTime getReportedAt() { return reportedAt; }
    public void setStatus(RequestStatus status) { this.status = status; }
}